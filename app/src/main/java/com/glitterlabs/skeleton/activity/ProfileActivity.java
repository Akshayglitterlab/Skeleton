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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
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
import com.pixplicity.easyprefs.library.Prefs;


public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
/*    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;*/

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

        databaseReference.child(Constant.TEST).child(Constant.USERS).child(Prefs.getString("userID",null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user =dataSnapshot.getValue(Users.class);
                phoneNumber.setText(user.getmMobile());
                userName.setText(user.getmName());
                userAddress.setText(user.getmAddress());

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

    private void initViews() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userPhoto = findViewById(R.id.profile_image);
        phoneNumber = findViewById(R.id.phone_number);
        userName = findViewById(R.id.userName);
        userAddress = findViewById(R.id.userAddress);
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
                profileActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(profileActivity);
                finish();
                break;
            case R.id.AboutUs:
                Intent aboutUsActivity = new Intent(ProfileActivity.this, AboutUsActivity.class);
                aboutUsActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(aboutUsActivity);
                finish();
                break;
            case R.id.Feedback:
                Intent feedbackActivity = new Intent(ProfileActivity.this, com.glitterlabs.skeleton.activity.FeedbackActivity.class);
                feedbackActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(feedbackActivity);
                finish();
                break;
            case R.id.shareApp:
                shareApp();
                break;
            case R.id.Logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
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
    }
}
