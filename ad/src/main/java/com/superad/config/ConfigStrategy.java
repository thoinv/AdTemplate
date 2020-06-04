package com.superad.config;

public interface ConfigStrategy {

    boolean isLivePlacement(String key);

    boolean isLivePlacement(String key, boolean defaultValue);

    boolean isLiveAdMob(String key, boolean defaultValue);

    boolean isLiveAdMob(String key);

    int getAdCacheTime();

}
