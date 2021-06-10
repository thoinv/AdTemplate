package com.superad.inter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.LoadAdError;
import com.superad.util.AdEventTracker;
import com.superad.util.AdLogger;


public class AdListenerImp extends SuperAdListener {

    private static final String SUCCESS = "success";
    private static final String IMPRESS = "impress";
    private static final String CLICK = "click";
    private static final String DISMISS = "dismiss";
    private static final String ERROR = "error";

    private final String adPlacement;
    private final Context context;

    public AdListenerImp(Context context, @NonNull String adPlacement) {
        super();
        this.adPlacement = adPlacement;
        this.context = context;
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();
        logEvent(adPlacement, DISMISS);
    }

    private void logEvent(String adPlacement, String event) {
        AdEventTracker.getInstance(context).log("gad_" + adPlacement + "_" + event);
    }

    @Override
    public void onAdFailedToLoad(LoadAdError var1) {
        super.onAdFailedToLoad(var1);
        AdLogger.logE("Error " + var1.getCode());
        logEvent(adPlacement, ERROR);
    }

    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
        logEvent(adPlacement, SUCCESS);
    }

    @Override
    public void onAdClicked() {
        super.onAdClicked();
        logEvent(adPlacement, CLICK);
    }

    @Override
    public void onAdImpression() {
        super.onAdImpression();
        logEvent(adPlacement, IMPRESS);
    }
}
