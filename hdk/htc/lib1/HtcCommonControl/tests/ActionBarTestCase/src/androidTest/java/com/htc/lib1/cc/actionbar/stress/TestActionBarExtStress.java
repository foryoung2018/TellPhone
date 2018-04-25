
package com.htc.lib1.cc.actionbar.stress;

import com.htc.actionbar.aut.ActionBarMockActivity;
import com.htc.lib1.cc.actionbar.ActionBarTestUtil;
import com.htc.lib1.cc.actionbar.ActionBarWidgetsFactory;
import com.htc.lib1.cc.actionbar.HtcActionBarActivityTestCase;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class TestActionBarExtStress extends HtcActionBarActivityTestCase {

    private static final int STRESSNUM = 100;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        initActionBarContainers();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarTestUtil.addCenterViewCompat(mActionBarSearchContainer, ActionBarWidgetsFactory.createActionBarSearch(mActivity));
                ActionBarTestUtil.addRightViewCompat(mActionBarSearchContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity));
            }
        });
    }

    public TestActionBarExtStress() {
        super(ActionBarMockActivity.class);
    }

    public void testActionBarExt_stressSwitchContainer() {
        for (int i = 0; i < STRESSNUM; i++) {
            try {
                this.runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActionBarExt.switchContainer();
                    }
                });
            } catch (Throwable e) {
                fail(e.getMessage());
            }
        }
    }
}
