package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationsList = findViewById(R.id.notificationsList);
        notificationsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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