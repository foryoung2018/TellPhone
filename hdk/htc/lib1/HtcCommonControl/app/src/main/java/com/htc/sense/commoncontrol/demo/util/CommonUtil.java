
package com.htc.sense.commoncontrol.demo.util;

import com.htc.lib1.cc.util.BitmapUtil;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.HtcCommonUtil.ObtainThemeListener;
import com.htc.lib1.cc.util.ShapeUtil;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.cc.widget.HtcListItemTileImage;
import com.htc.sense.commoncontrol.demo.DemoActivity;
import com.htc.sense.commoncontrol.demo.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

public class CommonUtil {
    private static final String TAG = "CommonUtil";

    public static final String EXTRA_THEME_KEY = "theme_id";
    public static final String EXTRA_CATEGORY_KEY = "category_id";
    public static final String EXTRA_THEME_BUNDLE_KEY = "theme_bundle";
    public static final String EXTRA_HEADER_MODE_KEY = "header_mode";

    public static final int MODE_LIGHT = 0;
    public static final int MODE_DARK = 1;

    public static final int DEFAULT_THEME_ID = R.style.HtcDeviceDefault;

    private static final int STATUS_BAR_COLOR = 0x19000000;

    public static final int MODE_HEADER_COLOR = 0;
    public static final int MODE_HEADER_TEXTURE = 1;

    public static void reloadDemoTheme(final Activity activity, final int themeId, int categoryId) {
        activity.setTheme(themeId);
        if (activity instanceof DemoActivity) {
            HtcCommonUtil.initTheme(activity, categoryId, 0, true);
        } else {
            HtcCommonUtil.initTheme(activity, categoryId);
        }
    }

    /**
     * accord to themeId, reload right theme for the activity.
     *
     * @param activity
     * @param themeId
     */
    public static void reloadDemoTheme(Activity activity, int themeId) {
        reloadDemoTheme(activity, themeId, HtcCommonUtil.BASELINE);
    }

    public static Bundle applyDemoTheme(Activity activity, Bundle savedInstanceState) {
        int themeId = DEFAULT_THEME_ID;
        int categoryId = HtcCommonUtil.BASELINE;
        int header_mode = MODE_HEADER_COLOR;
        Bundle themeBundle = null;
        if (savedInstanceState != null) {
            themeBundle = savedInstanceState.getBundle(EXTRA_THEME_BUNDLE_KEY);
            if (themeBundle != null) {
                themeId = themeBundle.getInt(CommonUtil.EXTRA_THEME_KEY, DEFAULT_THEME_ID);
                categoryId = themeBundle.getInt(CommonUtil.EXTRA_CATEGORY_KEY, HtcCommonUtil.BASELINE);
                header_mode = themeBundle.getInt(CommonUtil.EXTRA_HEADER_MODE_KEY, MODE_HEADER_COLOR);
            }
        }
        if (activity != null && activity.getIntent() != null) {
            Bundle intentBundle = activity.getIntent().getBundleExtra(EXTRA_THEME_BUNDLE_KEY);
            if (intentBundle != null) {
                themeId = intentBundle.getInt(CommonUtil.EXTRA_THEME_KEY);
                categoryId = intentBundle.getInt(CommonUtil.EXTRA_CATEGORY_KEY);
                header_mode = intentBundle.getInt(CommonUtil.EXTRA_HEADER_MODE_KEY);
            }
        }

        reloadDemoTheme(activity, themeId, categoryId);

        if (themeBundle == null) themeBundle = new Bundle();
        themeBundle.putInt(EXTRA_THEME_KEY, themeId);
        themeBundle.putInt(EXTRA_CATEGORY_KEY, categoryId);
        themeBundle.putInt(EXTRA_HEADER_MODE_KEY, header_mode);
        return themeBundle;
    }

    /**
     * @deprecated please use {@link #applyDemoTheme(Activity, Bundle)} instead.
     */
    public static int reloadDemoTheme(Activity activity, Bundle savedInstanceState) {
        Bundle themeBundle = applyDemoTheme(activity, savedInstanceState);
        return themeBundle.getInt(CommonUtil.EXTRA_THEME_KEY);
    }

    /**
     * @deprecated just to demo
     */
    public static void setThemeListener(final Activity activity) {
        HtcCommonUtil.setObtianThemeListener(new ObtainThemeListener() {
            @Override
            public int onObtainThemeColor(int index, int themeColor) {
                TypedArray typedArray = activity.getTheme().obtainStyledAttributes(R.styleable.ThemeColor);
                int retColor = typedArray.getColor(index, themeColor);
                typedArray.recycle();
                return retColor;
            }
        });
    }

    public static ActionBarExt initHtcActionBar(final Activity activity, boolean enableBackup, boolean enableTitle) {
        return initHtcActionBar(activity, enableBackup, enableTitle, MODE_HEADER_COLOR);
    }

    public static ActionBarExt initHtcActionBar(final Activity activity, boolean enableBackup, boolean enableTitle, int headerCategory) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) return null;

        ActionBarExt actionBarExt = new ActionBarExt(activity, actionBar);
        Drawable headerBackground = null;
        if (headerCategory == MODE_HEADER_TEXTURE) {
            headerBackground = HtcCommonUtil.getCommonThemeTexture(activity, com.htc.lib1.cc.R.styleable.CommonTexture_android_headerBackground);
        }
        if (headerBackground == null) {
            headerBackground = new ColorDrawable(HtcCommonUtil.getCommonThemeColor(activity, com.htc.lib1.cc.R.styleable.ThemeColor_multiply_color));
        }
        actionBarExt.setBackgroundDrawable(headerBackground);

        if (enableBackup) {
            actionBarExt.getCustomContainer().setBackUpEnabled(true);
            actionBarExt.getCustomContainer().setBackUpOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }
        if (enableTitle && !TextUtils.isEmpty(activity.getTitle())) {
            updateCommonTitle(activity, actionBarExt, null, activity.getTitle());
        }
        return actionBarExt;
    }

    public static ActionBarDropDown updateCommonTitle(Context context, ActionBarExt actionBarExt, ActionBarDropDown actionBarDropDown, CharSequence title) {
        if (context == null || actionBarExt == null) return null;

        ActionBarDropDown dropDown = actionBarDropDown;
        if (actionBarDropDown == null) {
            dropDown = new ActionBarText(context);
            actionBarExt.getCustomContainer().addCenterView(dropDown);
        }
        dropDown.setPrimaryText(resolveCommonTitle(title.toString()));
        return dropDown;
    }

    public static String resolveCommonTitle(String title) {
        if (TextUtils.isEmpty(title)) return null;

        int lastIndex = title.lastIndexOf("/");
        if (lastIndex > 0) {
            title = title.substring(lastIndex + 1);
        }
        return title;
    }

    /**
     * apply style for ListView
     *
     * @param listView The ListView which should be applied style
     * @param style The Style Which ListView should apply,such as HTCLISTVIEW_STYLE_LIGHT or
     *            HTCLISTVIEW_STYLE_DARK
     */
    public static void applyHtcListViewStyle(ListView listView, int style) {
        if (MODE_LIGHT == style) {
            applyHtcListViewStyle(listView, com.htc.lib1.cc.R.drawable.inset_list_divider, com.htc.lib1.cc.R.drawable.list_selector_light, Color.WHITE);
        } else if (MODE_DARK == style) {
            applyHtcListViewStyle(listView, com.htc.lib1.cc.R.drawable.inset_list_divider_dark, com.htc.lib1.cc.R.drawable.list_selector_dark, Color.BLACK);
        } else {
            Log.e(TAG, "The style is woring,it should be HTCLISTVIEW_STYLE_LIGHT or HTCLISTVIEW_STYLE_DARK", new Exception());
        }
    }

    private static void applyHtcListViewStyle(ListView listView, int dividerId, int selectorId, int backgroundColor) {
        if (null == listView) {
            Log.e(TAG, "listView cannot be null", new Exception());
            return;
        }
        final Resources res = listView.getResources();
        if (null == res) {
            Log.e(TAG, "listView.getResources() cannot be null", new Exception());
            return;
        }
        listView.setDivider(res.getDrawable(dividerId));
        listView.setSelector(selectorId);
        listView.setBackgroundColor(backgroundColor);
    }

    public static int getApBackgroundColor(Context context, int mode) {
        if (mode == MODE_DARK) {
            return HtcCommonUtil.getCommonThemeColor(context, com.htc.lib1.cc.R.styleable.ThemeColor_dark_ap_background_color);
        } else {
            return HtcCommonUtil.getCommonThemeColor(context, com.htc.lib1.cc.R.styleable.ThemeColor_ap_background_color);
        }
    }

    public static Drawable getWindowBackground(Activity activity) {
        TypedValue out = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.windowBackground, out, true);
        if (out.resourceId > 0) return activity.getResources().getDrawable(out.resourceId);
        return null;
    }


    public static void setupWindowBkgDrawable(Activity activity, int headerCategory) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);

            Drawable windowBackground = null;
            if (headerCategory == MODE_HEADER_TEXTURE) {
                windowBackground = HtcCommonUtil.getCommonThemeTexture(activity, com.htc.lib1.cc.R.styleable.CommonTexture_android_windowBackground);
            }
            if (windowBackground == null) {
                windowBackground = new ColorDrawable(HtcCommonUtil.getCommonThemeColor(activity, com.htc.lib1.cc.R.styleable.ThemeColor_multiply_color));
            }
            Drawable frontLayer = getWindowBackground(activity);
            if (windowBackground != null && frontLayer != null) {
                Drawable[] allLayer = {windowBackground, frontLayer};
                LayerDrawable mWindowBgLayerDrawable = new LayerDrawable(allLayer);

                mWindowBgLayerDrawable.setLayerInset(1, 0, getStatusBarHeight(activity), 0, 0);
                activity.getWindow().setBackgroundDrawable(mWindowBgLayerDrawable);
            }
        }
    }

    public static int getStatusBarHeight(Context context) {
        // first set default 25 dp and then convert it to px
        float statusBarHeight = (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics());
        int id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0) {
            statusBarHeight = context.getResources().getDimension(id);
        }
        return (int) Math.ceil(statusBarHeight);
    }

    /**
     * @hide
     * @deprecated this method will be used in floatingActionButton for painting .
     */
    public static void applyHtcThemeColor(Context context, FloatingActionButton floatingActionButton) {
        if (null == context || null == floatingActionButton) {
            Log.d("TAG", "Context == null", new Exception());
            return;
        }
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(HtcCommonUtil.getCommonThemeColor(context, com.htc.lib1.cc.R.styleable.ThemeColor_category_color)));
    }

    public static void wrapBorderDrawable(int resId, Context context, ImageView imageView) {
        RoundedBitmapDrawable dr = null;
        try {
            dr = BitmapUtil.getRoundedBitmapDrawable(context, resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageDrawable(dr);
        imageView.setBackground(ShapeUtil.createRoundStroke(context));
    }

    public static void wrapBorderDrawable(int resId, Context context, HtcListItemTileImage imageView) {
        RoundedBitmapDrawable dr = null;
        try {
            dr = BitmapUtil.getRoundedBitmapDrawable(context, resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setTileImageDrawable(dr);
        imageView.getTileImage().setBackground(ShapeUtil.createRoundStroke(context));
    }

    public static String[][] parseDimensionalArray(Context context, int arrayId) {
        String[] str = context.getResources().getStringArray(arrayId);
        String[][] ssStrings = new String[str.length][];
        for (int i = 0; i < str.length; i++) {

            String[] bbStrings = str[i].split(",");
            if (bbStrings == null) bbStrings = new String[]{str[i]};

            if (ssStrings[i] == null) ssStrings[i] = new String[bbStrings.length];

            for (int j = 0; j < bbStrings.length; j++) {
                ssStrings[i][j] = bbStrings[j];
            }
        }
        return ssStrings;
    }
}
