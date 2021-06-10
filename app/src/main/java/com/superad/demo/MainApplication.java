package com.superad.demo;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.superad.AdManager;
import com.superad.AppDelegate;
import com.superad.config.ConfigLoader;
import com.superad.config.ConfigStrategy;
import com.superad.openad.AppOpenManager;

public class MainApplication extends Application {
    public AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        AdManager.init(this, true);
        AdManager.getInstance(this).setAppDelegate(new AppDelegate() {
            @Override
            public boolean isPolicyAccepted() {
                return true;
            }
        });

        ConfigStrategy config = new ConfigLoader(this).getConfig();

        appOpenManager = new AppOpenManager(this, config.getAdMobOpenAdUnit("oa_return", null));
    }
}
