package com.htc.sense.commoncontrol.demo.htcbutton;

import android.os.Bundle;
import android.widget.Button;

import com.htc.lib1.cc.util.HtcStyleUtil;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class AndroidButtons extends CommonDemoActivityBase {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_android_buttons);
        Button bt = (Button) findViewById(R.id.custombutton5);
        HtcStyleUtil.applyHtcStyle(this, bt);
    }
}