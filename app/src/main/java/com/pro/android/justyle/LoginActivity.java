package com.pro.android.justyle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button mButtonSignIn;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private TextView mTextViewSignUp;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mTextViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        mButtonSignIn = (Button) findViewById(R.id.Login);


        if (mFirebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),FrontPageActivity.class));
        }
        mButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        mTextViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

    }
    private void userLogin(){
        String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter Email, please", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter Password, please",Toast.LENGTH_SHORT).show();
            return;
        }
        //if validation is ok you will end up here
        mProgressDialog.setMessage("Logging in, Please Wait...");
        mProgressDialog.show();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),FrontPageActivity.class));
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"wrong password or Email",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


}