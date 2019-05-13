package com.pro.android.justyle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private Button mRegisterButton;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mConfirmPassword;
    private TextView mTextViewSignIn;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), FrontPageActivity.class));
        }


        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mTextViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        mRegisterButton = (Button) findViewById(R.id.RegisterButton);
        mConfirmPassword = (EditText) findViewById(R.id.confirmPassword);
        mProgressDialog = new ProgressDialog(this);

        mTextViewSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                registerUser();
            }
        });
    }
    private void registerUser(){
        String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
        String passwordConfirmed = mConfirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter Email, please", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter Password, please",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirmed)){
            Toast.makeText(this, "Password Confirmation must match password", Toast.LENGTH_SHORT).show();
            return;
        }


        //if validation is ok you will end up here
        mProgressDialog.setMessage("Registering User, Please Wait...");
        mProgressDialog.show();

        mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                                finish();
                                startActivity(new Intent(getApplicationContext(),FrontPageActivity.class));
                            Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SignUpActivity.this, "Registration failed",Toast.LENGTH_SHORT).show();
                        }
                        mProgressDialog.dismiss();
                    }
                });

    }

}