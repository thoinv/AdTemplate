package com.superad.demo;

import android.app.Application;

import com.superad.util.AdPrefs;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AdPrefs.initialize(this);
    }
}
