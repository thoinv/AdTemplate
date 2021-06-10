package com.superad.config;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.superad.AdManager;
import com.superad.BuildConfig;
import com.superad.util.AdConfigFetcher;
import com.superad.util.AdLogger;
import com.superad.util.AdPrefs;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AppRemoteConfig implements ConfigStrategy {

    private static final String GAD = "gad";
    private static volatile AppRemoteConfig instance;
    private Context context;

    private AppRemoteConfig() {
    }

    public static AppRemoteConfig instance() {
        if (instance == null) {
            synchronized (AppRemoteConfig.class) {
                if (instance == null) {
                    instance = new AppRemoteConfig();
                }
            }
        }
        return instance;
    }


    public AppRemoteConfig(Context context) {
        this.context = context;
    }

    @Override
    public boolean isLiveAdMob(String key, boolean defaultValue) {
        return AdPrefs.getBoolean(key, defaultValue);
    }

    @Override
    public boolean isLiveAdMob(String key) {
        return isLiveAdMob(GAD + "_" + key, true);
    }

    @Override
    public void fetch() {
        AdConfigFetcher.getInstance().fetch(new AdConfigFetcher.OnFetchListener() {
            @Override
            public void onSuccess(FirebaseRemoteConfig firebaseRemoteConfig) {
                saveConfigByPrefix(firebaseRemoteConfig, "it_");
                saveConfigByPrefix(firebaseRemoteConfig, "nt_");
                saveConfigByPrefix(firebaseRemoteConfig, "oa_");
            }

            @Override
            public void onFail() {
                AdLogger.logI("Fetch config failed");
            }
        });
    }

    private void saveConfigByPrefix(FirebaseRemoteConfig firebaseRemoteConfig, String prefix) {
        Set<String> subConfigKeys = firebaseRemoteConfig.getKeysByPrefix(prefix);
        AdLogger.logD(subConfigKeys.toString());
        for (String subConfigKey : subConfigKeys) {
            saveConfigs(firebaseRemoteConfig, subConfigKey);
        }
    }

    private void saveConfigs(FirebaseRemoteConfig firebaseRemoteConfig, String subConfigKey) {
        if (firebaseRemoteConfig == null) {
            return;
        }
        String inputStringFirebase = firebaseRemoteConfig.getString(subConfigKey);
        if (TextUtils.isEmpty(inputStringFirebase)) {
            return;
        }

        try {
            Pattern queryLangPattern = Pattern.compile("true|false", Pattern.CASE_INSENSITIVE);
            Matcher matcher = queryLangPattern.matcher(inputStringFirebase);
            if (matcher.matches()) {
                AdPrefs.putBoolean(subConfigKey, Boolean.valueOf(inputStringFirebase));
                return;
            }
            throw new Exception("Invalid type");
        } catch (Exception exception) {
            try {
                AdPrefs.putInt(subConfigKey, Integer.parseInt(inputStringFirebase));
            } catch (NumberFormatException e) {
                AdPrefs.putString(subConfigKey, inputStringFirebase);
            }
        }
    }


    @Override
    public boolean isLivePlacement(String key) {
        boolean livePlacement = isLivePlacement(key, true);
        AdLogger.logI(key + " " + livePlacement);
        return livePlacement;
    }

    @Override
    public boolean isLivePlacement(String key, boolean defaultValue) {
        return AdPrefs.getBoolean(key, defaultValue);
    }

    @Override
    public int getAdCacheTime() {
        return AdPrefs.getInt(ConfigConstant.AD_CACHE_TIME, BuildConfig.DEBUG ? 0 : 5);
    }

    @Override
    public String getAdMobInterAdUnit(String key, String defaultVal) {
        if (isDebugMode()) {
            return "ca-app-pub-3940256099942544/1033173712";
        }
        return AdPrefs.getString(key, defaultVal);
    }

    private boolean isDebugMode() {
        return AdManager.getInstance(context).isDebugMode();
    }

    @Override
    public String getAdMobNativeAdUnit(String key, String defaultVal) {
        if (isDebugMode()) {
            return "ca-app-pub-3940256099942544/2247696110";
        }
        return AdPrefs.getString(key, defaultVal);
    }

    @Override
    public String getAdMobOpenAdUnit(String key, String defaultVal) {
        if (isDebugMode()) {
            return "ca-app-pub-3940256099942544/3419835294";
        }
        return AdPrefs.getString(key, defaultVal);
    }
}
