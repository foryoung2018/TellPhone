package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.support.widget.HtcTintManager;


/**
 * HtcCheckBox
 */
public class HtcCheckBox extends CheckBox {

    private int mBackgroundMode = HtcButtonUtil.BACKGROUND_MODE_THEME;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     */
    public HtcCheckBox(Context context) {
        super(context);
        init(null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows
     * the theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Constructor to indicate the background mode.
     * @param context The Context the view is running in.
     * @param backgroundMode The background mode, default is Light mode.
     */
    public HtcCheckBox(Context context, int backgroundMode) {
        super(context);
        mBackgroundMode = backgroundMode;
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_THEME && attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HtcAnimationButtonMode);
            mBackgroundMode = a.getInt(R.styleable.HtcAnimationButtonMode_backgroundMode, HtcButtonUtil.BACKGROUND_MODE_THEME);
            a.recycle();
        }

        if (mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_THEME) {
            HtcTintManager.get(getContext()).tintThemeColor(this);
        } else {
            HtcTintManager.get(getContext()).tintThemeColor(this, mBackgroundMode == HtcButtonUtil.BACKGROUND_MODE_LIGHT);
        }
    }


    //------------------------------Deprecated/Delete--------------------------------------------

    /**
     * To set partial selection mode with this HtcCheckBox enable/disable.
     *
     * @param enable True to enable, false to disable.
     * @deprecated
     */
    public void setPartialSelection(boolean enable) {
    }

    /**
     * @deprecated
     */
    public void setButtonDrawables(Drawable outer, Drawable pressed, Drawable inner, Drawable rest, Drawable on) {
    }

    //[Ahan][2012/09/26][Override draw related methods to fit S50 design]
    /**
     * Describe how this button draws its press-on state.
     * @param canvas The canvas on which the press-state will be drawn.
     * @deprecated
     */
    protected void drawPressOn(Canvas canvas) {
    }

    /**
     * Describe how this button draws its outer background.
     * @param canvas The canvas on which the outer background will be drawn.
     * @deprecated
     */
    protected void drawOuter(Canvas canvas) {
    }

    /**
     * Describe how this button draws its on foreground.
     * @param canvas The canvas on which the on foreground will be drawn.
     * @deprecated
     */
    protected void drawFgOn(Canvas canvas) {
    }

    /**
     * Describe how this button draws its rest foreground.
     * @param canvas The canvas on which the rest foreground will be drawn.
     * @deprecated
     */
    protected void drawFgRest(Canvas canvas) {
    }

    /**
     * To get Bitmap array for all states of this button.
     * @param context The Context the view is running in
     * @deprecated [Not use any longer] This static API will be removed
     */
    /**@hide*/
    public static Bitmap[] compositeBitmap(Context context) {
        return null;
    }

    /**
     * This API is used to make HtcCheckbox composite Bitmaps for all state and just draw 1 bitmap for each state instead of drawing 3 or 4 drawables for each state and it just support Light mode now.
     *
     * @param reComposite True to enable DrawOnce mode, false to disable.
     * @param custom      Takes no effects now, just pass null in.
     * @deprecated
     */
    public void setDrawOnce(boolean reComposite, Drawable[] custom) {
    }

    /**
     * To get the Bitmap array for all states of this button, be sure it is in DrawOnce mode before calling this method.
     *
     * @return Bitmap array for all states of this button, if it is not in DrawOnce mode, it returns null.
     * @deprecated
     */
    protected Bitmap[] getStatesBitmap() {
        return null;
    }

    /**
     * @deprecated [Not use any longer] This API will be removed on S50.
     * TODO: Remove this API on S50.
     */
    /**
     * @hide
     */
    public void stopDrawOnce() {
    }


    /**
     * @deprecated [Not use any longer]
     * @param context context
     * @param drs custom drawable array to composite, its length should be 5 and [0] for bkgOuter, [1] for bkgPressed, [2] for bkgRest, [3] for fgOn, [4] for fgRest
     * @param defBkg not used anymore
     */
    /**
     * @hide
     */
    public static Bitmap[] compositeBitmap(Context context, Drawable[] drs, boolean defBkg) {
        return compositeBitmap(context, drs, defBkg, true, true);
    }

    /**
     * This API is used to get Bitmap array with bitmaps represent each state of this button.
     *
     * @param context context
     * @param drs     custom drawable array to composite, its length should be 5 and [0] for bkgOuter, [1] for bkgPressed, [2] for bkgRest, [3] for fgOn, [4] for fgRest
     * @param defBkg  not used anymore
     * @param pressed True for press-state multiply/overlay, false for not.
     * @param onState True for on multiply/overlay, false for not.
     * @return Bitmap array with bitmaps for each state of this button.
     * @deprecated
     */
    public static Bitmap[] compositeBitmap(Context context, Drawable[] drs, boolean defBkg, boolean pressed, boolean onState) {
        return null;
    }


    /**
     * Invoke this API will make this button to composite a bitmap array and just draw a bitmap on each state.
     * It may reduce a little time cost when draw, but please confirm the usage with control owner before you use this API.
     *
     * @param reComposite Pass true means users want to redo compositeBitmap, it should be false in general case
     * @param custom      If not null, its length should be 5 at least, [0] for bkgOuter, [1] for bkgPress, [2] for bkgInner, [3] for fgRest and [4] for fgOn
     * @param pressed     True for press-state multiply/overlay, false for not.
     * @param onstate     True for on multiply/overlay, false for not.
     * @deprecated
     */
    public void setDrawOnce(boolean reComposite, Drawable[] custom, boolean pressed, boolean onstate) {
    }


    /**
     * This method is used to draw state bitmaps when setDrawOnce is set.
     *
     * @param canvas The Canvas to which the View is rendered.
     * @deprecated
     */
    protected void drawStatesBitmap(Canvas canvas) {
    }

    /**
     * Describe how this button draws its rest-off state.
     *
     * @param canvas The canvas on which the rest-state will be drawn.
     * @deprecated
     */
    protected void drawRestOff(Canvas canvas) {
    }

    /**
     * Describe how this button draws its rest-on state.
     *
     * @param canvas The canvas on which the rest-state will be drawn.
     * @deprecated
     */
    protected void drawRestOn(Canvas canvas) {
    }

    /**
     * Describe how this button draws its press-off state.
     *
     * @param canvas The canvas on which the press-state will be drawn.
     * @deprecated
     */
    protected void drawPressOff(Canvas canvas) {
    }


    /**
     * Describe how this button draws its pressed background.
     *
     * @param canvas The canvas on which the pressed background will be drawn.
     * @deprecated
     */
    protected void drawPressed(Canvas canvas) {
    }

    /**
     * Describe how this button draws its inner background.
     *
     * @param canvas The canvas on which the inner background will be drawn.
     * @deprecated
     */
    protected void drawInner(Canvas canvas) {
    }

    /**
     * To set drawable objects with this button, set null if the asset is not required.
     *
     * @param outer          The outer drawable.
     * @param pressed        The pressed drawable, it usually be applied multiply/overlay color on.
     * @param inner          The inner drawable.
     * @param rest           The rest drawable.
     * @param on             The on drawable, it usually be applied multiply/overlay color on.
     * @param setColorFilter True to setColorFilter on pressed/on drwable, false for not.
     * @deprecated
     */
    public void setButtonDrawables(Drawable outer, Drawable pressed, Drawable inner, Drawable rest, Drawable on, boolean setColorFilter) {
    }

    /**
     * To set resource id of the drawables with this button, set 0 if the asset is not required.
     *
     * @param backgroundOuter The resource id of the outer drawable.
     * @param backgroundPress The resource id of the pressed drawable, it usually be applied multiply/overlay color on.
     * @param background      The resource id of the inner drawable.
     * @param contentRest     The resource id of the rest drawable.
     * @param contentOn       The resource id of the on drawable, it usually be applied multiply/overlay color on.
     * @deprecated
     */
    public void setButtonDrawableResources(int backgroundOuter, int backgroundPress, int background, int contentRest, int contentOn) {
    }

    /**
     * To Set the Drawables for the assets used by this Button.
     *
     * @param context      The Context the view is running in.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr The default style to apply to this view.
     * @hide
     * @deprecated [module internal use] This access level of This API will be changed on S50
     */
    public void setButtonDrawables(Context context, AttributeSet attrs, int defStyleAttr) {
    }


    /**
     * @deprecated
     */
    protected int getDefStyleRes() {
        return 0;
    }

}
