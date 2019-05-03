package com.glitterlabs.home.skeleton1;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executor;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmOTP extends Fragment {

    private FirebaseAuth mAuth;
    public TextView textViewOTP;
    public Button submit;

    public String code;
    //public String OTP;
    private String verificationId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_ot, container, false);
        verificationId = getArguments().getString("Id");
        code = getArguments().getString("Token");
        mAuth = FirebaseAuth.getInstance();
        textViewOTP = view.findViewById(R.id.OTP_TextField);
        submit = view.findViewById(R.id.btnConfOTP);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTP = textViewOTP.getText().toString().trim();

                if (TextUtils.isEmpty(OTP)) {
                    //Toast.makeText(PhoneNumber.class,"Please enter phone number.",Toast.LENGTH_SHORT).show();
                    ///Toast.makeText(this, "Please enter Phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, OTP);
                signInWithPhoneAuthCredential(credential);

            }
        });


        return view;
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser firebaseUser = task.getResult().getUser();
                            firebaseUser.getUid();
                            String id = firebaseUser.getUid();
                            User user = new User();
                            MainApplication mainApplication = MainApplication.getInstance();
                            user.setmMobile(firebaseUser.getPhoneNumber());
                            user.setmUserID(firebaseUser.getUid());
                            mainApplication.setUser(user);

                            Intent intent = new Intent(getActivity(), CreateProfileActivity.class);
                            ((MainActivity) getActivity()).startActivity(intent);

                            //
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                            }
                        }
                    }
                });

    }

}
