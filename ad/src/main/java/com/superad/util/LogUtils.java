package com.superad.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class LogUtils {
    private static final String TAG = "LOGGER";

    public static void logI(String message) {
        Log.i(TAG, composeDefaultMessage(message));
    }

    public static void logIMethodName() {
        Log.i(TAG, composeDefaultMessage(""));
    }

    public static void logI(List<Integer> message) {
        Log.i(TAG, composeDefaultMessage(TextUtils.join(",", message)));
    }

    public static void logD(String message) {
        Log.d(TAG, composeDefaultMessage(message));
    }

    public static void logE(String message) {
        Log.e(TAG, composeDefaultMessage(message));
    }

    public static void logE(Exception exception) {
        exception.printStackTrace();
    }

    private static String composeDefaultMessage(String message) {
        return getCurrentMethod() + " = " + message;
    }

    private static String getCurrentMethod() {
        try {
            StackTraceElement[] stacktraceObj = Thread.currentThread().getStackTrace();
            StackTraceElement stackTraceElement = stacktraceObj[5];
            String className = stackTraceElement.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1, className.length());
            return " [" + className + "] " + stackTraceElement.getMethodName();
        } catch (Exception e) {
            return "";
        }
    }
}
