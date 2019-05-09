package com.glitterlabs.skeleton.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.glitterlabs.skeleton.R;


public class AboutUsActivity extends AppCompatActivity {

    private ImageButton imgBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutUsActivity.this, HomeActivity.class));
                finish();
            }
        });
    }
}
