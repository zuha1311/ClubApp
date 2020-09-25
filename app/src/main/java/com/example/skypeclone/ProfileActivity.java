package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private String receiverID = "";
    private String receiverUserImage = "";
    private String receiverUserName = "";
    private ImageView background_image_view;
    private TextView name_profile;
    private Button add,cancel;
    private FirebaseAuth mAuth;
    private String senderUserId;
    private String currentState = "new";
    private DatabaseReference friendRequestRef, contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");


        receiverID = getIntent().getExtras().get("visit_user_id").toString();
        receiverUserImage = getIntent().getExtras().get("profile_image").toString();
        receiverUserName = getIntent().getExtras().get("profile_name").toString();

        background_image_view = findViewById(R.id.background_profile_view);
        name_profile = findViewById(R.id.name_profile);
        add = findViewById(R.id.add_friend);
        cancel = findViewById(R.id.cancel_friend);

        Picasso.get().load(receiverUserImage).into(background_image_view);
        name_profile.setText(receiverUserName);

        manageClickEvents();

    }

    private void manageClickEvents() {

        friendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(receiverID))
                        {
                            String requestType = snapshot.child(receiverID).child("request_type")
                                    .getValue().toString();

                            if(requestType.equals("sent"))
                            {
                                currentState = "request_sent";
                                add.setText("Cancel Friend Request");

                            }
                            else if(requestType.equals("received"))
                            {
                                currentState = "request_received";
                                add.setText("Accept Friend Request");

                                cancel.setVisibility(View.VISIBLE);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            contactsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.hasChild(receiverID))
                                            {
                                                currentState = "friends";
                                                add.setText("Delete Contact");
                                            }
                                            else
                                            {
                                                currentState = "new";
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if(senderUserId.equals(receiverID))
        {
            add.setVisibility(View.GONE);
        }
        else
        {
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(currentState.equals("new"))
                    {
                            sendFriendRequest();
                    }
                    if(currentState.equals("request_sent"))
                    {
                            cancelFriendRequest();
                    }
                    if(currentState.equals("request_received"))
                        {
                            acceptFriendRequest();
                    }
                     if(currentState.equals("request_sent"))
                    {
                        cancelFriendRequest();
                    }

                }
            });
        }

    }

    private void acceptFriendRequest() {
        contactsRef.child(senderUserId).child(receiverID)
                .child("Contacts")
                .setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            contactsRef.child(receiverID).child(senderUserId)
                                    .child("Contacts")
                                    .setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {
                                                friendRequestRef.child(senderUserId).child(receiverID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    friendRequestRef.child(receiverID).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        currentState = "friends";
                                                                                        add.setText("Delete Contact");

                                                                                        cancel.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }

                    }
                });
    }

    private void cancelFriendRequest() {
        friendRequestRef.child(senderUserId).child(receiverID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                friendRequestRef.child(receiverID).child(senderUserId)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                        currentState = "new";
                                                        add.setText("ADD FRIEND");
                                                }
                                            }
                                        });
                            }
                    }
                });
    }

    private void sendFriendRequest() {

        friendRequestRef.child(senderUserId)
                .child(receiverID)
                .child("request_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            friendRequestRef.child(receiverID)
                                    .child(senderUserId)
                                    .child("request_type")
                                    .setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                currentState = "request_sent";
                                                add.setText("Cancel Friend Request");
                                                Toast.makeText(ProfileActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        }

                    }
                });



    }
}