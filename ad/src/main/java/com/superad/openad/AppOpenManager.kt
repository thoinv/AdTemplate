package com.superad.openad

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.superad.AdManager
import com.superad.inter.InterstitialAdCache
import com.superad.util.AdUtil
import com.superad.util.DeviceUtil
import java.util.*

class AppOpenManager(private var myApplication: Application, private var adUnit: String) :
        LifecycleObserver, Application.ActivityLifecycleCallbacks {
    companion object {
        private const val LOG_TAG = "AppOpenManager"
    }

    private var appOpenAd: AppOpenAd? = null
    private lateinit var loadCallback: AppOpenAdLoadCallback
    private var currentActivity: Activity? = null
    private var isShowingAd: Boolean = false
    private var loadTime: Long = 0
    private var customCallback: FullScreenContentCallback? = null
    private var classForLockDisplayAd =
            mutableSetOf(AdActivity::class.java.name, RewardedAd::class.java.name)

    init {
        this.myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(ON_START)
    fun onStart() {
        showAdIfAvailable()
        Log.d(LOG_TAG, "onStart")
    }

    fun isShowingAd(): Boolean {
        return isShowingAd
    }

    fun addScreenClassNameForLockAd(screenClassName: String) {
        classForLockDisplayAd.add(screenClassName)
    }

    fun setCallback(listener: FullScreenContentCallback?) {
        this.customCallback = listener
    }

    fun canShowAd(): Boolean {
        if (currentActivity == null) {
            return false
        }

        if (!DeviceUtil.isConnected(currentActivity)) {
            Log.i(LOG_TAG, "No internet")
            return false
        }

        val currentActivityClassName = currentActivity!!::class.java.name
        val isAdActivityShown = classForLockDisplayAd.contains(currentActivityClassName)
                || InterstitialAdCache.instance().showing

        return !isShowingAd && isAdAvailable() && !isAdActivityShown
    }

    fun showAdIfAvailable() {
        if (!canShowAd()) {
            Log.d(LOG_TAG, "Can not show ad.")
            fetchAd()
            return;
        }
        Log.d(LOG_TAG, "Will show ad.")
        val fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                customCallback?.onAdDismissedFullScreenContent()
                this@AppOpenManager.appOpenAd = null
                isShowingAd = false
                fetchAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                customCallback?.onAdFailedToShowFullScreenContent(adError)
            }

            override fun onAdShowedFullScreenContent() {
                customCallback?.onAdShowedFullScreenContent()
                isShowingAd = true
            }
        }
        appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
        appOpenAd?.show(currentActivity!!)
    }

    fun fetchAd() {
        if (isAdAvailable() || !AdManager.getInstance(currentActivity).appDelegate.isPolicyAccepted
                || !DeviceUtil.isConnected(currentActivity)
        ) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.i(LOG_TAG, "onAppOpenAdFailedToLoad: " + loadAdError.message)
            }

            override fun onAdLoaded(ad: AppOpenAd) {
                super.onAdLoaded(ad)
                this@AppOpenManager.appOpenAd = ad
                this@AppOpenManager.loadTime = (Date()).time
            }

        }

        val request: AdRequest = getAdRequest()
        AppOpenAd.load(
                myApplication, adUnit, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }


    private fun getAdRequest(): AdRequest {
        return AdUtil.getAdRequestBuilderWithTestDevice(myApplication).build()
    }

    fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = (Date()).time - this.loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return (dateDifference < (numMilliSecondsPerHour * numHours))
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }


}