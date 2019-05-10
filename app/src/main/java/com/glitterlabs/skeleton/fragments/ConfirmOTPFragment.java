package com.glitterlabs.skeleton.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.glitterlabs.skeleton.R;
import com.glitterlabs.skeleton.activity.ProfileActivity;
import com.glitterlabs.skeleton.model.Users;
import com.glitterlabs.skeleton.utility.Constant;
import com.glitterlabs.skeleton.activity.CreateProfileActivity;
import com.glitterlabs.skeleton.utility.MainApplication;

import com.glitterlabs.skeleton.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmOTPFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private TextView editTextOTP;
    private Button btnSubmit;

    private String code;
    //public String OTP;
    private String verificationId;
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_ot, container, false);

        initView(view);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTP = editTextOTP.getText().toString().trim();
                if (TextUtils.isEmpty(OTP)) {
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, OTP);
                signInWithPhoneAuthCredential(credential);
            }
        });
        return view;

    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        verificationId = getArguments().getString("Id");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        code = getArguments().getString("Token");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        editTextOTP = view.findViewById(R.id.editTextOTP);
        btnSubmit = view.findViewById(R.id.btnConfOTP);
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
                            checkExistingUser(firebaseUser);
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

    private void checkExistingUser(final FirebaseUser firebaseUser) {
        databaseReference.child(Constant.TEST).child(Constant.USERS).child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(String.valueOf(dataSnapshot.getValue()) != "null"){

                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("userID",firebaseUser.getUid());
                    startActivity(intent);
                    getActivity().finish();

                }else{
                    MainApplication mainApplication = MainApplication.getInstance();
                    Users user = new Users();
                    user.setmMobile(firebaseUser.getPhoneNumber());
                    user.setmUserID(firebaseUser.getUid());
                    mainApplication.setUser(user);

                    Intent intent = new Intent(getActivity(), CreateProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ((MainActivity) getActivity()).startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                String strError = error.getMessage();
            }
        });
    }

}
