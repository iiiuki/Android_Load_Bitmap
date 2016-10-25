package com.example.hiep.android_loadbitmap.system;

import android.app.Application;

/**
 * Created by Hiep on 10/25/2016.
 */
public class MyApp extends Application {
    private static MyApp mInstance;
    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }
}
