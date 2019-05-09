package com.glitterlabs.skeleton.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.glitterlabs.home.skeleton1.R;
import com.glitterlabs.skeleton.fragments.EnterNameFragment;


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
            EnterNameFragment enterName = new EnterNameFragment();
            fragmentTransaction.add(R.id.fragmentContainer,enterName,null);
            fragmentTransaction.commit();
        }
        //progressDialog.setMessage("Register");
        //progressDialog.show();
    }
}
