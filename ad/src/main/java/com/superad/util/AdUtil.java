package com.superad.util;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.superad.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdUtil {
    public static AdRequest.Builder getAdRequestBuilderWithTestDevice(Context context) {
        return new AdRequest.Builder();
    }
}