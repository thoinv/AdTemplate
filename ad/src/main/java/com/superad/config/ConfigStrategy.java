package com.superad.config;

public interface ConfigStrategy {

    void fetch();

    boolean isLivePlacement(String key);

    boolean isLivePlacement(String key, boolean defaultValue);

    boolean isLiveAdMob(String key, boolean defaultValue);

    boolean isLiveAdMob(String key);

    int getAdCacheTime();

    String getAdMobInterAdUnit(String key, String defaultVal);

    String getAdMobNativeAdUnit(String key, String defaultVal);

    String getAdMobOpenAdUnit(String key, String defaultVal);
}
