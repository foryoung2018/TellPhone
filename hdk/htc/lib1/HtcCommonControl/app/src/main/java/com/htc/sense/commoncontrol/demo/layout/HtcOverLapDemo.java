
package com.htc.sense.commoncontrol.demo.layout;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.htc.lib1.cc.widget.HtcAutoCompleteTextView;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcOverlapLayout;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.WidgetDataPreparer;

public class HtcOverLapDemo extends CommonDemoActivityBase {

    private HtcOverlapLayout overlayLayout = null;
    private int mThemeFlag = -1;
    private final static String THEME_FLAG = "THEME_FLAG";
    private final static int THEME_HOLO = 0;
    private final static int THEME_MATERIAL = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        mThemeFlag= i.getIntExtra(THEME_FLAG, -1);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.overlaplayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        overlayLayout = (HtcOverlapLayout) findViewById(R.id.overlap_layout);
        LinearLayout ll=(LinearLayout)findViewById(R.id.ll_content);
        initContent(ll);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getActionBar().addTab(getActionBar().newTab().setText("Translucent Off").setTabListener(new TabListener() {
            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                // overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub
            }
        }));
        getActionBar().addTab(getActionBar().newTab().setText("Translucent On").setTabListener(new TabListener() {

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                // overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                // overlayLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }
        }));
    }

    @Override
    public void setTheme(int resid) {
        switch (mThemeFlag) {
            case THEME_HOLO:
                Toast.makeText(this,"setTheme(holo)",Toast.LENGTH_SHORT).show();
                resid = android.R.style.Theme_Holo_Light;
                break;
            case THEME_MATERIAL:
                Toast.makeText(this,"setTheme(materital)",Toast.LENGTH_SHORT).show();
                resid = android.R.style.Theme_Material_Light;
                break;
        }
        super.setTheme(resid);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, THEME_HOLO, 0, "theme holo");
        menu.add(0, THEME_MATERIAL, 0, "theme material");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case THEME_HOLO:
                mThemeFlag=THEME_HOLO;
                break;

            case THEME_MATERIAL:
                mThemeFlag=THEME_MATERIAL;
                break;
        }
        sendBroadcast();
        return true;
    }

    private void initContent(LinearLayout ll) {
        for (int i = 0; i < 10; i++) {
            HtcListItem item = new HtcListItem(this);
            HtcListItem1LineCenteredText tv = new HtcListItem1LineCenteredText(this);
            HtcCheckBox cb = new HtcCheckBox(this);
            HtcAutoCompleteTextView hatv = new HtcAutoCompleteTextView(this);
            hatv.setHint("HtcAutoComplete");
            WidgetDataPreparer.prepareAdapater(this, hatv);
            tv.setText("Position " + i + " :");

            item.addView(tv);
            item.addView(hatv);
            item.addView(cb);
            ll.addView(item);
        }
    }

    public void sendBroadcast() {
        Intent intent = new Intent();
        intent.putExtra(THEME_FLAG, mThemeFlag);
        intent.setAction(HtcOverLapThemeChangeReciever.ACTION_THEME_CHANGE);
        finish();
        sendBroadcast(intent);
    }

    public void showActionBar(View view) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null && !actionBar.isShowing()) {
            actionBar.show();
            overlayLayout.isActionBarVisible(true);
        }
    }

    public void hideActionBar(View view) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null && actionBar.isShowing()) {
            actionBar.hide();
            overlayLayout.isActionBarVisible(false);
            // overlayLayout.setInsetStatusBar(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void applyCustomWindowFeature() {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    }
}
