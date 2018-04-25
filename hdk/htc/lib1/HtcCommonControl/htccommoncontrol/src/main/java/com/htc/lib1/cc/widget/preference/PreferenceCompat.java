package com.htc.lib1.cc.widget.preference;


import android.preference.Preference;
import android.util.Log;

import com.htc.lib1.cc.BuildConfig;

import java.lang.reflect.Field;

class PreferenceCompat {
    private static final String TAG = PreferenceCompat.class.getSimpleName();
    private static Field sCanRecycleLayout;
    private static boolean sLoadCanRecycleLayout = false;

    /**
     * @return 1 means success,less than 1 means failure.
     */
    public static int setRecycleLayout(Preference preference, boolean canRecycleLayout) {

        if (sCanRecycleLayout == null && sLoadCanRecycleLayout) {
            if (BuildConfig.DEBUG) Log.d(TAG, "No mCanRecycleLayout found in Preference class");
            return -1;
        }

        if (sCanRecycleLayout == null) {
            try {
                sCanRecycleLayout = Preference.class.getDeclaredField("mCanRecycleLayout");
                sCanRecycleLayout.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            sLoadCanRecycleLayout = true;
        }

        if (sCanRecycleLayout == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "No mCanRecycleLayout found in Preference class");
            return -1;
        }

        try {
            sCanRecycleLayout.setBoolean(preference, canRecycleLayout);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
