package com.glitterlabs.home.skeleton1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;

    private String urlPic = null;
    public TextView phoneNumber, userName, userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("User Profile");
        setSupportActionBar(toolbar);
        phoneNumber = (TextView)findViewById(R.id.phone_number);
        userName = (TextView)findViewById(R.id.userName);
        userAddress = (TextView) findViewById(R.id.userAddress);

        MainApplication mainApplication = MainApplication.getInstance();
        User user = mainApplication.getUser();
        user.getmMobile();
        user.getmName();
        user.getmAddress();
        user.getmPicURL();

        phoneNumber.setText(user.getmMobile());
        userName.setText(user.getmName());
        userAddress.setText(user.getmAddress());

        Toast.makeText(this, "Getting data", Toast.LENGTH_SHORT).show();
    }
}
