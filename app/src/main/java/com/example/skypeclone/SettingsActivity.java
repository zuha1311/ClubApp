package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private Button save;
    private EditText username, bio;
    private ImageView userImage;
    private static int galleryPick = 1;
    private Uri imageUri;
    private StorageReference userProfileImageRef;
    private String downloadUrl;
    private DatabaseReference userRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        save = findViewById(R.id.save_settings);
        userImage = findViewById(R.id.settings_profile_image);
        username = findViewById(R.id.username_Settings);
        bio= findViewById(R.id.bio_Settings);
        loadingBar = new ProgressDialog(this);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPick);
            }
        });
        
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedUserData();
            }
        });

        retreieveUserInfo();
    }

    private void savedUserData() {
        final String getUsername = username.getText().toString();
        final String getBio = bio.getText().toString();

        if(imageUri == null)
        {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid() ).hasChild("profileImage"))
                    {
                        saveInfoOnly();
                    }
                    else
                    {
                        Toast.makeText(SettingsActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if(getUsername.equals(""))
        {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
        }
        else if(getBio.equals(""))
        {
            Toast.makeText(this, "Bio is required", Toast.LENGTH_SHORT).show();
        }
        else{

            loadingBar.setTitle("Account Settings");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            final StorageReference filePath = userProfileImageRef.child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid());

            final UploadTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    downloadUrl = filePath.getDownloadUrl().toString();
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        downloadUrl = task.getResult().toString();

                        HashMap<String, Object> profileMap = new HashMap<>();
                        profileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profileMap.put("name",getUsername);
                        profileMap.put("bio",getBio);
                        profileMap.put("profileImage",downloadUrl);

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent intent = new Intent(SettingsActivity.this,ContentsActivity.class);
                                        startActivity(intent);
                                        finish();
                                        loadingBar.dismiss();
                                    }
                            }
                        });
                    }
                }
            });
        }
    }

    private void saveInfoOnly() {
        final String getUsername = username.getText().toString();
        final String getBio = bio.getText().toString();


        if(getUsername.equals(""))
        {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
        }
        else if(getBio.equals(""))
        {
            Toast.makeText(this, "Bio is required", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Account Settings");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            profileMap.put("name",getUsername);
            profileMap.put("bio",getBio);


            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(SettingsActivity.this,ContentsActivity.class);
                        startActivity(intent);
                        finish();
                        loadingBar.dismiss();
                    }
                }
            });
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galleryPick && resultCode==RESULT_OK && data!=null)
        {
            imageUri = data.getData();
            userImage.setImageURI(imageUri);

        }

    }

    private void retreieveUserInfo()
    {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String imageDb= snapshot.child("profileImage").getValue().toString();
                            String usernameDb = snapshot.child("name").getValue().toString();
                            String bioDb = snapshot.child("bio").getValue().toString();

                            username.setText(usernameDb);
                            bio.setText(bioDb);
                            Picasso.get().load(imageDb).placeholder(R.drawable.profile_image).into(userImage);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}