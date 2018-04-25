package com.htc.sense.commoncontrol.demo.layout;

import android.os.Bundle;

import com.htc.lib1.cc.util.DrawableUtil;
import com.htc.lib1.cc.widget.HtcIconButton;
import com.htc.lib1.cc.widget.RailingLayout;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class RailingLayoutDemoActivity extends CommonDemoActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_railinglayout);
        ((RailingLayout) findViewById(R.id.dark_rl2)).setLargerModeEnabled(true);

        HtcIconButton htcIconButton = (HtcIconButton) findViewById(R.id.icon);
        htcIconButton.useSelectorWhenPressed(true);
        htcIconButton = (HtcIconButton) findViewById(R.id.icon_light_2);
        htcIconButton.useSelectorWhenPressed(true);

        htcIconButton = (HtcIconButton) findViewById(R.id.icon_light_3);
        htcIconButton.useSelectorWhenPressed(true);
        htcIconButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, DrawableUtil.overlayIcon(this, getResources().getDrawable(R.drawable.icon_btn_search_dark)), null, null);
        htcIconButton = (HtcIconButton) findViewById(R.id.icon_light_4);
        htcIconButton.useSelectorWhenPressed(true);
        htcIconButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, DrawableUtil.overlayIcon(this, getResources().getDrawable(R.drawable.icon_btn_search_dark)), null, null);
    }


}