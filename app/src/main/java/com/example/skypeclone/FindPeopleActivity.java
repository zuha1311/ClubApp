package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FindPeopleActivity extends AppCompatActivity {
    private RecyclerView findFriendsList;
    private EditText search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        search = findViewById(R.id.search_user_text);
        findFriendsList = findViewById(R.id.findFriendsList);
        findFriendsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        Button call;
        ImageView profileImage;
        RelativeLayout cardView;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.name_contacts);
            call = itemView.findViewById(R.id.call_btn);
            profileImage = itemView.findViewById(R.id.image_contacts);
            cardView = itemView.findViewById(R.id.card_view1);

        }
    }

}