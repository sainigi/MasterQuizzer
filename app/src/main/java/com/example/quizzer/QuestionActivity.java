package com.example.quizzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    public static final String FILE_NAME = "QUIZZER";
    public static final String KEY_NAME = "QUESTIONS";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private TextView question,noIndicator;
    private FloatingActionButton bkmrkBtn;
    private LinearLayout optionContainer;
    private Button shareBtn,nextBtn;
    private int count =0;
    private  List<QuestionModel> list;
    private int pos = 0;
    private int score = 0;
    private String category;
    private int setNo;
    private int matchedquestionpostion;

    private Dialog loadingDialog;

    private List<QuestionModel> bookmarksList;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Toolbar toolbar = findViewById(R.id.question_toolbar);
        setSupportActionBar(toolbar);

        question = findViewById(R.id.question_TextView);
        noIndicator = findViewById(R.id.no_indicator);
        bkmrkBtn = findViewById(R.id.bookmarkIconBtn);
        optionContainer = findViewById(R.id.option_container);
        shareBtn = findViewById(R.id.share_btn);
        nextBtn = findViewById(R.id.next_btn);

        list = new ArrayList<>();

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();

        bkmrkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatch()){
                    bookmarksList.remove(matchedquestionpostion);
                    bkmrkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                }else {
                    bookmarksList.add(list.get(pos));
                    bkmrkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });

        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setNo",1);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        loadingDialog.show();
        myRef.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    list.add(snapshot.getValue(QuestionModel.class));
                }
                if (list.size() > 0){
                    for (int i=0;i<4;i++){
                        optionContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkAnswer(((Button)v));
                            }
                        });
                    }
                    playAnim(question,0,list.get(pos).getQuestion());

                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.77f);
                            pos++;
                            enableOption(true);
                            if (pos == list.size()){
                                //Score Activity
                                Intent scoreIntent = new Intent(QuestionActivity.this,ScoreActivity.class);
                                scoreIntent.putExtra("score",score);
                                scoreIntent.putExtra("total",list.size());
                                startActivity(scoreIntent);
                                finish();
                                return;
                            }
                            count = 0;
                            playAnim(question,0,list.get(pos).getQuestion());
                        }
                    });
                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String body = "MasterQuizz Question" + "\n" +
                                    "Ques- "+list.get(pos).getQuestion() + "\n" +
                                    "a) " + list.get(pos).getOptionA() + "\n" +
                                    "b) " + list.get(pos).getOptionB() + "\n" +
                                    "c) " +list.get(pos).getOptionC() + "\n" +
                                    "d) " +list.get(pos).getOptionD();
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Demo Quizz Question");
                            shareIntent.putExtra(Intent.EXTRA_TEXT,body);
                            startActivity(Intent.createChooser(shareIntent,"Share via"));
                        }
                    });
                }else {
                    finish();
                    Toast.makeText(QuestionActivity.this,"no questions",Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(QuestionActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void playAnim(final View view, final int value, final String data){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
        .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (value == 0 && count <  4 ){
                    String option="";
                    if (count == 0){
                        option = list.get(pos).getOptionA();
                    }else if (count == 1){
                        option = list.get(pos).getOptionB();
                    }else if (count == 2){
                        option = list.get(pos).getOptionC();
                    }else if (count == 3) {
                        option = list.get(pos).getOptionD();
                    }
                    playAnim(optionContainer.getChildAt(count),0,option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (value == 0){
                    try {
                        ((TextView)view).setText(data);
                        noIndicator.setText(pos+1+"/"+list.size());
                        if (modelMatch()){
                            bkmrkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                        }else {
                            bkmrkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                        }
                    }catch (ClassCastException ex){
                        ((Button)view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view,1,data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void checkAnswer(Button selectedOption){
        enableOption(false);
        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);
        if (selectedOption.getText().toString().equals(list.get(pos).getCorrectOption())){
            //correct
            score++;
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#02f533")));
        }else {
            //incorrect
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            Button correctAns = (Button) optionContainer.findViewWithTag(list.get(pos).getCorrectOption());
            correctAns.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#02f533")));

        }

    }

    private void enableOption(boolean enable){
        for (int i=0;i<4;i++){
            optionContainer.getChildAt(i).setEnabled(enable);
            if (enable){
                optionContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4A00E0")));
            }
        }
    }

    private void getBookmarks(){
        String json = preferences.getString(KEY_NAME,"");

        Type type = new TypeToken<List<QuestionModel>>(){}.getType();

        bookmarksList = gson.fromJson(json,type);

        if (bookmarksList == null){
            bookmarksList = new ArrayList<>();
        }
    }

    private boolean modelMatch(){
        boolean matched = false;
        int i =0;
        for (QuestionModel model : bookmarksList){
            if(model.getQuestion().equals(list.get(pos).getQuestion())
            && model.getCorrectOption().equals(list.get(pos).getCorrectOption())
            && model.getSetNo() == list.get(pos).getSetNo()){
                matched = true;
                matchedquestionpostion =i;
            }
            i++;
        }
        return matched;
    }

    private  void storeBookmarks(){
        String json = gson.toJson(bookmarksList);

        editor.putString(KEY_NAME,json);
        editor.commit();
    }
}