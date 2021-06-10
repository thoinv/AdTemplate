package com.superad;

import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.superad.config.AppRemoteConfig;
import com.superad.util.AdLogger;
import com.superad.util.AdPrefs;

public class AdManager {
    private static AdManager instance;
    private boolean debugMode;
    private boolean enableEventTrack = false;
    private AppDelegate appDelegate;

    AdManager(Context context) {
        initConfigs(context);
    }

    private void initConfigs(Context context) {
        this.reloadRemoteConfigs();
        AdPrefs.initialize(context);
        MobileAds.initialize(context, initializationStatus -> {
            AdLogger.logI("Initialize completed");
        });
    }

    public void reloadRemoteConfigs() {
        AppRemoteConfig.instance().fetch();
    }

    public AdManager(Context context, boolean debugMode) {
        this.debugMode = debugMode;
        initConfigs(context);
    }

    public static void init(Context context) {
        instance = new AdManager(context);
    }

    public static void init(Context context, boolean debugMode) {
        instance = new AdManager(context, debugMode);
    }

    public static AdManager getInstance(Context context) {
        if (instance == null) {
            instance = new AdManager(context);
        }
        return instance;
    }

    public AdManager setEnableEventTrack(boolean enableEventTrack) {
        this.enableEventTrack = enableEventTrack;
        return this;
    }

    public boolean isEnableEventTrack() {
        return enableEventTrack;
    }

    public void setAppDelegate(AppDelegate appDelegate) {
        this.appDelegate = appDelegate;
    }

    public AppDelegate getAppDelegate() {
        if (appDelegate == null) {
            return () -> false;
        }
        return appDelegate;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }
}
