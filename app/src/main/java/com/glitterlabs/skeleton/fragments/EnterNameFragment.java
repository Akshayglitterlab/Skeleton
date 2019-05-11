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


/**
 * A simple {@link Fragment} subclass.
 */
public class EnterNameFragment extends Fragment {

    private TextView nameTxt;
    private Button btnNext;

    public EnterNameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_name, container, false);

        nameTxt = view.findViewById(R.id.nameText);
        btnNext = view.findViewById(R.id.btnSubmit);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name= nameTxt.getText().toString().trim();
                if (TextUtils.isEmpty(Name)){
                    return;
                }else {
                    proceed(Name);
                }
            }
        });
        return view;
    }
    private void proceed(String Name){
        MainApplication mainApplication = MainApplication.getInstance();
        Users user = mainApplication.getUser();
        user.setmName(Name);
        mainApplication.setUser(user);
        CreateProfileActivity.fragmentManager.beginTransaction().
                replace(R.id.fragmentContainer,new EnterAddressFragment(),null).addToBackStack(null).commit();
    }
}
