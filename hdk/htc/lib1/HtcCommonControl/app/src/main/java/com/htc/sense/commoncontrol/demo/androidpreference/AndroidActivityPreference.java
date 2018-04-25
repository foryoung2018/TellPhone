
package com.htc.sense.commoncontrol.demo.androidpreference;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.preference.PreferenceUtil;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;

public class AndroidActivityPreference extends PreferenceActivity {

    private final static String TAG = "AndroidPreference";
    private String mPreferenceScreenkey;
    private PreferenceScreen mPreferenceScreen;
    private int mThemeMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CommonUtil.reloadDemoTheme(this, savedInstanceState);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        CommonUtil.initHtcActionBar(this, true, true);
        mPreferenceScreenkey = getResources().getString(R.string.screen_preference);
        mPreferenceScreen = (PreferenceScreen) findPreference(mPreferenceScreenkey);
        mPreferenceScreen.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (null != mPreferenceScreenkey && mPreferenceScreenkey.equals(preference.getKey())) {
                    if (mThemeMode == 0) {
                        PreferenceUtil.applyHtcListViewStyle(AndroidActivityPreference.this,
                            (ViewGroup) ((PreferenceScreen)
                                preference).getDialog().getWindow().getDecorView());
                    } else {
                        applyHtcListView((ViewGroup) ((PreferenceScreen)
                            preference).getDialog().getWindow().getDecorView());
                    }
                }
                return false;
            }
        });

    }

    @Override
    public void onContentChanged() {
        mThemeMode = HtcCommonUtil.getThemeType(this);
        if (mThemeMode == 0) {
            PreferenceUtil.applyHtcListViewStyle(this, (ViewGroup) getWindow().getDecorView());
        } else {
            applyHtcListView((ViewGroup) getWindow().getDecorView());
        }

        super.onContentChanged();
    }

    protected static void applyHtcListView(ViewGroup container) {
        if (null == container) {
            Log.d(TAG, "Container = null", new Exception());
            return;
        }

        if (null == container.getContext()) {
            Log.d(TAG, "container.getContext()= null", new Exception());
            return;
        }

        final Resources res = container.getContext().getResources();
        if (null == res) {
            Log.d(TAG, "container.getContext().getResources()= null", new Exception());
            return;
        }

        final ListView list = (ListView) container.findViewById(android.R.id.list);
        list.setBackgroundResource(com.htc.lib1.cc.R.drawable.common_app_bkg_dark);
        list.setPadding(0, 0, 0, 0);
        list.setDivider(null);
        list.setSelector(res.getDrawable(com.htc.lib1.cc.R.drawable.list_selector_dark));
    }

}
