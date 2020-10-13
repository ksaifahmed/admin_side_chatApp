package com.example.talksoroddho;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private List<Long> times;
    private FirebaseAuth mAuth;


    public MessageAdapter (List<Messages> userMessagesList, List<Long> times)
    {
        this.userMessagesList = userMessagesList;
        this.times = times;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText, senderTime, receiverTime;
        public CircleImageView receiverimg;
        public LinearLayout rll, sll;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message);
            receiverMessageText = itemView.findViewById(R.id.receiver_message);
            receiverimg = itemView.findViewById(R.id.message_img);

            senderTime = itemView.findViewById(R.id.sender_message_time);
            receiverTime = itemView.findViewById(R.id.receiver_message_time);
            rll = itemView.findViewById(R.id.receiver_message_linlay);
            sll = itemView.findViewById(R.id.sender_message_linlay);
        }
    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_messages_layout, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i)
    {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(i);
        Long time = (Long) times.get(i);

        Date date = new Date(time);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd hh:mm a");
        final String messagetime = df.format(date);

        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();


        messageViewHolder.receiverMessageText.setVisibility(View.INVISIBLE);
        messageViewHolder.receiverimg.setVisibility(View.INVISIBLE);
        messageViewHolder.senderMessageText.setVisibility(View.INVISIBLE);

        messageViewHolder.receiverTime.setVisibility(View.INVISIBLE);
        messageViewHolder.senderTime.setVisibility(View.INVISIBLE);
        messageViewHolder.rll.setVisibility(View.INVISIBLE);
        messageViewHolder.sll.setVisibility(View.INVISIBLE);



        if(fromMessageType.equals("text"))
        {

            if(fromUserId.equals(messageSenderId))
            {
                messageViewHolder.sll.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderTime.setVisibility(View.VISIBLE);

                messageViewHolder.senderMessageText.setText(messages.getMessage());
                messageViewHolder.senderTime.setText(messagetime);


            }
            else
            {
				messageViewHolder.rll.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverimg.setVisibility(View.VISIBLE);
                messageViewHolder.receiverTime.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessageText.setText(messages.getMessage());
                messageViewHolder.receiverTime.setText(messagetime);
            }
        }
    }






    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }




}

