package com.htc.test;

import java.util.Locale;

import com.robotium.solo.Solo;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnitRunner;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

public class HtcTestRunner extends AndroidJUnitRunner {
    private static final String TAG = "HtcTestRunner";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    static {
        System.setProperty("jacoco-agent.destfile", "/mnt/sdcard/coverage.ec");
    }

    String mTheme = null;
    int mOrientation = Solo.PORTRAIT;
    String mFontStyle = null;
    int mCategoryId = 0;
    private Bundle mArguments;
    private int mDensity = 0;

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        Locale preferredLocale = getPreferredLocale();
        if (!preferredLocale.equals(Locale.getDefault())) {
            Resources res = getTargetContext().getResources();
            Configuration config = res.getConfiguration();
            config.locale = preferredLocale;
            Locale.setDefault(preferredLocale);
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        super.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void onCreate(Bundle arguments) {
        Log.d(TAG, "origin coverage = " + arguments.getBoolean("coverage"));
        Log.d(TAG, "origin coverageFile = " + arguments.getString("coverageFile"));
        arguments.putBoolean("coverage", true);
        super.onCreate(arguments);
        Log.d(TAG, "coverage = " + arguments.getBoolean("coverage"));
        Log.d(TAG, "coverageFile = " + arguments.getString("coverageFile"));
        mArguments = arguments;
        mTheme = arguments.getString("theme");
        mFontStyle = arguments.getString("font");
        String orientation = arguments.getString("orientation");
        mCategoryId = arguments.getInt("category");
        String strDensity =  arguments.getString("density");
        if (null != strDensity) {
            mDensity = Integer.valueOf(strDensity);
        }

        if ("portrait".equals(orientation)) {
            mOrientation = Solo.PORTRAIT;
        } else if ("landscape".equals(orientation)) {
            mOrientation = Solo.LANDSCAPE;
        }

        Log.d(TAG, "mFontStyle=" + mFontStyle);
        Log.d(TAG, "orientation=" + orientation + " " + mOrientation);
    }

    public String getThemeName() {
        return mTheme;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public String getFontStyle() {
        return mFontStyle;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    protected Locale getPreferredLocale() {
        return Locale.ENGLISH;
    }

    public Bundle getArguments() {
        return mArguments;
    }

    public int getDensity() {
        return mDensity;
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        try {
            Class rt = Class.forName("org.jacoco.agent.rt.RT");
            Method getAgent = rt.getMethod("getAgent");
            Method dump = getAgent.getReturnType().getMethod("dump", boolean.class);
            Object agent = getAgent.invoke(null);
            dump.invoke(agent, false);
            Log.d(TAG, "finish");
        } catch (Throwable e) {
            Log.d(TAG, "finish exception", e);
        }

        super.finish(resultCode, results);
    }
}
