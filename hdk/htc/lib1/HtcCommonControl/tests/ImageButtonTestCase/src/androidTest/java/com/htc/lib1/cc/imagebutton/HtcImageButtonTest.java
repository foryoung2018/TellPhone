
package com.htc.lib1.cc.imagebutton;

import android.view.View;

import com.htc.imagebutton.aut.HtcImageButtonAutActivity;
import com.htc.imagebutton.aut.R;
import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.ScreenShotUtil;

public class HtcImageButtonTest extends HtcActivityTestCaseBase {

    private static final int STATE_REST = 0;
    private static final int STATE_PRESS = 1;
    private static final int STATE_DISABLE = 2;

    private HtcImageButton imgbtn, imgbtndark, imgbtn_coloron, imgbtn_inpress;

    public HtcImageButtonTest() {
        super(HtcImageButtonAutActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();

        imgbtn = (HtcImageButton) mActivity.findViewById(R.id.img_light);
        imgbtndark = (HtcImageButton) mActivity.findViewById(R.id.img_dark);
        imgbtn_coloron = (HtcImageButton) mActivity.findViewById(R.id.img_coloron);
        imgbtn_inpress = (HtcImageButton) mActivity.findViewById(R.id.img_inpress);
    }

    public void test_Light_Rest() {
        test(imgbtn, STATE_REST);
    }

    public void test_Light_Press() {
        test(imgbtn, STATE_PRESS);
    }

    public void test_Light_Disable() {
        test(imgbtn, STATE_DISABLE);
    }

    public void test_Dark_Rest() {
        test(imgbtndark, STATE_REST);
    }

    public void test_Dark_Press() {
        test(imgbtndark, STATE_PRESS);
    }

    public void test_Dark_Disable() {
        test(imgbtndark, STATE_DISABLE);
    }

    public void test_AutoMotiveLight_Rest() {
        assertNotNull(imgbtn);
    }

    public void test_AutoMotiveLight_Press() {
        assertNotNull(imgbtn);
    }

    public void test_AutoMotiveLight_Disable() {
        assertNotNull(imgbtn);
    }

    public void test_AutoMotiveDark_Rest() {
        assertNotNull(imgbtndark);
    }

    public void test_AutoMotiveDark_Press() {
        assertNotNull(imgbtndark);
    }

    public void test_AutoMotiveDark_Disable() {
        assertNotNull(imgbtndark);
    }

    private void test(final HtcImageButton button, int state) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();

        switch (state) {
            case STATE_REST:
                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                        HtcImageButtonTest.this);
                break;
            case STATE_PRESS:
                EventUtil.callLongPressed(getInstrumentation(), button,
                        new EventUtil.EventCallBack() {
                            @Override
                            public void onPressedStatus(View view) {
                                getInstrumentation().waitForIdleSync();
                                mSolo.sleep(3000);
                                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                                        HtcImageButtonTest.this);
                            }
                        });
                break;
            case STATE_DISABLE:
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        button.setEnabled(false);
                    }
                });
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                        HtcImageButtonTest.this);
                break;
        }
    }

}
