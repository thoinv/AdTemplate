package com.superad.demo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.superad.NativeAdLoader;
import com.superad.NativeAdView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.superad.demo.Constant.NT_AD;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mtrx_native_ad_view)
    NativeAdView nativeAdView;
    @BindView(R.id.mtrx_native_ad_media_view)
    NativeAdView nativeAdMediaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loadAd();
        loadAdMediaView();
    }

    private void loadAdMediaView() {
        NativeAdLoader.newInstance().setAdPosition(NT_AD)
                .setAdUnit(Constant.NT_AD_KEY)
                .setLiveKey(Constant.NT_AD_LIVE)
                .setOnAdLoaderListener(new NativeAdLoader.NativeAdLoaderListener() {
                    @Override
                    public void onAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        super.onAdLoaded(unifiedNativeAd);
                        nativeAdMediaView.show(unifiedNativeAd);
                    }

                    @Override
                    public void onAdLoadFailed(String message) {
                        super.onAdLoadFailed(message);
                        nativeAdMediaView.setVisibility(View.GONE);
                    }
                }).loadAd(this);
    }

    private void loadAd() {
        NativeAdLoader.newInstance().setAdPosition(NT_AD)
                .setAdUnit(Constant.NT_AD_KEY)
                .setLiveKey(Constant.NT_AD_LIVE)
                .setOnAdLoaderListener(new NativeAdLoader.NativeAdLoaderListener() {
                    @Override
                    public void onAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        super.onAdLoaded(unifiedNativeAd);
                        nativeAdView.show(unifiedNativeAd);
                    }

                    @Override
                    public void onAdLoadFailed(String message) {
                        super.onAdLoadFailed(message);
                        nativeAdView.setVisibility(View.GONE);
                    }
                }).loadAd(this);
    }
}