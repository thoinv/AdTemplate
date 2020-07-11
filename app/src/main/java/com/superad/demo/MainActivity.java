package com.superad.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.superad.AdListenerImp;
import com.superad.NativeAdLoader;
import com.superad.NativeAdView;
import com.superad.inter.InterstitialAdLoader;
import com.superad.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.superad.demo.Constant.IT_AD_KEY;
import static com.superad.demo.Constant.NT_AD;
import static com.superad.demo.Constant.IT_INTER_AD;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mtrx_native_ad_view)
    NativeAdView nativeAdView;
    @BindView(R.id.mtrx_native_ad_media_view)
    NativeAdView nativeAdMediaView;

    private InterstitialAdLoader interstitialAdLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loadAd();
        loadAdMediaView();
        preloadInterAd();
    }

    @OnClick(R.id.bt_show_inter_ad)
    void showInterAd(){
        if (interstitialAdLoader != null && interstitialAdLoader.isAdLoaded()) {
            interstitialAdLoader.show();
            return;
        }
        Toast.makeText(this, "Load ad first", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (interstitialAdLoader != null && interstitialAdLoader.isAdShowed()) {
            interstitialAdLoader.reload();
        }
    }

    private void preloadInterAd() {
        interstitialAdLoader = InterstitialAdLoader.newInstance(this, IT_AD_KEY);
        interstitialAdLoader.setAdListener(new AdListenerImp(this, IT_INTER_AD));
        interstitialAdLoader.setAdPosition(IT_INTER_AD);
        interstitialAdLoader.load();
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