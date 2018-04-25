
package com.htc.lib1.cc.adapterview;

import android.view.KeyEvent;
import android.widget.TextView;

import com.htc.adapterview.aut.R;
import com.htc.adapterview.aut.TableViewDemo;
import com.htc.lib1.cc.view.table.TableView;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class TableViewDemoTest extends HtcActivityTestCaseBase {

    public TableViewDemoTest() {
        super(TableViewDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testDefaultSnapShot() {
        TableView tableView = (TableView) getActivity().findViewById(R.id.tableview);
        mSolo.waitForView(tableView);
        mSolo.sleep(1000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, tableView, this);
    }

    public void testFocusException() {
        final TextView tv = (TextView) mActivity.findViewById(R.id.tv);
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                tv.requestFocus();
            }
        });
        mSolo.sleep(1000);
        mSolo.sendKey(KeyEvent.KEYCODE_TAB);
    }
}
