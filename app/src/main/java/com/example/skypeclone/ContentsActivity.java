package com.example.skypeclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ContentsActivity extends AppCompatActivity {
    BottomNavigationView navView;
    RecyclerView myContatcsList;
    ImageView findFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        findFriends = findViewById(R.id.find_people);
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
                    Intent intent = new Intent(ContentsActivity.this, ContentsActivity.class);
                    startActivity(intent);
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

}
