package com.example.talksoroddho;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button createAcButton;
    private EditText Useremail, Userpass, conpass;
    private TextView login, wealPassMsg;
    private ProgressDialog loadingBar;
    private DatabaseReference rootref;
    private boolean exit=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rootref = FirebaseDatabase.getInstance().getReference();
        Initialize();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToLoginPage();
            }
        });
        createAcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

    }

    private void SendToLoginPage()
    {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void Initialize()
    {

        createAcButton = (Button) findViewById(R.id.createAc_button);
        Useremail = (EditText) findViewById(R.id.new_email);
        Userpass = (EditText) findViewById(R.id.new_password);
        conpass = (EditText) findViewById(R.id.confirm_password);
        login = (TextView) findViewById(R.id.login_link);
        wealPassMsg = (TextView) findViewById(R.id.weak_pass_msg);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
    }


    private void createNewAccount()
    {
        String email = Useremail.getText().toString();
        String pass = Userpass.getText().toString();
        String cpass = conpass.getText().toString();
        int pstren = calculatePasswordStrength(pass);


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cpass))
        {
            Toast.makeText(this, "Please Retype Password", Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equals(cpass))
        {
            Toast.makeText(this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
        }
        else if(pstren != 10)
        {
            wealPassMsg.setTextColor(Color.parseColor("#F06464"));
            Toast.makeText(this, "Password Requirements Not Met!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            wealPassMsg.setTextColor(Color.parseColor("#00E5FF"));

            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.setTitle("Creating New Account!");
            loadingBar.setMessage("Please wait while account is being created");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    String emailString = mAuth.getCurrentUser().getEmail();
                                                    String split[] = emailString.split("@");
                                                    String userID = mAuth.getCurrentUser().getUid();
                                                    Map<String, String> value = new HashMap<>();
                                                    value.put("name", split[0]);
                                                    rootref.child("Unregistered").child(userID).setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            SendToLoginPage();
                                                            Toast.makeText(RegisterActivity.this, "Account Created Successfully. Check email for verification link", Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();
                                                        }
                                                    });


                                                }else
                                                {
                                                    Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                String ermsg = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, ermsg, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
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
