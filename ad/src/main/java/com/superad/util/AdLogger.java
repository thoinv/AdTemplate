package com.superad.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AdLogger {
    private static final String TAG = "AdLogger";

    public static void logI(String message) {
        Log.i(TAG, composeDefaultMessage(message));
    }

    public static void showCurrentMethodName() {
        Log.i(TAG, composeDefaultMessage(""));
    }

    public static void logI(List<Integer> message) {
        Log.i(TAG, composeDefaultMessage(TextUtils.join(",", message)));
    }

    public static void logD(String message) {
        Log.d(TAG, composeDefaultMessage(message));
    }

    public static void logD(int intValue) {
        Log.d(TAG, composeDefaultMessage(String.valueOf(intValue)));
    }

    public static void logE(String message) {
        Log.e(TAG, composeDefaultMessage(message));
    }

    public static void logE(Throwable exception) {
        try {
            if (exception == null) {
                return;
            }
            Log.e(TAG, exception.getMessage());
            exception.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String composeDefaultMessage(String message) {
        return getCurrentMethod() + " = " + message;
    }

    public static void writeStringAsFile(Context context, final String fileContents, String fileName) {
        try {
            AdLogger.showCurrentMethodName();
            FileWriter out = new FileWriter(new File(context.getFilesDir(), fileName));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            AdLogger.logE(e.getMessage());
        }
    }

    private static String getCurrentMethod() {
        try {
            StackTraceElement[] stacktraceObj = Thread.currentThread().getStackTrace();
            StackTraceElement stackTraceElement = stacktraceObj[5];
            String className = stackTraceElement.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            return " [" + className + "] " + stackTraceElement.getMethodName();
        } catch (Exception e) {
            return "";
        }
    }
}
