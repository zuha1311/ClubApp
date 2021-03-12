package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FindPeopleActivity extends AppCompatActivity {
    private RecyclerView findFriendsList;
    private EditText search;
    private String str = "";
    private DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        search = findViewById(R.id.search_user_text);
        findFriendsList = findViewById(R.id.findFriendsList);
        findFriendsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(search.getText().toString().equals(""))
                {
                    Toast.makeText(FindPeopleActivity.this, "Please write name to search", Toast.LENGTH_SHORT).show();
                }
                else
                {
                   str = charSequence.toString();
                   onStart();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options  = null;

        if(str.equals(""))
        {
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(usersRef, Contacts.class)
                    .build();
        }
        else
        {
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(usersRef.orderByChild("name")
                            .startAt(str)
                            .endAt(str + "\uf8ff"), Contacts.class)
                    .build();
        }
        FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull final Contacts model) {
                    holder.userName.setText(model.getName());
                    Picasso.get().load(model.getProfileImage()).into(holder.profileImage);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String visit_user_id = getRef(position).getKey();
                            Intent intent = new Intent(FindPeopleActivity.this, ProfileActivity.class);
                            intent.putExtra("visit_user_id",visit_user_id);
                            intent.putExtra("profile_name",model.getName());
                            intent.putExtra("profile_image",model.getProfileImage());
                            startActivity(intent);
                        }
                    });

            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout,parent,false);
                FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                return  viewHolder;
            }
        };

        findFriendsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        Button call,msg;
        ImageView profileImage;
        RelativeLayout cardView;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.name_contacts);
            call = itemView.findViewById(R.id.call_btn);
            msg = itemView.findViewById(R.id.msg_btn);
            profileImage = itemView.findViewById(R.id.image_contacts);
            cardView = itemView.findViewById(R.id.card_view1);

            call.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);



        }
    }

}