package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView notificationsList;
    private DatabaseReference friendRequestRef, contactsRef,usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");



        notificationsList = findViewById(R.id.notificationsList);
        notificationsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(friendRequestRef.child(currentUserId), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, NotificationsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contacts, NotificationsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationsViewHolder holder, int position, @NonNull Contacts model) {
                    holder.accept.setVisibility(View.VISIBLE);
                    holder.cancel.setVisibility(View.VISIBLE);

                     final String listUserId = getRef(position).getKey();

                    DatabaseReference requestTypeRef = getRef(position).child("request_type")
                            .getRef();
                    requestTypeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                String type = snapshot.getValue().toString();

                                if(type.equals("received"))
                                {
                                    holder.cardView.setVisibility(View.GONE);

                                    usersRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.hasChild("profileImage"))
                                            {
                                                final String imageStr = snapshot.child("profileImage").getValue().toString();

                                                Picasso.get().load(imageStr).into(holder.profileImage);

                                            }
                                            final String nameStr = snapshot.child("name").getValue().toString();
                                            holder.userName.setText(nameStr);


                                            holder.accept.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    contactsRef.child(currentUserId).child(listUserId)
                                                            .child("Contacts")
                                                            .setValue("Saved")
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful())
                                                                    {
                                                                        contactsRef.child(listUserId).child(currentUserId)
                                                                                .child("Contacts")
                                                                                .setValue("Saved")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            friendRequestRef.child(currentUserId).child(listUserId)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful())
                                                                                                            {
                                                                                                                friendRequestRef.child(listUserId).child(currentUserId)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if(task.isSuccessful())
                                                                                                                                {
                                                                                                                                    Toast.makeText(NotificationsActivity.this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
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
                                            });
                                            holder.cancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    friendRequestRef.child(currentUserId).child(listUserId)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        friendRequestRef.child(listUserId).child(currentUserId)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            Toast.makeText(NotificationsActivity.this, "Friend Request Cancelled", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else
                                {
                                    holder.cardView.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }

            @NonNull
            @Override
            public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friends_layout,parent,false);
                NotificationsActivity.NotificationsViewHolder viewHolder = new NotificationsActivity.NotificationsViewHolder(view);
                return  viewHolder;
            }
        };

        notificationsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        Button accept, cancel;
        ImageView profileImage;
        RelativeLayout cardView;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.name_notification);
            accept = itemView.findViewById(R.id.accept_btn);
            cancel = itemView.findViewById(R.id.cancel_btn);
            profileImage = itemView.findViewById(R.id.image_notification);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }

}