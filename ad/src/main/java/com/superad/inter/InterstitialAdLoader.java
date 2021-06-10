package com.superad.inter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.superad.config.ConfigLoader;
import com.superad.config.ConfigStrategy;
import com.superad.inter.cachetime.AdRequestCapping;
import com.superad.inter.cachetime.Capping;
import com.superad.util.AdLogger;
import com.superad.util.AdUtil;
import com.superad.util.DeviceUtil;

import java.util.Calendar;


public class InterstitialAdLoader {

    private final String adUnitId;
    private InterstitialAd interstitialAdObj;
    private final Context context;
    private String adPosition;
    private boolean isAdShowed;
    private String liveKey;
    private SuperAdListener adListener;

    public interface SkipAdListener {
        void onSkip();
    }

    private SkipAdListener skipAdListener;

    public InterstitialAdLoader setAdListener(SuperAdListener adListener) {
        this.adListener = adListener;
        return this;
    }

    public InterstitialAdLoader setSkipAdListener(SkipAdListener skipAdListener) {
        this.skipAdListener = skipAdListener;
        return this;
    }

    public InterstitialAdLoader setLiveKey(String liveKey) {
        this.liveKey = liveKey;
        return this;
    }

    public static InterstitialAdLoader newInstance(Context context, String adUnit) {
        return new InterstitialAdLoader(context, adUnit);
    }

    public void reload() {
        isAdShowed = false;
        load();
    }

    private InterstitialAdLoader(Context context, String adUnitId) {
        this.context = context;
        this.adUnitId = adUnitId;
    }

    public InterstitialAdLoader setAdPosition(String adPosition) {
        this.adPosition = adPosition;
        return this;
    }

    public void show(Activity activity) {
        this.isAdShowed = true;
        this.interstitialAdObj.show(activity);
    }

    public boolean isAdLoaded() {
        return this.interstitialAdObj != null;
    }

    public InterstitialAdLoader load() {
        ConfigStrategy config = new ConfigLoader(context).getConfig();
        if (!config.isLivePlacement(liveKey) || !DeviceUtil.isConnected(context)) {
            return this;
        }

        AdRequestCapping periodicJob = AdRequestCapping.create(new Capping(context, adPosition,
                config.getAdCacheTime(), Calendar.MINUTE) {
            @Override
            protected void task() {
                AdLogger.logD("[" + adPosition + "] Request ad with cache time " + config.getAdCacheTime() + " min");
                loadAd(adListener);
            }

            @Override
            protected void skip() {
                AdLogger.logD("[" + adPosition + "] Skip request ad");
                if (skipAdListener != null) {
                    skipAdListener.onSkip();
                }
            }
        });
        periodicJob.schedule();
        return this;
    }

    private void loadAd(SuperAdListener adListener) {

        if (adListener != null && TextUtils.isEmpty(adUnitId)) {
            AdLogger.logE("AD UNIT is empty");
            return;
        }
        InterstitialAd.load(context, adUnitId, AdUtil.getAdRequestBuilderWithTestDevice(context).build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitialAdObj = interstitialAd;
                        interstitialAdObj.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                AdLogger.logD(adError.getMessage());
                                if (adListener != null) {
                                    adListener.onAdFailedToShowFullScreenContent(adError);
                                }
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                                AdLogger.showCurrentMethodName();
                                isAdShowed = true;
                                if (adListener != null) {
                                    adListener.onAdImpression();
                                }
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                AdLogger.showCurrentMethodName();
                                if (adListener != null) {
                                    adListener.onAdClosed();
                                }
                            }

                        });
                        if (adListener != null) {
                            AdLogger.logD("onAdLoaded");
                            adListener.onAdLoaded();
                        }

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        AdLogger.logE(loadAdError.getMessage());
                        interstitialAdObj = null;
                        if (adListener != null) {
                            adListener.onAdFailedToLoad(loadAdError);
                        }
                    }
                });

    }

    public boolean isAdShowed() {
        return isAdShowed;
    }

    public void reset() {
        this.isAdShowed = false;
    }

}
