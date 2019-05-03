package com.glitterlabs.home.skeleton1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;


public class CreateProfileActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //progressDialog = new ProgressDialog(this);

        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragmentContainer) != null){
            if (savedInstanceState !=null){
                return;
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            EnterName enterName = new EnterName();
            fragmentTransaction.add(R.id.fragmentContainer,enterName,null);
            fragmentTransaction.commit();
        }
        //progressDialog.setMessage("Register");
        //progressDialog.show();
    }
}
