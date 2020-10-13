package com.example.talksoroddho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPass, newPassword, confirmPass;
    private Button button;
    private FirebaseUser currentUser;
    private TextView changePassText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.changepass_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Initialize();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePassword();
            }
        });
    }



    private void Initialize()
    {
        currentPass = findViewById(R.id.changepass_oldpass);
        newPassword = findViewById(R.id.changepass_newpass);
        confirmPass = findViewById(R.id.changepass_confirmpass);
        button = findViewById(R.id.updatepassbutton);
        changePassText = findViewById(R.id.changepass_text);
    }

    private void UpdatePassword()
    {
        if(currentUser==null)
        {
            Toast.makeText(this, "Log Out and Try Again", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldp = currentPass.getText().toString();
        String newp = newPassword.getText().toString();
        String confirmp = confirmPass.getText().toString();
        int pstren = calculatePasswordStrength(newp);


        if(TextUtils.isEmpty(oldp))
        {
            Toast.makeText(this, "Current Password Not Provided", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(newp))
        {
            Toast.makeText(this, "New Password Not Provided", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmp))
        {
            Toast.makeText(this, "Confirm Password Not Provided", Toast.LENGTH_SHORT).show();
        }
        else if(!newp.equals(confirmp))
        {
            Toast.makeText(this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
        }
        else if(pstren != 10)
        {
            changePassText.setTextColor(Color.parseColor("#F06464"));
            Toast.makeText(this, "Password requirements not met", Toast.LENGTH_SHORT).show();
        }
        else
        {
            changePassText.setTextColor(Color.parseColor("#00E676"));
            final ProgressDialog loadingBar = new ProgressDialog(this);
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.setTitle("Changing Password");
            loadingBar.setMessage("Please wait!");
            loadingBar.show();


            final String newPass = newp;
            final String oldpass = oldp;
            final String email = currentUser.getEmail();

            AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        currentUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                {
                                    loadingBar.dismiss();
                                    Toast.makeText(ChangePasswordActivity.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    loadingBar.dismiss();
                                    Toast.makeText(ChangePasswordActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                    GoToSettings();
                                }
                            }
                        });
                    }
                    else {
                        loadingBar.dismiss();
                        Toast.makeText(ChangePasswordActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }



    }


    private int calculatePasswordStrength(String password){

        int iPasswordScore = 0;
        if( password.length() < 8 )
            return 0;
        else
            iPasswordScore += 2;
        if( password.matches("(?=.*[0-9]).*") )
            iPasswordScore += 2;
        if( password.matches("(?=.*[a-z]).*") )
            iPasswordScore += 2;
        if( password.matches("(?=.*[A-Z]).*") )
            iPasswordScore += 2;
        if( password.matches("(?=.*[!\"#$%&'*+,-./:;<=>?@^_`{|}~-]).*") )
            iPasswordScore += 2;

        return iPasswordScore;
    }

    private void GoToSettings()
    {
        Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
