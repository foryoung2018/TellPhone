package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.htc.lib1.cc.R;

/**
 * HtcDeleteButton
 */
public class HtcDeleteButton extends HtcCompoundButton {
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcDeleteButton(Context context) {
        this(context, null);
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
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcDeleteButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.htcDeleteButtonStyle);
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
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcDeleteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * Constructor to indicate the background mode.
     * @param context The Context the view is running in.
     * @param backgroundMode The background mode, default is Light mode.
     */
    public HtcDeleteButton(Context context, int backgroundMode) {
        super(context, backgroundMode, false, true);
        init(context, null, R.attr.htcDeleteButtonStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mSkipFirstUpDraw = true; //Add by Ahan due to disable animation
        mIsContentMultiplyRequired = true;
        mHasOnState = true;
        mTheSameWithPressOn = true;
        setButtonDrawables(context, attrs, defStyleAttr);
    }

    @Override
    public void setButtonDrawables(Drawable outer, Drawable pressed, Drawable inner, Drawable rest, Drawable on) {
        super.setButtonDrawables(outer, pressed, inner, rest, on);
        if (mContentPress != null) mContentPress.clearColorFilter();
        if (mBackgroundRest != null) mBackgroundRest.setColorFilter(mCategoryColor, PorterDuff.Mode.SRC_ATOP);
    }

    //[Ahan][2012/09/25][Override following methods for S50 rule]
    /**
     * Describe how this button draws its outer background.
     * @param canvas The canvas on which the background will be drawn.
     */
    @Override
    protected void drawOuter(Canvas canvas) {
        if (mBackgroundRest != null && isChecked() && !mIsAnimating) {
            if (!mUnCheckUpAnimating) {
                mBackgroundRest.setAlpha(HtcButtonUtil.MAX_ALPHA);
                mBackgroundRest.draw(canvas);
            }
        }
    }

    @Override
    protected void drawPressOn(Canvas canvas) {
        drawPressed(canvas);
        drawFgOn(canvas);
    }

    /**
     * Describe how this button draws its on-state foreground.
     * @param canvas The canvas on which the on-state foreground will be drawn.
     */
    @Override
    protected void drawFgOn(Canvas canvas) {
        if (mContentPress != null && !mUnCheckUpAnimating) {
            if (isChecked() && !mIsAnimating) {
                mContentPress.clearColorFilter();
            } else if (mIsAnimating) {
                mContentPress.setAlpha(HtcButtonUtil.MAX_ALPHA);
                mContentPress.setColorFilter(mMultiplyColor, PorterDuff.Mode.SRC_ATOP);
            }
            mContentPress.draw(canvas);
        } else mUnCheckUpAnimating = false;
    }
    //[Ahan][2012/09/25][End]

    @Override
    protected int getDefStyleRes() {
        // [Ahan][2012/09/25][HtcDeleteButton doesn't have dark mode on S50]
        switch (mBackgroundMode) {
            default:
                return R.style.HtcCompoundButton_HtcDeleteButton;
        }
    }

}
