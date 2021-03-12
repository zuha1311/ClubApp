package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String msgReceiverId="", msgUserName="",msgSenderId="";
    private TextView userName;
    private DatabaseReference usersRef;
    private String senderUserId="";
    private ImageButton sendMsgBtn;
    private EditText msgText;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        msgReceiverId = getIntent().getExtras().get("msg_user_id").toString();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        senderUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userName = findViewById(R.id.chatActivityUserName);
        sendMsgBtn = findViewById(R.id.sendMsgIcon);
        msgText = findViewById(R.id.message1);

        mAuth = FirebaseAuth.getInstance();
        msgSenderId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        setProfileName();


    }

    private void setProfileName() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(msgReceiverId).exists())
                {
                    msgUserName = snapshot.child(msgReceiverId).child("name").getValue().toString();
                    userName.setText(msgUserName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage()
    {
            String msgInputText = msgText.getText().toString();

            if(TextUtils.isEmpty(msgInputText))
            {
                Toast.makeText(this, "Please write a message", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String messageSenderRef = "Messages/" + msgSenderId + "/" + msgReceiverId;
                String messageReceiverRef = "Messages/" + msgReceiverId + "/" + msgSenderId;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(msgSenderId).child(msgReceiverId).push();

                String messagePushId = userMessageKeyRef.getKey();

                Map messageTextBody = new HashMap();
                messageTextBody.put("message", msgInputText);
                messageTextBody.put("type","text");
                messageTextBody.put("from",msgSenderId);

                Map messageBodyDetails = new HashMap();
                messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);

                messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);
                
                RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ChatActivity.this, "Message sending unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                        msgText.setText("");
                    }
                });



            }
    }

}