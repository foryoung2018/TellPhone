package com.htc.sense.commoncontrol.demo.htclistitemfragment;

import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.Fragment;

import com.htc.lib1.cc.widget.ListItem;

public class MyFragment extends Fragment {
    public static final int M1 = 0;
    public static final int M2 = 1;
    public static final int M3 = 2;
    public static final int M4 = 3;
    public static final int M5 = 4;
    public static final int M6 = 5;
    public int[] mMargin = new int[6];
    public static void setMargin(View view, int start, int top, int end, int bottom) {
        ListItem.LayoutParams lp = (ListItem.LayoutParams) view.getLayoutParams();
        lp.setMargins(start, top, end, bottom);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            lp.setMarginEnd(end);
            lp.setMarginStart(start);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(com.htc.sense.commoncontrol.demo.R.menu.htclistitem_menu, menu);
        menu.findItem(com.htc.sense.commoncontrol.demo.R.id.listitem).setChecked(MainActivity.mShowListItem);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case com.htc.sense.commoncontrol.demo.R.id.listitem:
                item.setChecked(MainActivity.mShowListItem = !MainActivity.mShowListItem);
                getActivity().recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
