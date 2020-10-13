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
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        TextView goback = findViewById(R.id.go_back_to_login);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToLogin();
            }
        });

        final EditText email = findViewById(R.id.reset_email);
        Button reset = findViewById(R.id.reset_pass_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(email.getText().toString()))
                {
                    Toast.makeText(PasswordResetActivity.this, "Email not provided", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String stringEmail = email.getText().toString();
                    final ProgressDialog progress = new ProgressDialog(PasswordResetActivity.this);
                    progress.setTitle("Please Wait");
                    progress.setMessage("Sending a password reset email");
                    progress.setCanceledOnTouchOutside(true);
                    progress.show();

                    FirebaseAuth.getInstance().sendPasswordResetEmail(stringEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progress.dismiss();
                                Toast.makeText(PasswordResetActivity.this, "Check Your Email for Password Reset Link", Toast.LENGTH_LONG).show();
                                GoToLogin();
                            }
                            else
                            {
                                progress.dismiss();
                                Toast.makeText(PasswordResetActivity.this, "Something Went Wrong. Please Try Again Later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void GoToLogin()
    {
        Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
