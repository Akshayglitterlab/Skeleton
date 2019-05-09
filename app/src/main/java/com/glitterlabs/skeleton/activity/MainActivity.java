package com.glitterlabs.skeleton.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.glitterlabs.home.skeleton1.R;
import com.glitterlabs.skeleton.fragments.PhoneNumber;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);

            String uid = firebaseUser.getUid();
            intent.putExtra("userID",uid);
            startActivity(intent);
        }

        fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.fragmentContainer) != null)
        {
            if (savedInstanceState != null)
            {
                return;
            }

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            PhoneNumber phoneNumber = new PhoneNumber();
            fragmentTransaction.add(R.id.fragmentContainer,phoneNumber,null);
            fragmentTransaction.commit();

        }
    }
}
