package com.glitterlabs.home.skeleton1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
                Intent profileActivity = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(profileActivity);
                break;
            case R.id.AboutUs:
                Intent aboutUsActivity = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(aboutUsActivity);
                break;
            case R.id.Feedback:
                Intent FeedbackActivity = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(FeedbackActivity);
                break;
            case R.id.shareApp:
                Intent shareAppActivity = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(shareAppActivity);
                break;
            case R.id.Logout:
                Intent Logout = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(Logout);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
