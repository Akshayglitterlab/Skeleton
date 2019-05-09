package com.glitterlabs.skeleton.utility;

import android.content.ContextWrapper;


import com.glitterlabs.skeleton.model.Users;
import com.google.firebase.firestore.auth.User;
import com.pixplicity.easyprefs.library.Prefs;
import android.support.multidex.MultiDexApplication;

public class MainApplication extends MultiDexApplication {

    static MainApplication mainApplication = null;

    private Users user;


    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;
        initPrefs();
        Prefs.putString(Constant.MODE,Constant.TEST);

    }

    private void initPrefs() {
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }


    public static MainApplication getInstance(){
        if (mainApplication == null){
            mainApplication = new MainApplication();
        }
        return mainApplication;
    }

    public Users getUser(){
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }


}
