package com.htc.sense.commoncontrol.demo.griditem;

import android.content.res.Configuration;
import android.os.Bundle;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcGridView;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;

public class HtcGridViewBaseActivity extends CommonDemoActivityBase {
    private int mColumnsPortrait = 3;
    private int mColumnsLandscape = 5;
    protected HtcGridView mGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isAutomotiveMode = HtcCommonUtil.getThemeType(this) == HtcCommonUtil.THEME_CAR;
        mColumnsPortrait = isAutomotiveMode ? 2 : 3;
        mColumnsLandscape = isAutomotiveMode ? 4 : 5;
        init();
        setNumColumns(getResources().getConfiguration().orientation);
    }

    protected void init() {
    }

    private void setNumColumns(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGrid.setNumColumns(mColumnsPortrait);
        } else {
            mGrid.setNumColumns(mColumnsLandscape);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setNumColumns(newConfig.orientation);
    }
}
