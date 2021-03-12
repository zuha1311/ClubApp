package com.example.skypeclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;


    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMsgTXT, receiverMsgTXT;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsgTXT = itemView.findViewById(R.id.sender_msg_txt);
            receiverMsgTXT = itemView.findViewById(R.id.receiver_msg_txt);
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_msgs_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);


        if (fromMessageType.equals("text")) {
            holder.receiverMsgTXT.setVisibility(View.INVISIBLE);
            holder.senderMsgTXT.setVisibility(View.INVISIBLE);

            if (fromUserId.equals(messageSenderId)) {
                holder.senderMsgTXT.setBackgroundResource(R.drawable.sender_msg_layout);
                holder.senderMsgTXT.setText(messages.getMessage());
                holder.senderMsgTXT.setVisibility(View.VISIBLE);


            } else {

                holder.receiverMsgTXT.setVisibility(View.VISIBLE);
                holder.receiverMsgTXT.setBackgroundResource(R.drawable.receier_msgs_layout);
                holder.receiverMsgTXT.setText(messages.getMessage());

            }
        }


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
