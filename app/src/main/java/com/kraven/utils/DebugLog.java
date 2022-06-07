package com.kraven.utils; /***
 * This is free and unencumbered software released into the public domain.
 * <p/>
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * <p/>
 * For more information, please refer to <http://unlicense.org/>
 */


import android.util.Log;


import com.kraven.BuildConfig;


/**
 * Create a simple and more understandable Android logs.
 */

public class DebugLog {

    static String className;
    static String methodName;
    static int lineNumber;

    private DebugLog() {
        /* Protect from instantiations */
    }

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    private static String createLog(String log) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        buffer.append(methodName);
        buffer.append(":");
        buffer.append(lineNumber);
        buffer.append("]");
        buffer.append(log);

        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void e(String tag, String message) {
        if (!isDebuggable())
            return;

        // Throwable instance must be created before any methods
        getMethodNames(new Throwable().getStackTrace());
        Log.e(tag, message);
    }

    public static void e(String message) {
        DebugLog.e(className, createLog(message));
    }

    public static void i(String tag, String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.i(tag, message);
    }

    public static void i(String message) {
        DebugLog.i(className, createLog(message));
    }

    public static void d(String tag, String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.d(tag, createLog(message));
    }

    public static void d(String message) {
        DebugLog.d(className, createLog(message));
    }

    public static void v(String tag, String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.v(tag, createLog(message));
    }

    public static void v(String message) {
        DebugLog.v(className, createLog(message));
    }

    public static void w(String tag, String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.w(tag, message);
    }

    public static void w(String message) {
        DebugLog.w(className, createLog(message));
    }

    public static void wtf(String tag, String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.wtf(tag, createLog(message));
    }

    public static void wtf(String message) {
        DebugLog.wtf(className, createLog(message));
    }

}
