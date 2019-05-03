package com.glitterlabs.home.skeleton1;

import android.content.ContextWrapper;
import com.glitterlabs.home.skeleton1.User;
import com.google.android.gms.common.internal.Constants;
import com.pixplicity.easyprefs.library.Prefs;

public class MainApplication extends MultiDexApplication{

    static MainApplication mainApplication = null;

    private User user;


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

    public User getUser(){
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
