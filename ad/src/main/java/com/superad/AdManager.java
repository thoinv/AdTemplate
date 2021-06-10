package com.superad;

import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.superad.config.AppRemoteConfig;
import com.superad.util.AdLogger;
import com.superad.util.AdPrefs;

public class AdManager {
    private static AdManager instance;
    private boolean debugMode;

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

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }
}
