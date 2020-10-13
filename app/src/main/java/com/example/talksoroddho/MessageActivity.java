package com.example.talksoroddho;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {


    private String messageSenderID;

    private Toolbar toolbar;
    private ImageButton sendbutton;
    private EditText messageInputText;
    private RecyclerView userMessageList;


    private FirebaseAuth mAuth;
    private DatabaseReference rootref;

    private final List<Messages> messagesList = new ArrayList<>();
    private final List<Long> timeList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private Long lastTimeStamp;
    private boolean first = true, doneloadin = false;
    private boolean firstmes = false;

    private SwipeRefreshLayout pullRefresh;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();



        Initialize();
        LoadMessages(20);

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendMessage();
            }
        });
        pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Loadmore(20);
                pullRefresh.setRefreshing(false);
            }
        });
    }


    private void LoadMessages(int scrollnum)
    {

        rootref.child("Messages").child(messageSenderID).limitToLast(scrollnum).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

                Messages messages = dataSnapshot.getValue(Messages.class);
                Long time = (Long) dataSnapshot.child("time").getValue();
                if(first)
                {
                    first = false;
                    lastTimeStamp = (Long) dataSnapshot.child("time").getValue();
                }
                messagesList.add(messages);
                timeList.add(time);
                messageAdapter.notifyDataSetChanged();

                userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void Loadmore(int scrollnum)
    {
        DatabaseReference ref = rootref.child("Messages").child(messageSenderID);
        ValueEventListener listener = ref.orderByChild("time").limitToLast(scrollnum).endAt(lastTimeStamp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Messages> tmp = new ArrayList<>();
                List<Long> tmp2 = new ArrayList<>();
                first = true;
                firstmes = true;
                for(DataSnapshot data: dataSnapshot.getChildren())
                {

                    Messages messages = data.getValue(Messages.class);
                    Long time = (Long) data.child("time").getValue();
                    if(first)
                    {
                        first = false;
                        lastTimeStamp = (Long) data.child("time").getValue();
                    }
                    tmp.add(0, messages);
                    tmp2.add(0, time);
                }

                for(int i=0; i<tmp.size(); i++)
                {
                    if(firstmes)
                    {
                        firstmes = false;
                        continue;
                    }
                    messagesList.add(0, tmp.get(i));
                    timeList.add(0, tmp2.get(i));
                    messageAdapter.notifyDataSetChanged();
                    ///userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void Initialize()
    {
        toolbar = findViewById(R.id.messageToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        messageInputText = findViewById(R.id.input_field);
        sendbutton = findViewById(R.id.sendbutton);

        messageAdapter = new MessageAdapter(messagesList, timeList);
        userMessageList = findViewById(R.id.messages_list);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);

        pullRefresh = findViewById(R.id.refresh_layout);
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    private void SendMessage()
    {

        final String messageText = messageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText)) return;

        String messageSenderRef = "Messages/" + messageSenderID;

        DatabaseReference userMessageKeyRef = rootref.child("Messages").child(messageSenderID).push();

        final String messagePushID = userMessageKeyRef.getKey();



        Map messageTextBody = new HashMap();
        messageTextBody.put("message", messageText);
        messageTextBody.put("type", "text");
        messageTextBody.put("from", messageSenderID);
        messageTextBody.put("time", ServerValue.TIMESTAMP);

        Map messageBodyDetails = new HashMap();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);


        rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {


                    rootref.child("Users Assigned").child(messageSenderID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("peer").exists())
                            {
                                final String peerID = dataSnapshot.child("peer").getValue().toString();
                                final DatabaseReference contactsRef = rootref.child("Contacts").child(peerID).child(messageSenderID);

                                Map values = new HashMap();
                                values.put("message", messageText);
                                values.put("from", messageSenderID);
                                values.put("time", ServerValue.TIMESTAMP);
                                values.put("seen", "false");

                                contactsRef.updateChildren(values).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful())
                                        {
                                            contactsRef.child("time").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists())
                                                    {
                                                        String timeString = dataSnapshot.getValue().toString();
                                                        if(!timeString.contains("-"))
                                                        {
                                                            Long timeLong = Long.parseLong("-"+timeString);
                                                            contactsRef.child("time").setValue(timeLong);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
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
        });

        messageInputText.getText().clear();

    }



}




