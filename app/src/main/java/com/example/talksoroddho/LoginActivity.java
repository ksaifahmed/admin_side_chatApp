package com.example.talksoroddho;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
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


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button loginButton;
    private EditText email, pass;
    private TextView forgetPass, signUp;
    private ProgressDialog loadingBar;
    private boolean exit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Initialize();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendToRegisterPage();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordReset();
            }
        });

    }

    private void PasswordReset()
    {
        Intent passreset = new Intent(LoginActivity.this, PasswordResetActivity.class);
        passreset.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(passreset);
        finish();
    }

    private void SendToRegisterPage()
    {
        Intent RegIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        RegIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(RegIntent);
        finish();
    }

    private void Login()
    {
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.setTitle("Authenticating");
        loadingBar.setMessage("Please wait!");
        loadingBar.show();

        String Email = email.getText().toString();
        String Password = pass.getText().toString();

        if(TextUtils.isEmpty(Email))
        {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Password))
        {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                if(mAuth.getCurrentUser().isEmailVerified())
                                {
                                    SendToMainActivity();
                                    Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, "Email needs to be verified first!", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }

                            }else
                            {
                                String errorMsg = task.getException().toString();
                                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });



        }
    }

    private void SendToMainActivity()
    {
        Intent mainActIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActIntent);
        finish();
    }

    private void Initialize()
    {
        loginButton = (Button) findViewById(R.id.login_button);
        email = (EditText) findViewById(R.id.login_email);
        pass = (EditText) findViewById(R.id.login_password);
        forgetPass = (TextView) findViewById(R.id.forgot_password);
        signUp = (TextView) findViewById(R.id.sign_up);
        loadingBar = new ProgressDialog(this);
    }


    @Override
    public void onBackPressed() {
        if(exit)
        {
            super.onBackPressed();
            return;
        }
        exit = true;
        Toast.makeText(this, "Press Again to Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
    }


}

///forget password needs to be done!
