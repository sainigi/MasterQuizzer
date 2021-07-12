package com.example.quizzer.registration;

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

import com.example.quizzer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAccountFragment newInstance(String param1, String param2) {
        CreateAccountFragment fragment = new CreateAccountFragment();
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
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private EditText email,phone,password,confirmPassword;
    private ProgressBar progressBar;
    private Button createAccountBtn;
    private TextView loginText;

    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        firebaseAuth = FirebaseAuth.getInstance();

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity)getActivity()).setFragment(new LoginFragment());
            }
        });

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setError(null);
                phone.setError(null);
                password.setError(null);
                confirmPassword .setError(null);
                if(email.getText().toString().isEmpty()){
                    email.setError("Required Email.");
                    return;
                }
                if(phone.getText().toString().isEmpty()){
                    phone.setError("Required Email.");
                    return;
                }
                if(password.getText().toString().isEmpty()){
                    password.setError("Required Email.");
                    return;
                }
                if(confirmPassword.getText().toString().isEmpty()){
                    confirmPassword.setError("Required Email.");
                    return;
                }
                if( !VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString()).find()){
                    email.setError("Please Enter a Valid Email");
                    return;
                }
                if (phone.getText().toString().length() != 10){
                    phone.setError("Please Enter a Valid Phone no.");
                    return;
                }
                if (!password.getText().toString().equals(confirmPassword.getText().toString())){
                    confirmPassword.setError("Password MisMatch");
                    return;
                }

                createAccount();
            }
        });
    }

    private  void init(View view){
        email = view.findViewById(R.id.CAemail);
        phone = view.findViewById(R.id.CAphone);
        password = view.findViewById(R.id.CApassword);
        confirmPassword = view.findViewById(R.id.CAconfirmPassword);
        createAccountBtn = view.findViewById(R.id.createAccountBTn);
        progressBar = view.findViewById(R.id.CAprogressBar);
        loginText = view.findViewById(R.id.CAloginText);
    }

    private void createAccount(){
        progressBar.setVisibility(View.VISIBLE);
        createAccountBtn.setEnabled(false);
        firebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getSignInMethods().isEmpty()){
                        ((RegisterActivity)getActivity()).setFragment(new OtpFragment(email.getText().toString(),phone.getText().toString(),password.getText().toString()));
                    }else {
                        email.setError("Email Already Exist.");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }else {
                    Toast.makeText(getContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
                createAccountBtn.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}