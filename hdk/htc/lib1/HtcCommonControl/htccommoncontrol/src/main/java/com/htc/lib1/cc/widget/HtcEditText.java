
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.EditText;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.graphic.StateDrawable;
import com.htc.lib1.cc.graphic.StateDrawable.ColorState;
import com.htc.lib1.cc.util.HtcCommonUtil;

public class HtcEditText extends EditText {

    /**
     * The public constant for user to set the mode of this widget. This is used for user to put this widget
     * on bright background. For example: put this widget in a white scene. This is the default value.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public static final int MODE_BRIGHT_BACKGROUND = HtcInputFieldUtil.MODE_BRIGHT_BACKGROUND;

    /**
     * The public constant for user to set the mode of this widget. This is used for user to put this widget
     * on dark background. For example: put this widget in a black scene.
     */
    public static final int MODE_DARK_BACKGROUND = HtcInputFieldUtil.MODE_DARK_BACKGROUND;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_BRIGHT_BACKGROUND, to = "MODE_BRIGHT_BACKGROUND"),
            @IntToString(from = MODE_DARK_BACKGROUND, to = "MODE_DARK_BACKGROUND")
    })
    private int mMode = MODE_BRIGHT_BACKGROUND;

    private Drawable mResBackground;

    private Drawable mBackground;

    private ColorState mLight;

    private ColorState mDark;

    @ExportedProperty(category = "CommonControl")
    private int mPressedColor;

    @ExportedProperty(category = "CommonControl")
    private int mMinHeight;

    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mRestDrawID;
    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mPressDrawID;
    @ExportedProperty(category = "CommonControl", resolveId = true)
    private float mDisabledAlpha = 0.0f;
    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mTextStyleId = 0;

    /**
     * Simple constructor
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcEditText(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attr The attributes of the XML tag that is inflating the view.
     */
    public HtcEditText(Context context, AttributeSet attr) {
        this(context, attr, R.attr.htcEditTextStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attr The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style will be applied (beyond what is
     *            included in the theme). This may either be an attribute resource, whose value will be
     *            retrieved from the current theme, or an explicit style resource.
     */
    public HtcEditText(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context, attr, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcEditText,defStyle, R.style.htcEditTextStyleDefault);
        mRestDrawID = a.getResourceId(R.styleable.HtcEditText_android_panelBackground, R.drawable.common_inputfield_rest);
        mPressDrawID = a.getResourceId(R.styleable.HtcEditText_android_panelFullBackground, R.drawable.common_inputfield_pressed);
        mTextStyleId = a.getResourceId(R.styleable.HtcEditText_android_textAppearanceLarge, R.style.input_default_m);
        mDisabledAlpha = a.getFloat(R.styleable.HtcEditText_android_disabledAlpha, HtcInputFieldUtil.DISABLED_ALPHA_LIGHT);
        int colorEmpty = a.getColor(R.styleable.HtcEditText_android_textColorPrimary, context.getResources().getColor(R.color.HtcEditText_color_empty));
        mPressedColor = HtcCommonUtil.getCommonThemeColor(getContext(), R.styleable.ThemeColor_light_category_color);
        int colorLightPress = mPressedColor;
        int colorDarkPress = mPressedColor;
        int colorFocus = mPressedColor;
        mLight = new ColorState(colorLightPress, colorFocus, colorEmpty);
        mDark = new ColorState(colorDarkPress, colorFocus, colorEmpty);

        mResBackground = a.getDrawable(R.styleable.HtcEditText_android_background);
        mMode = HtcInputFieldUtil.mapXMLMode(a.getInt(R.styleable.HtcEditText_backgroundMode, MODE_BRIGHT_BACKGROUND));
        a.recycle();

        mMinHeight = context.getResources().getDrawable(mRestDrawID).getIntrinsicHeight();
        if (null == mResBackground) {
            setMode(mMode);
        }
        setMinHeight(mMinHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() < mMinHeight) {
            setMeasuredDimension(getMeasuredWidth(), mMinHeight);
        }
    }

    /**
     * @param colorList -- the colors in diff state.
     * @return ColorStateList -- a selector
     * @hide
     * @deprecated [Module internal use]
     */
    @Deprecated
    public static ColorStateList createSelector(int[] colorList) {
        int[][] state = {
                View.PRESSED_STATE_SET, View.FOCUSED_STATE_SET, View.EMPTY_STATE_SET
        };
        ColorStateList colorStateList = new ColorStateList(state, colorList);
        return colorStateList;
    }

    /**
     * @param cs  -- colorStateList
     * @param draw -- background drawable
     * @return ColorStateDrawable
     * @hide
     * @deprecated [Module internal use]
     */
    @Deprecated
    public static Drawable getRealBackground(ColorState cs, Drawable draw) {
        int[] color = {
                cs.getPressColor(), cs.getFocusColor(), cs.getEmptyColor()
        };
        return StateDrawable.getStateDrawable(draw, createSelector(color));
    }

    /**
      * @deprecated [Sense70 not use any longer]
      * @hide
      */
    public void updateCustomThemeColor(int color[]){
    }

    private static Drawable getStateListDrawable(Drawable pressedDraw, Drawable resetDraw) {
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[] {
                android.R.attr.state_pressed, android.R.attr.state_enabled
        }, pressedDraw);
        sd.addState(new int[] {
                android.R.attr.state_focused, android.R.attr.state_enabled
        }, pressedDraw);
        sd.addState(new int[] {}, resetDraw);
        return sd;
    }

    private Drawable getModeBackground(int mode) {
        Drawable pressedDraw = null;
        Drawable restDraw = null;
        ColorState cs;
        Resources res = getResources();
        switch (mode) {
            case MODE_DARK_BACKGROUND:
                cs = mDark;
                pressedDraw = getRealBackground(cs,
                        res.getDrawable(R.drawable.common_b_inputfield_pressed));
                restDraw = getRealBackground(cs,
                        res.getDrawable(R.drawable.common_b_inputfield_rest));
                break;
            case MODE_BRIGHT_BACKGROUND:
                cs = mLight;
                pressedDraw = getRealBackground(cs,
                        res.getDrawable(mPressDrawID == 0 ? R.drawable.common_inputfield_pressed : mPressDrawID));
                restDraw = getRealBackground(cs, res.getDrawable(mRestDrawID == 0 ? R.drawable.common_inputfield_rest : mRestDrawID));
                break;
            default:
                break;
        }
        return getStateListDrawable(pressedDraw, restDraw);
    }
    /**
     * You can use this function to decide the background of this widget. There are two choices: 1. If this
     * widget is put on a bright style scene pass in the parameter HtcEditText.MODE_BRIGHT_BACKGROUND. This is
     * the default setting. 2. If this widget is put on a dark style scene pass in the parameter
     * HtcEditText.MODE_DARK_BACKGROUND
     *
     * @param mode The parameter to decide the background of this widget. You can pass in either
     *            HtcEditText.MODE_BRIGHT_BACKGROUND or HtcEditText.MODE_DARK_BACKGROUND.
     */
    public void setMode(int mode) {
        if (mode != MODE_BRIGHT_BACKGROUND && mode != MODE_DARK_BACKGROUND) {
            mMode = MODE_BRIGHT_BACKGROUND;
        } else {
            mMode = mode;
        }
        mBackground = getModeBackground(mMode);
        setBackground(mBackground);

        // >> Sense 60: default font style according to mode
        setTextAppearance(getContext(), mode == MODE_BRIGHT_BACKGROUND ? mTextStyleId : HtcInputFieldUtil.getDefaultFontStyleByMode(mMode));
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            if (mMode == MODE_DARK_BACKGROUND) {
                setAlpha(HtcInputFieldUtil.DISABLED_ALPHA_DARK);
            } else {
                setAlpha(mDisabledAlpha == 0.0f ? HtcInputFieldUtil.DISABLED_ALPHA_LIGHT : mDisabledAlpha);
            }
        } else {
            setAlpha(HtcInputFieldUtil.REST_ALPHA);
        }
        super.setEnabled(enabled);
    }

}
