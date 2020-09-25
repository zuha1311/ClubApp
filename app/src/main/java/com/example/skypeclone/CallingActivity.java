package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CallingActivity extends AppCompatActivity {

    private TextView nameContacts;
    private ImageView profileImage;
    private ImageView cancel, call;
    private String receiverUserId="", receiverUserName="", receiverUserImage="";
    private String senderUserId="", senderUserName="", senderUserImage="";
    private DatabaseReference usersRef;
    private String checker="";
    private String callingId = "" , ringingId = "";
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        senderUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        nameContacts = findViewById(R.id.name_calling);
        profileImage = findViewById(R.id.calling_profile_image);
        cancel = findViewById(R.id.cancel_call);
        call = findViewById(R.id.make_call);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();

                checker = "clicked";

                cancelCallingUser();

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.stop();
                final HashMap<String, Object> callingPickupMap = new HashMap<>();
                callingPickupMap.put("picked", "picked");

                usersRef.child(senderUserId).child("Ringing").updateChildren(callingPickupMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent intent = new Intent(CallingActivity.this, VideoChatActivity.class);
                                        startActivity(intent);
                                    }
                            }
                        });
            }
        });

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mediaPlayer = MediaPlayer.create(this,R.raw.ringing);
        
        getAndSetUserProfileInfo();




    }

    private void cancelCallingUser() {

        //from sender side
        usersRef.child(senderUserId).child("Calling")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.hasChild("calling"))
                        {
                            callingId = snapshot.child("calling").getValue().toString();

                            usersRef.child(callingId)
                                    .child("Ringing")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                usersRef.child(senderUserId)
                                                        .child("Calling")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Intent intent = new Intent(CallingActivity.this,RegistrationActivity.class);
                                                                startActivity(intent);

                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            Intent intent = new Intent(CallingActivity.this,RegistrationActivity.class);
                            startActivity(intent);                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //from receiver's side
        usersRef.child(senderUserId).child("Ringing")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.hasChild("ringing"))
                        {
                            ringingId = snapshot.child("ringing").getValue().toString();

                            usersRef.child(ringingId)
                                    .child("Calling")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                usersRef.child(senderUserId)
                                                        .child("Ringing")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Intent intent = new Intent(CallingActivity.this,RegistrationActivity.class);
                                                                startActivity(intent);

                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            Intent intent = new Intent(CallingActivity.this,RegistrationActivity.class);
                            startActivity(intent);                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();


        usersRef.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!checker.equals("clicked") && !snapshot.hasChild("Calling") && !snapshot.hasChild("Ringing"))
                {
                    final HashMap<String, Object> callingInfo = new HashMap<>();

                    callingInfo.put("calling",receiverUserId);

                    usersRef.child(senderUserId).child("Calling")
                            .updateChildren(callingInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        final HashMap<String, Object> ringingInfo = new HashMap<>();

                                        ringingInfo.put("ringing",senderUserId);

                                        usersRef.child(receiverUserId)
                                                .child("Ringing")
                                                .updateChildren(ringingInfo);
                                    }
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAndSetUserProfileInfo() {

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(receiverUserId).exists())
                {
                    receiverUserImage = snapshot.child(receiverUserId).child("profileImage").getValue().toString();
                    receiverUserName = snapshot.child(receiverUserId).child("name").getValue().toString();

                    nameContacts.setText(receiverUserName);
                    Picasso.get().load(receiverUserImage).placeholder(R.drawable.profile_image).into(profileImage);

                }

                if(snapshot.child(senderUserId).exists())
                {
                    senderUserImage = snapshot.child(senderUserId).child("profileImage").getValue().toString();
                    senderUserName = snapshot.child(senderUserId).child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(senderUserId).hasChild("Ringing") && !snapshot.child(senderUserId).hasChild("Calling") )
                {
                    call.setVisibility(View.VISIBLE);
                    mediaPlayer.stop();

                }
                if(snapshot.child(receiverUserId).child("Ringing").hasChild("picked"))
                {
                    mediaPlayer.stop();
                    Intent intent = new Intent(CallingActivity.this, VideoChatActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}