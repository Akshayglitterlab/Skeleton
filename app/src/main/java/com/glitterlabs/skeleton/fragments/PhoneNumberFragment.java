package com.glitterlabs.skeleton.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.glitterlabs.skeleton.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneNumberFragment extends Fragment {

    private String mVerificationId;
    private String phone1;

    CountryCodePicker ccp;
    private AppCompatEditText edtPhone;
    private Button btnPhoneSubmit;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    public PhoneNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone__number, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        ccp = view.findViewById(R.id.ccp);
        btnPhoneSubmit = view.findViewById(R.id.btnPhone);
        edtPhone = view.findViewById(R.id.edittextPhone);
        ccp.registerPhoneNumberTextView(edtPhone);

        btnPhoneSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone1 =ccp.getNumber();

                if (TextUtils.isEmpty(phone1)) {
                    Toast.makeText(getActivity(), "Please enter Phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendVerificationCode(phone1);

            }



        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                //signInWithPhoneAuthCredential(credential);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                super.onCodeSent(verificationId,token);
                Log.d(TAG, "onCodeSent:" + verificationId);
                //codeSent = verificationId;
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                sendVerification(mVerificationId,mResendToken);
            }
        };
        return view;
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                getActivity(),
                mCallbacks);
    }

    private void sendVerification(String mVerificationId, PhoneAuthProvider.ForceResendingToken mResendToken){
        ConfirmOTPFragment confirmOTP = new ConfirmOTPFragment();
        Bundle args = new Bundle();
        args.putString("Id", mVerificationId);
        args.putString("Token", String.valueOf(mResendToken));
        confirmOTP.setArguments(args);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentContainer, confirmOTP, "createProfile")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("createProfile").commit();
    }
}
