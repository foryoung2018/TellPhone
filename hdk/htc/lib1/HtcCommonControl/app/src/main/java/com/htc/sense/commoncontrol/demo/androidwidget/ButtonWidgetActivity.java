package com.htc.sense.commoncontrol.demo.androidwidget;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;

import com.htc.lib1.cc.util.HtcStyleUtil;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class ButtonWidgetActivity extends CommonDemoActivityBase {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_button_widget);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        CommonUtil.applyHtcThemeColor(this, fab);

        Button btnRaise = (Button) findViewById(R.id.raised);
        HtcStyleUtil.applyHtcStyle(this, btnRaise, HtcStyleUtil.BUTTON_STYLE_RAISED);

        Button btnRaiseDis = (Button) findViewById(R.id.raisedDis);
        HtcStyleUtil.applyHtcStyle(this, btnRaiseDis, HtcStyleUtil.BUTTON_STYLE_RAISED);

        Button btnRaiseColor = (Button) findViewById(R.id.raisedColor);
        HtcStyleUtil.applyHtcStyle(this, btnRaiseColor, HtcStyleUtil.BUTTON_STYLE_RAISEDCOLOR);

        Button btnRaiseColorDis = (Button) findViewById(R.id.raisedColorDis);
        HtcStyleUtil.applyHtcStyle(this, btnRaiseColorDis, HtcStyleUtil.BUTTON_STYLE_RAISEDCOLOR);

        Button btnFlat = (Button) findViewById(R.id.flat);
        HtcStyleUtil.applyHtcStyle(this, btnFlat, HtcStyleUtil.BUTTON_STYLE_FLAT);

        Button btnFlatDis = (Button) findViewById(R.id.flatDis);
        HtcStyleUtil.applyHtcStyle(this, btnFlatDis, HtcStyleUtil.BUTTON_STYLE_FLAT);

        Button btnFlatColor = (Button) findViewById(R.id.flatColor);
        HtcStyleUtil.applyHtcStyle(this, btnFlatColor, HtcStyleUtil.BUTTON_STYLE_FLATCOLOR);

        Button btnFlatColorD = (Button) findViewById(R.id.flatColorDis);
        HtcStyleUtil.applyHtcStyle(this, btnFlatColorD, HtcStyleUtil.BUTTON_STYLE_FLATCOLOR);
    }
}
