package com.glitterlabs.home.skeleton1;

import android.app.Application;
import android.content.Context;

class MultiDexApplication extends Application {

    public MultiDexApplication() {
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }
}
