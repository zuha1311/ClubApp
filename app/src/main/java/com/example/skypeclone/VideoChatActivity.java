package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity  implements Session.SessionListener, PublisherKit.PublisherListener

{


    private static String API_KEY = "46932134";
    private static String SESSION_ID = "2_MX40NjkzMjEzNH5-MTYwMDk3MjE5NzkzMX4yV2hhTEY0Y1VsY2x2NGtnUllyN0k5Qi9-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjkzMjEzNCZzaWc9OWVlODg1YTVkYjgxZTE3ZDBjOWJkZDdiYmIyYTk1ZDNmYWMxYzQ2MjpzZXNzaW9uX2lkPTJfTVg0ME5qa3pNakV6Tkg1LU1UWXdNRGszTWpFNU56a3pNWDR5VjJoaFRFWTBZMVZzWTJ4Mk5HdG5VbGx5TjBrNVFpOS1mZyZjcmVhdGVfdGltZT0xNjAwOTcyMjU0Jm5vbmNlPTAuNDM3NjM3MDI4MTQ0NDE4NiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjAzNTY0Mzg3JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERMISSION = 124;

    private ImageView closeVideo;
    private DatabaseReference usersRef;
    private String userId = "";

    private FrameLayout mPublisherViewController;
    private FrameLayout mSubscriberViewController;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        closeVideo = findViewById(R.id.close_video_chat_btn);
        closeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child(userId).hasChild("Ringing"))
                            {
                                usersRef.child(userId).child("Ringing").removeValue();
                                if(mPublisher != null)
                                {
                                    mPublisher.destroy();
                                }
                                if(mSubscriber != null)
                                {
                                    mSubscriber.destroy();
                                }

                                startActivity(new Intent(VideoChatActivity.this,RegistrationActivity.class));
                                finish();
                            }
                            if(snapshot.child(userId).hasChild("Calling"))
                            {
                                usersRef.child(userId).child("Calling").removeValue();

                                if(mPublisher != null)
                                {
                                    mPublisher.destroy();
                                }
                                if(mSubscriber != null)
                                {
                                    mSubscriber.destroy();
                                }

                                startActivity(new Intent(VideoChatActivity.this,RegistrationActivity.class));
                                finish();
                            }
                            else
                            {
                                startActivity(new Intent(VideoChatActivity.this,RegistrationActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        });

        requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,VideoChatActivity.this);

    }
    @AfterPermissionGranted(RC_VIDEO_APP_PERMISSION)
    private void requestPermission()
    {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};

        if(EasyPermissions.hasPermissions(this,perms))
        {
            mPublisherViewController = findViewById(R.id.publisher_container);
            mSubscriberViewController = findViewById(R.id.subscriber_container);

            //initialize and connect to the session

            mSession = new Session.Builder(this,API_KEY,SESSION_ID).build();

            mSession.setSessionListener(VideoChatActivity.this);
            mSession.connect(TOKEN);


        }
        else
        {
            EasyPermissions.requestPermissions(this,"Hey! This app requires Camera and Mic permission, please grant it",RC_VIDEO_APP_PERMISSION,perms);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    //publishing stream to the session

    @Override
    public void onConnected(Session session) {

        Log.i(LOG_TAG,"Session Connected");
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(VideoChatActivity.this);

        mPublisherViewController.addView(mPublisher.getView());

        if(mPublisher.getView() instanceof GLSurfaceView)
        {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }
        mSession.publish(mPublisher);

    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG,"Stream Disconnected");


    }

    //Subscribing to the stream

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG,"Stream Received");

        if(mSubscriber == null)
        {
            mSubscriber = new Subscriber.Builder(this,stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewController.addView(mSubscriber.getView());
        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG,"Stream Dropped");

        if(mSubscriber!=null)
        {
            mSubscriber = null;
            mSubscriberViewController.removeAllViews();
        }


    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(LOG_TAG,"Stream Error");


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    }
