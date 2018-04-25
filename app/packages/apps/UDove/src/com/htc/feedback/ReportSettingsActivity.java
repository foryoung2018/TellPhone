package com.htc.feedback;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.theme.NavBarColorUtil;
import com.htc.lib2.configuration.HtcWrapConfiguration;
import com.htc.feedback.reportagent.Common;

public class ReportSettingsActivity extends Activity{
    private static final String TAG = "ReportSettingsActivity";
    private static final boolean LOG = Common.DEBUG;
    private int mShowUsage;
    private float mHtcFontscale = 0.0f;
    private ActionBarContainer mActionBarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG)
            Log.d(TAG, "onCreate");
        // sy_wang, 20131218, To apply Htc Huge font size
        HtcWrapConfiguration.applyHtcFontscale(this);
    	mHtcFontscale = getResources().getConfiguration().fontScale;
        super.onCreate(savedInstanceState);
        mShowUsage = Settings.Secure.getInt(this.getContentResolver(), Common.SHOW_HTC_APPLICATION_LOG, 1);
        initActionBar();

        //navigation bar color change feature(Only for SW navigation bar and 18:9 device)
        NavBarColorUtil.setNavBarBkg(getWindow());

        if (mShowUsage != 1)
        	setTitle(R.string.setting_name_error_only);
        else
        	setTitle(R.string.setting_name);
        
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
      
        SettingsPreferenceFragment fragments = new SettingsPreferenceFragment();
        fragmentTransaction.replace(android.R.id.content, fragments);
        fragmentTransaction.commit();
                
    }

    private void initActionBar() {
    	if (LOG)
            Log.d(TAG, "initActionBar");
        ActionBarExt actionBar = new ActionBarExt(ReportSettingsActivity.this, ReportSettingsActivity.this.getActionBar());
        if(actionBar == null) {
			Log.w(TAG, "actionBar is not ready.");
			return;
		}
        mActionBarContainer = actionBar.getCustomContainer();
        if (mActionBarContainer == null) {
            Log.w(TAG, "mActionBarContainer is not ready.");
            return;
        }
        mActionBarContainer.setBackUpEnabled(true);
        mActionBarContainer.setBackUpOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                ReportSettingsActivity.this.finish();
            }
        });
        
        ActionBarText actionBarText = new ActionBarText(ReportSettingsActivity.this);
        mActionBarContainer.addCenterView(actionBarText);
        if (mShowUsage != 1)
        	actionBarText.setPrimaryText(R.string.setting_name_error_only);
        else
        	actionBarText.setPrimaryText(R.string.setting_name);
    }
        
    @Override
    public void onResume() {
        if (LOG)
            Log.d(TAG, "onResume()");
        super.onResume();
        // sy_wang, 20151224, To apply Htc Huge font size
        boolean needRecreate = false;
        needRecreate |= HtcWrapConfiguration.checkHtcFontscaleChanged(this, mHtcFontscale);

        if (needRecreate == true) {
        	getWindow().getDecorView().postOnAnimation(new Runnable() {
        		@Override
        		public void run() {
        			HtcCommonUtil.notifyChange(ReportSettingsActivity.this, HtcCommonUtil.TYPE_THEME);
        			recreate();
        		}
        	});
        }
    }

}
