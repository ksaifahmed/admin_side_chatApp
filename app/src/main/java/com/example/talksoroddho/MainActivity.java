package com.example.talksoroddho;


import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Iterator;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private Animation zoomout;
    private Button chatbtn;
    private boolean exit = false;
    private DatabaseReference rootref;
    private ProgressBar progressBar;
    private TextView leaveMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();



        InitializeClient();

        rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference connectionRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected && currentUser!=null)
                {
                    rootref.child("Unregistered").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("name").exists())
                            {
                                progressBar.setVisibility(View.GONE);
                                chatbtn.setText("Awaiting\nPeer\nApproval");
                                chatbtn.setVisibility(View.VISIBLE);
                                Toast.makeText(MainActivity.this, "A peer must approve before you can start to chat", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                progressBar.setVisibility(View.GONE);
                                chatbtn.setText("Start\nTalking");
                                leaveMessage.setText("Leave a message and we'll get back to you");
                                chatbtn.setVisibility(View.VISIBLE);
                                chatbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SendToMessageActivity();
                                    }
                                });
                                rootref.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child("name").exists())
                                        {
                                            String devtk = FirebaseInstanceId.getInstance().getToken();
                                            rootref.child("Users").child(currentUser.getUid()).child("deviceToken").setValue(devtk);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void InitializeClient()
    {


        leaveMessage = findViewById(R.id.nicher_text);
        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        progressBar = findViewById(R.id.progressBarx);
        progressBar.setVisibility(View.VISIBLE);


        chatbtn = findViewById(R.id.gotoChatButton);
        chatbtn.setVisibility(View.GONE);
        chatbtn.setText("");
        zoomout = AnimationUtils.loadAnimation(this, R.anim.zoomout);
        chatbtn.setAnimation(zoomout);







        zoomout.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                chatbtn.startAnimation(zoomout);

            }
        });





    }



    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null)
        {
            mAuth.signOut();
            sendUserToLoginActivity();
            return;
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference connectionRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                connectionRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean offline = dataSnapshot.getValue(Boolean.class);
                        if(!offline)
                        {
                            Log.d(":)","ekbar ki \t\t\tdui????");
                            progressBar.setVisibility(View.GONE);
                            chatbtn.setText("NO\nCONNECTION");
                            chatbtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }, 5000);




    }

    private void sendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_option)
        {
            mAuth.signOut();
            sendUserToLoginActivity();
        }

        if(item.getItemId() == R.id.main_settings_option)
        {
            SendToSettings();
        }
        if(item.getItemId() == R.id.about_developer_option)
        {
            SendToAboutPage();
        }
        return true;
    }




    private void SendToMessageActivity()
    {
        Intent messageInt = new Intent(MainActivity.this, MessageActivity.class);
        startActivity(messageInt);
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


    private void SendToSettings()
    {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void SendToAboutPage()
    {

    }

}













