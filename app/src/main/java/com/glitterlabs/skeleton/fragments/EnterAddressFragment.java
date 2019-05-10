package com.glitterlabs.skeleton.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.glitterlabs.skeleton.R;
import com.glitterlabs.skeleton.activity.CreateProfileActivity;
import com.glitterlabs.skeleton.model.Users;
import com.glitterlabs.skeleton.utility.MainApplication;
import com.google.firebase.firestore.auth.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnterAddressFragment extends Fragment {

    private TextView addressTxt;
    private Button btnAddressSubmit;


    public EnterAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_address, container, false);
        addressTxt = view.findViewById(R.id.AddressTextField);
        btnAddressSubmit = view.findViewById(R.id.btnAddress);
        btnAddressSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = addressTxt.getText().toString().trim();
                if (TextUtils.isEmpty(address)){
                    return;
                }else {
                    proceed(address);
                }

            }
        });

        return view;
    }

    private void proceed(String address) {

        MainApplication mainApplication = MainApplication.getInstance();
        Users user =mainApplication.getUser();
        user.setmAddress(address);
        mainApplication.setUser(user);
        CreateProfileActivity.fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer,new EnterProfilePictureFragment(),null).addToBackStack(null).commit();
        getActivity().finish();
    }

}
