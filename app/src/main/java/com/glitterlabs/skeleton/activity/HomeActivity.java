package com.glitterlabs.skeleton.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.glitterlabs.home.skeleton1.BuildConfig;
import com.glitterlabs.home.skeleton1.R;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //firebaseAuth = FirebaseAuth.getInstance();
        //firebaseUser = firebaseAuth.getCurrentUser();
        String s = getIntent().getStringExtra("userID");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            case R.id.Profile:
                Intent profileActivity = new Intent(HomeActivity.this, ProfileActivity.class);
                String ss = getIntent().getStringExtra("userID");
                profileActivity.putExtra("userID",ss);
                startActivity(profileActivity);
                break;
            case R.id.AboutUs:
                Intent aboutUsActivity = new Intent(HomeActivity.this, AboutUsActivity.class);
                startActivity(aboutUsActivity);
                break;
            case R.id.Feedback:
                Intent FeedbackActivity = new Intent(HomeActivity.this, com.glitterlabs.skeleton.activity.FeedbackActivity.class);
                startActivity(FeedbackActivity);
                break;
            case R.id.shareApp:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Skeleton App");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
            case R.id.Logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
