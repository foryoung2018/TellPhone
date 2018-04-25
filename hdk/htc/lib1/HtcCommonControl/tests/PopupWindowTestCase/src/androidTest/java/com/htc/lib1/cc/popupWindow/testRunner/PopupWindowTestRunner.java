
package com.htc.lib1.cc.popupWindow.testRunner;

import android.os.Bundle;
import android.test.InstrumentationTestSuite;

import com.htc.lib1.cc.popupWindow.popupcontainer.PopupContainerTest;
import com.htc.lib1.cc.popupWindow.popupcontainer.SeekBarPopupTest;
import com.htc.lib1.cc.popupWindow.popupmenu.ExpandListPopupMenuTest;
import com.htc.lib1.cc.popupWindow.popupmenu.ListPopupMenuTest;
import com.htc.lib1.cc.popupWindow.popupmenu.ListPopupWindowTest;
import com.htc.lib1.cc.popupWindow.popupmenu.PopupMenuTest;
import com.htc.test.HtcTestRunner;

import junit.framework.TestSuite;

public class PopupWindowTestRunner extends HtcTestRunner {

    public TestSuite getAllTests() {
        TestSuite ts = new InstrumentationTestSuite(this);
        ts.addTestSuite(SeekBarPopupTest.class);
        ts.addTestSuite(ExpandListPopupMenuTest.class);
        ts.addTestSuite(ListPopupMenuTest.class);
        ts.addTestSuite(ListPopupWindowTest.class);
        ts.addTestSuite(PopupMenuTest.class);

        Bundle arg = getArguments();
        if (arg != null) {
            String performance = arg.getString("notPerformanceAnnotation");
            if ("false".equals(performance)) {
                ts.addTestSuite(PopupContainerTest.class);
            }
        }
        return ts;
    }

}
