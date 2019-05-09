package com.glitterlabs.skeleton.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.glitterlabs.home.skeleton1.R;
import com.glitterlabs.skeleton.utility.Constant;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixplicity.easyprefs.library.Prefs;

public class FeedbackActivity extends AppCompatActivity {

    private static final int STATUS_EXP = 400;
    private static final int STATUS_OK = 200;
    private Button btnSubmitFeedback;
    private EditText edtFeedback;
    private ProgressDialog pd;
    private ImageButton btnClose;
    private TextInputLayout tilFeedback;
    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initViews();

        registerEvents();


    }

    private void registerEvents() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss();
                Intent intent = new Intent(FeedbackActivity.this, HomeActivity.class)
                        .putExtra("userID",Prefs.getString("userID",null));
                startActivity(intent);
            }
        });
        btnSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtFeedback.getText().toString().trim().equals("")) {
                    tilFeedback.setError("Please enter your feedback.");
                } else {

                    pd = ProgressDialog.show(FeedbackActivity.this, "", "Submitting...");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            submitFeedback(edtFeedback.getText().toString());
                        }
                    }).start();
                }
            }
        });
    }
    void submitFeedback(final String feedback) {
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        if(Prefs.getString(Constant.MODE,null).equals("live")){
            databaseReference = database.getReference(Constant.LIVE).child(Constant.FEEDBACK);
        }else{
            databaseReference = database.getReference(Constant.TEST).child(Constant.FEEDBACK);
        }
        databaseReference.child(Prefs.getString(Constant.USER_ID,null)).setValue(feedback);
        pd.dismiss();
        //dismiss();
    }

    private void initViews() {
        btnClose = (ImageButton)findViewById(R.id.btnClose);
        edtFeedback = (EditText)findViewById(R.id.edtFeedback);
        btnSubmitFeedback = (Button)findViewById(R.id.btnSubmitFeedback);
        tilFeedback = (TextInputLayout)findViewById(R.id.tilFeedback);
        /*WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;*/
    }
}
