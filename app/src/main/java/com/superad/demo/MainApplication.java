package com.superad.demo;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.superad.AdManager;
import com.superad.util.AdPrefs;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        AdManager.init(this, true);
    }
}
