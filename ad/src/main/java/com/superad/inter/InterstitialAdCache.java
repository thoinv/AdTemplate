package com.superad.inter;

public class InterstitialAdCache {
    private static InterstitialAdCache instance;
    public boolean showing = false;

    public static InterstitialAdCache instance() {
        if (instance == null) {
            instance = new InterstitialAdCache();
        }
        return instance;
    }
}
