
package com.htc.lib1.cc.htcFooter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.htc.htcFooter.aut.FooterActivity;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcFooterButton;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.htcFooter.aut.R;

public class FooterTest extends HtcActivityTestCaseBase {
    private static final int CHILD_VIEW_1 = 1;
    private static final int CHILD_VIEW_2 = 2;
    private static final int CHILD_VIEW_3 = 3;
    private static final int CHILD_VIEW_4 = 4;

    public FooterTest() {
        super(FooterActivity.class);
    }

    public final void test_UIGL_1Btn_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_icon, CHILD_VIEW_1, R.id.htcfooter_uigl_light_icon);
    }

    public final void test_UIGL_2Btn_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_icon, CHILD_VIEW_2, R.id.htcfooter_uigl_light_icon);
    }

    public final void test_UIGL_3Btn_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_icon, CHILD_VIEW_3, R.id.htcfooter_uigl_light_icon);
    }

    public final void test_UIGL_4Btn_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_icon, CHILD_VIEW_4, R.id.htcfooter_uigl_light_icon);
    }

    public final void test_UIGL_1Text_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_text, CHILD_VIEW_1, R.id.htcfooter_uigl_light_text);
    }

    public final void test_UIGL_2Text_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_text, CHILD_VIEW_2, R.id.htcfooter_uigl_light_text);
    }

    public final void test_UIGL_3Text_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_text, CHILD_VIEW_3, R.id.htcfooter_uigl_light_text);
    }

    public final void test_UIGL_4Text_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_text, CHILD_VIEW_4, R.id.htcfooter_uigl_light_text);
    }

    public final void test_UIGL_1Btn_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_icon, CHILD_VIEW_1, R.id.htcfooter_uigl_dark_icon);
    }

    public final void test_UIGL_2Btn_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_icon, CHILD_VIEW_2, R.id.htcfooter_uigl_dark_icon);
    }

    public final void test_UIGL_3Btn_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_icon, CHILD_VIEW_3, R.id.htcfooter_uigl_dark_icon);
    }

    public final void test_UIGL_4Btn_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_icon, CHILD_VIEW_4, R.id.htcfooter_uigl_dark_icon);
    }

    public final void test_UIGL_1Text_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_text, CHILD_VIEW_1, R.id.htcfooter_uigl_dark_text);
    }

    public final void test_UIGL_2Text_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_text, CHILD_VIEW_2, R.id.htcfooter_uigl_dark_text);
    }

    public final void test_UIGL_3Text_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_text, CHILD_VIEW_3, R.id.htcfooter_uigl_dark_text);
    }

    public final void test_UIGL_4Text_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_text, CHILD_VIEW_4, R.id.htcfooter_uigl_dark_text);
    }

    public final void test_UIGL_1Btn_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_icon, CHILD_VIEW_1, R.id.htcfooter_uigl_purelight_icon);
    }

    public final void test_UIGL_2Btn_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_icon, CHILD_VIEW_2, R.id.htcfooter_uigl_purelight_icon);
    }

    public final void test_UIGL_3Btn_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_icon, CHILD_VIEW_3, R.id.htcfooter_uigl_purelight_icon);
    }

    public final void test_UIGL_4Btn_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_icon, CHILD_VIEW_4, R.id.htcfooter_uigl_purelight_icon);
    }

    public final void test_UIGL_1Text_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_text, CHILD_VIEW_1, R.id.htcfooter_uigl_purelight_text);
    }

    public final void test_UIGL_2Text_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_text, CHILD_VIEW_2, R.id.htcfooter_uigl_purelight_text);
    }

    public final void test_UIGL_3Text_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_text, CHILD_VIEW_3, R.id.htcfooter_uigl_purelight_text);
    }

    public final void test_UIGL_4Text_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_text, CHILD_VIEW_4, R.id.htcfooter_uigl_purelight_text);
    }

    private void test(int layoutId, int widgetId) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();

        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(widgetId), this);
    }

    private void testUIGL(int layoutId, final int count, final int widgetId) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        HtcFooter hf = (HtcFooter) mActivity.findViewById(widgetId);
        final HtcFooterButton hfb1 = (HtcFooterButton) hf.findViewById(R.id.hfb_1);
        final HtcFooterButton hfb2 = (HtcFooterButton) hf.findViewById(R.id.hfb_2);
        final HtcFooterButton hfb3 = (HtcFooterButton) hf.findViewById(R.id.hfb_3);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                switch (count) {
                    case CHILD_VIEW_1:
                        hfb1.setVisibility(View.GONE);
                        hfb2.setVisibility(View.GONE);
                        hfb3.setVisibility(View.GONE);
                        break;
                    case CHILD_VIEW_2:
                        hfb1.setVisibility(View.GONE);
                        hfb2.setVisibility(View.GONE);
                        break;
                    case CHILD_VIEW_3:
                        hfb1.setVisibility(View.GONE);
                        break;
                    case CHILD_VIEW_4:
                        break;
                    default:
                        break;
                }
            }
        });
        getInstrumentation().waitForIdleSync();

        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(widgetId), this);
    }
}
