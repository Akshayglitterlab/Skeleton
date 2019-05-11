package com.glitterlabs.skeleton.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.glitterlabs.skeleton.BuildConfig;
import com.glitterlabs.skeleton.R;
import com.glitterlabs.skeleton.model.Users;
import com.glitterlabs.skeleton.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    DatabaseReference databaseReference;
    private Users user;
    private String urlPic = null;
    public TextView phoneNumber, userName, userAddress;
    public TextView editProfile;
    public ImageView userPhoto;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = getIntent().getStringExtra("userID");

        initViews();
        registerEvents();

        databaseReference.child(Constant.TEST).child(Constant.USERS).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user =dataSnapshot.getValue(Users.class);
                phoneNumber.setText(user.getmMobile());
                userName.setText(user.getmName());

                userAddress.setText(user.getmAddress());

                //userPhoto.setImageIcon(user.getmPicURL());

                if (String.valueOf(user.getmPicUrl())!= null){
                    Glide.with(ProfileActivity.this)
                            .load(user.getmPicUrl())
                            .into(userPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(this, "Getting data", Toast.LENGTH_SHORT).show();

    }

    private void registerEvents() {
        /*editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateprofile = new Intent(getApplicationContext(), UpdateProfile.class).putExtra("user", userId);
                        startActivity(updateprofile);
            }
        });*/
    }


    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("User Profile");
        setSupportActionBar(toolbar);
        userPhoto = (ImageView)findViewById(R.id.profile_image);
        phoneNumber = (TextView)findViewById(R.id.phone_number);
        userName = (TextView)findViewById(R.id.userName);
        userAddress = (TextView) findViewById(R.id.userAddress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.editProfile:
                Intent profileActivity = new Intent(ProfileActivity.this, UpdateProfile.class);
                /*String ss = getIntent().getStringExtra("userID");
                profileActivity.putExtra("userID",ss);*/
                startActivity(profileActivity);
                break;
            case R.id.AboutUs:
                Intent aboutUsActivity = new Intent(ProfileActivity.this, AboutUsActivity.class);
                startActivity(aboutUsActivity);
                break;
            case R.id.Feedback:
                Intent FeedbackActivity = new Intent(ProfileActivity.this, com.glitterlabs.skeleton.activity.FeedbackActivity.class);
                startActivity(FeedbackActivity);
                break;
            case R.id.shareApp:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Skeleton App");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id="
                            + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
            case R.id.Logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
