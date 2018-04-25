package com.htc.lib1.cc.HtcTintManagerTest;


import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.support.widget.HtcTintManager;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class HtcTintManagerTest extends HtcActivityTestCaseBase {
    private static final int STATE_DISABLE = 0;
    private static final int STATE_RADIO = 1;
    private RadioButton mRadioButton;
    private CheckBox mCheckBox;
    private Switch mSwitch;

    public HtcTintManagerTest() {
        super(ActivityBase.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        mRadioButton = new RadioButton(mActivity);
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                HtcTintManager.get(mActivity).tintThemeColor(mRadioButton);
            }
        });
        mCheckBox = new CheckBox(mActivity);
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                HtcTintManager.get(mActivity).tintThemeColor(mCheckBox);
            }
        });
        mSwitch = new Switch(mActivity);
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                HtcTintManager.get(mActivity).tintThemeColor(mSwitch);
            }
        });

    }


    public void test_radiochecked() {
        testRadioButton(mRadioButton, true, STATE_RADIO);
    }

    public void test_radiounchecked() {
        testRadioButton(mRadioButton, false, STATE_RADIO);
    }

    public void test_radiodisable() {
        testRadioButton(mRadioButton, false, STATE_DISABLE);
    }

    public void test_checkboxchecked() {
        testRadioButton(mCheckBox, true, STATE_RADIO);
    }

    public void test_checkboxunchecked() {
        testRadioButton(mCheckBox, false, STATE_RADIO);
    }

    public void test_checkboxdisable() {
        testRadioButton(mCheckBox, false, STATE_DISABLE);
    }

    public void test_switchchecked() {
        testRadioButton(mSwitch, true, STATE_RADIO);
    }

    public void test_switchunchecked() {
        testRadioButton(mSwitch, false, STATE_RADIO);
    }

    public void test_switchdisable() {
        testRadioButton(mSwitch, false, STATE_DISABLE);
    }


    public void testRadioButton(final CompoundButton compoundButton, final boolean isChecked, int state) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        final String fileName = ScreenShotUtil.getScreenShotName(HtcTintManagerTest.this);
        switch (state) {
            case STATE_RADIO:
                getInstrumentation().runOnMainSync(new Runnable() {

                    @Override
                    public void run() {
                        compoundButton.setChecked(isChecked);
                        mActivity.setContentView(compoundButton);
                    }
                });
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo, compoundButton, fileName);
                break;
            case STATE_DISABLE:
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        compoundButton.setEnabled(false);
                        mActivity.setContentView(compoundButton);
                    }
                });
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo,
                        compoundButton, this);
                break;
        }
    }
}