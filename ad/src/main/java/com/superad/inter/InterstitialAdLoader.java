package com.superad.inter;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.superad.AdListenerImp;
import com.superad.config.ConfigLoader;
import com.superad.config.ConfigStrategy;
import com.superad.inter.cachetime.AdRequestCapping;
import com.superad.inter.cachetime.Capping;
import com.superad.util.AdUtil;
import com.superad.util.DeviceUtil;
import com.superad.util.LogUtils;

import java.util.Calendar;


public class InterstitialAdLoader {

    private final InterstitialAd interstitialAd;
    private final Context context;
    private String adPosition;
    private boolean isAdShowed;
    private boolean isUserClickAd;
    private String liveKey;
    private AdListener adListener;

    public interface SkipAdListener {
        void onSkip();
    }

    private SkipAdListener skipAdListener;

    public InterstitialAdLoader setAdListener(AdListener adListener) {
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
        isUserClickAd = false;
    }

    private InterstitialAdLoader(Context context, String adUnitId) {
        this.context = context;
        this.interstitialAd = new InterstitialAd(context);
        this.interstitialAd.setAdUnitId(adUnitId);
    }

    public InterstitialAdLoader setAdPosition(String adPosition) {
        this.adPosition = adPosition;
        return this;
    }


    public void show() {
        this.isAdShowed = true;
        this.interstitialAd.show();
    }

    public boolean isAdLoaded() {
        return this.interstitialAd != null && this.interstitialAd.isLoaded();
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
                if (interstitialAd == null) {
                    return;
                }
                LogUtils.logD("[" + adPosition + "] Request ad with cache time " + config.getAdCacheTime() + " min");
                loadAd(adListener);
            }

            @Override
            protected void skip() {
                LogUtils.logD("[" + adPosition + "] Skip request ad");
                if (skipAdListener != null) {
                    skipAdListener.onSkip();
                }
            }
        });
        periodicJob.schedule();
        return this;
    }

    private void loadAd(AdListener adListener) {
        interstitialAd.setAdListener(new AdListenerImp(context, adPosition) {


            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (adListener != null) {
                    adListener.onAdLoaded();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (adListener != null) {
                    adListener.onAdFailedToLoad(i);
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                isUserClickAd = true;
                if (adListener != null) {
                    adListener.onAdClicked();
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (adListener != null) {
                    adListener.onAdImpression();
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (adListener != null) {
                    adListener.onAdClosed();
                }
            }
        });
        this.interstitialAd.loadAd(AdUtil.getAdRequestBuilderWithTestDevice(context).build());
    }

    public boolean isUserClickAd() {
        return isUserClickAd;
    }

    public boolean isAdShowed() {
        return isAdShowed;
    }

    public void reset() {
        this.isAdShowed = false;
    }

}
