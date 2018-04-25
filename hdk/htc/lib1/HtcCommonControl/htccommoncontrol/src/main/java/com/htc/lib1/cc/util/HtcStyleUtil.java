package com.htc.lib1.cc.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.widget.Button;

import com.htc.lib1.cc.R;

public class HtcStyleUtil {
    private static final boolean ISLOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private static final String TAG = "HtcStyleUtil";

    public static final int BUTTON_STYLE_RAISED = 0;
    public static final int BUTTON_STYLE_RAISEDCOLOR = 1;
    public static final int BUTTON_STYLE_FLAT = 2;
    public static final int BUTTON_STYLE_FLATCOLOR = 3;

    /**
     * @hide
     * @deprecated this method will be used in AndroidButton
     */
    public static void applyHtcStyle(Context context, Button gButton) {
        if (null == context || null == gButton) {
            Log.d(TAG, "Context == null", new Exception());
            return;
        }
        gButton.setBackground(HtcStyleUtil.getButtonThemingBackground(context));
    }

    /**
     * @hide
     * @deprecated this method will be uesd in RippleDrawable when version under L21 .
     * TODO
     */
    private static Drawable getShapeDrawable(Context context) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(10);
        gradientDrawable.setColor(context.getResources().getColor(R.color.HtcButtonTheme));
        return gradientDrawable;
    }

    /**
     * @hide
     * @deprecated this method will be used in Android Button Theme.
     * TODO
     */

    public static Drawable getButtonThemingBackground(Context context) {
        if (context == null) {
            return null;
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(10);
        gradientDrawable.setColor(HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_category_color));
        StateListDrawable stateListDrawable = new StateListDrawable();
        if (ISLOLLIPOP) {
            int buttonThemeColor = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_category_color);
            Double grayLevel = Color.red(buttonThemeColor) * 0.299 + Color.green(buttonThemeColor) * 0.587 + Color.blue(buttonThemeColor) * 0.114;
            int buttoncolor;
            if (grayLevel >= 192) {
                buttoncolor = context.getResources().getColor(R.color.HtcButtonColorDepp);
            } else {
                buttoncolor = context.getResources().getColor(R.color.HtcButtonColorShallow);
            }
            RippleDrawable mRippleDrawable = new RippleDrawable(ColorStateList.valueOf(buttoncolor), gradientDrawable, null);
            stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, mRippleDrawable);
        } else {
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, getShapeDrawable(context));
            stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, gradientDrawable);
        }
        ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
        stateListDrawable.addState(new int[]{0}, colorDrawable);
        return stateListDrawable;
    }

    private static ColorStateList getThemeColorTintListForB(Context context) {
        if (context == null) {
            Log.d(TAG, "Context == null", new Exception());
            return null;
        }

        TypedArray a = context.obtainStyledAttributes(null, R.styleable.HtcButton, R.attr
            .htcButtonStyle, R.style.HtcButton);
        float alphaRatio = a.getFloat(R.styleable.HtcButton_android_disabledAlpha, 0.5f);
        a.recycle();
        int color = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_category_color);
        int disableAlphaColor = (int) (Color.alpha(color) * alphaRatio);
        int disableColor = (color & 0xFFFFFF) | (disableAlphaColor << 24);
        ColorStateList colorStateList = new ColorStateList(new int[][]{{-android.R.attr.state_enabled}, {0}},
            new int[]{disableColor, color});
        return colorStateList;
    }

    /**
     * @hide
     * @deprecated this method will be used in AndroidButton
     */
    public static void applyHtcStyle(Context context, Button gButton, int style) {
        if (null == context || null == gButton) {
            Log.d(TAG, "Context == null", new Exception());
            return;
        }

        int themeMode = HtcCommonUtil.getThemeType(context);
        applyHtcStyle(context,gButton,style,themeMode);
    }

    /**
     * @hide
     * @deprecated this method will be used in AndroidButton
     */
    public static void applyHtcStyle(Context context, Button gButton, int style,int themeMode) {
        if (null == context || null == gButton) {
            Log.d(TAG, "Context == null", new Exception());
            return;
        }

        switch (style) {
            case BUTTON_STYLE_RAISED:
                setButtonStyle(context, gButton, BUTTON_STYLE_RAISED,themeMode);
                break;
            case BUTTON_STYLE_RAISEDCOLOR:
                setButtonStyle(context, gButton, BUTTON_STYLE_RAISEDCOLOR,themeMode);
                break;
            case BUTTON_STYLE_FLAT:
                setButtonStyle(context, gButton, BUTTON_STYLE_FLAT,themeMode);
                break;
            case BUTTON_STYLE_FLATCOLOR:
                setButtonStyle(context, gButton, BUTTON_STYLE_FLATCOLOR,themeMode);
                gButton.setTextColor(getThemeColorTintListForB(context));
                break;
        }
    }

    private static void setButtonStyle(Context context, Button btn, int preferItemMode, int
        themeMode) {
        int styleable[] = R.styleable.HtcButton;
        TypedArray a = context.obtainStyledAttributes(obtainStyleId(preferItemMode,themeMode),styleable);
        Drawable bgDrawable = a.getDrawable(R.styleable.HtcButton_android_background);
        int padding = a.getDimensionPixelOffset(R.styleable.HtcButton_android_padding, R.dimen.margin_m);
        int textStyleId = a.getResourceId(R.styleable.HtcButton_android_textAppearance,
            R.style.button_primary_m);
        a.recycle();
        btn.setTextAppearance(context, textStyleId);
        if (preferItemMode == BUTTON_STYLE_RAISEDCOLOR) {
            if (ISLOLLIPOP) {
                btn.setBackgroundTintList(getThemeColorTintListForB(context));
            } else {
                bgDrawable = DrawableCompat.wrap(bgDrawable);
                DrawableCompat.setTintList(bgDrawable, getThemeColorTintListForB(context));
                btn.setBackgroundDrawable(bgDrawable);
            }
        } else {
            btn.setBackground(bgDrawable);
        }
        btn.setPadding(padding, 0, padding, 0);
    }

    static int obtainStyleId(int preferItemMode, int themeMode) {
        boolean isThemeLight = themeMode == HtcCommonUtil.THEME_LIGHT;
        switch (preferItemMode) {
            case BUTTON_STYLE_RAISED:
                return isThemeLight ? R.style.ButtonRaisedStyle : R.style.ButtonRaisedStyle_Dark;
            case BUTTON_STYLE_RAISEDCOLOR:
                return isThemeLight ? R.style.ButtonRaisedStyle_Colored : R.style.ButtonRaisedStyle_Dark_Colored;
            case BUTTON_STYLE_FLAT:
            case BUTTON_STYLE_FLATCOLOR:
                return isThemeLight ? R.style.ButtonFlatStyle : R.style.ButtonFlatStyle_Dark;
        }

        return R.style.ButtonRaisedStyle;
    }

}

