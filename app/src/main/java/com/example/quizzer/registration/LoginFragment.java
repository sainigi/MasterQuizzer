package com.example.quizzer.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizzer.MainActivity;
import com.example.quizzer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.quizzer.registration.CreateAccountFragment.VALID_EMAIL_ADDRESS_REGEX;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private EditText emailOrPhone,password;
    private Button loginBtn;
    private ProgressBar progressBar;
    private TextView createAccountText,forgotpasswordText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                emailOrPhone.setError(null);
                password.setError(null);

                if (emailOrPhone.getText().toString().isEmpty()){
                    emailOrPhone.setError("Email or Phone is Required");
                    return;
                }
                if (password.getText().toString().isEmpty()){
                    password.setError("Password is Required");
                    return;
                }
                if( VALID_EMAIL_ADDRESS_REGEX.matcher(emailOrPhone.getText().toString()).find()){
                    progressBar.setVisibility(View.VISIBLE);
                    login(emailOrPhone.getText().toString());
                }else if (emailOrPhone.getText().toString().matches("\\d{10}")){
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("phone",emailOrPhone.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                List<DocumentSnapshot> document = task.getResult().getDocuments();
                                if (document.isEmpty()){
                                    emailOrPhone.setError("phone no. not found");
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return;
                                }else {
                                    String email = document.get(0).get("email").toString();
                                    login(email);
                                }

                            }else {
                                String error = task.getException().getMessage();
                                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }else {
                    emailOrPhone.setError("Please Enter a Valid Email or Phone No.");
                }
            }
        });

        createAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity)getActivity()).setFragment(new CreateAccountFragment());
            }
        });

        forgotpasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity)getActivity()).setFragment(new ForgotPasswordFragment());
            }
        });
    }

    private void init(View view){
        emailOrPhone = view.findViewById(R.id.LIemail);
        password = view.findViewById(R.id.LIpassword);
        progressBar = view.findViewById(R.id.LIprogressBar);
        forgotpasswordText = view.findViewById(R.id.forgotPassword);
        loginBtn = view.findViewById(R.id.loginBtn);
        createAccountText = view.findViewById(R.id.LiloginText);
    }

    private void login(String email){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        loginBtn.setEnabled(false);
        firebaseAuth.signInWithEmailAndPassword(email,password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent mainIntent = new Intent(getContext(), MainActivity.class);
                    startActivity(mainIntent);
                    getActivity().finish();
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                loginBtn.setEnabled(true);
            }
        });
    }
}