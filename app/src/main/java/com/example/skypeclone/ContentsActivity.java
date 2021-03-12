package com.example.skypeclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ContentsActivity extends AppCompatActivity {
    BottomNavigationView navView;
    RecyclerView myContatcsList;
    ImageView findFriends;
    private DatabaseReference contactsRef,usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String  profileImage="", userName="",calledBy="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();


        findFriends = findViewById(R.id.find_people);
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        myContatcsList = findViewById(R.id.contactsList);
        myContatcsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentsActivity.this, FindPeopleActivity.class);
                startActivity(intent);
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch(item.getItemId())
            {
                case R.id.navigation_home:
                   break;
                case R.id.navigation_notifications:
                    Intent intent1 = new Intent(ContentsActivity.this,NotificationsActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.navigation_settings:
                    Intent intent2 = new Intent(ContentsActivity.this,SettingsActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.navigation_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent3 = new Intent(ContentsActivity.this,RegistrationActivity.class);
                    startActivity(intent3);
                    finish();
                    break;


            }
            return true;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        
        checkForReceivingCall();

        validateUsers();

        FirebaseRecyclerOptions<Contacts> options =
               new  FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                final String listUserId = getRef(position).getKey();

                usersRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            userName = snapshot.child("name").getValue().toString();
                            profileImage = snapshot.child("profileImage").getValue().toString();

                            holder.userNameTxt.setText(userName);

                            Picasso.get().load(profileImage).into(holder.profileImageView);
                        }

                        holder.call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ContentsActivity.this,CallingActivity.class);
                                intent.putExtra("visit_user_id", listUserId);
                                startActivity(intent);
                                finish();
                            }
                        });

                        holder.msg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ContentsActivity.this,ChatActivity.class);
                                intent.putExtra("msg_user_id", listUserId);
                                intent.putExtra("msg_user_name", userName);
                                startActivity(intent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout,parent,false);
                ContentsActivity.ContactsViewHolder viewHolder = new  ContentsActivity.ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContatcsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void checkForReceivingCall() {
        usersRef.child(currentUserId)
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("ringing"))
                        {
                            calledBy = snapshot.child("ringing").getValue().toString();
                            Intent intent = new Intent(ContentsActivity.this,CallingActivity.class);
                            intent.putExtra("visit_user_id", calledBy);
                            startActivity(intent);
                            finish();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void validateUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    Intent settingIntent = new Intent(ContentsActivity.this,SettingsActivity.class);
                    startActivity(settingIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTxt;
        Button call,msg;
        ImageView profileImageView;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.name_contacts);
            call = itemView.findViewById(R.id.call_btn);
            profileImageView = itemView.findViewById(R.id.image_contacts);
            msg = itemView.findViewById(R.id.msg_btn);

        }
    }


}
