package com.htc.lib1.cc.widget;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.view.ViewDebug.ExportedProperty;
import android.widget.LinearLayout;

/**
 * RailingLayout extends android LinearLayout. This class helps user to draw
 * background and divider.
 *
 * @author vincentwang
 */
public class RailingLayout extends LinearLayout {

    private static final boolean Debug = HtcBuildFlag.Htc_DEBUG_flag;

    private Drawable mDivDrawable=null;
    @ExportedProperty(category = "CommonControl")
    private int mHorizontalMargin;
    @ExportedProperty(category = "CommonControl")
    private int mDefaultHeight = 0;
    @ExportedProperty(category = "CommonControl")
    private int mDefaultLargeHeight = 0;
    @ExportedProperty(category = "CommonControl")
    private boolean mLargerEnabled = false;

    private Resources mResource = null;

    /**
     * Use to set railinglayout to light mode
     */
    public static final int LIGHT_MODE = 0;

    /**
     * Use to set railinglayout to dark mode
     */
    public static final int DARK_MODE = 1;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = DARK_MODE, to = "DARK_MODE"),
            @IntToString(from = LIGHT_MODE, to = "LIGHT_MODE")
    })
    private int mMode = HtcButtonUtil.BACKGROUND_MODE_THEME;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     */
    public RailingLayout(Context context) {
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
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     * @deprecated [Module internal use]
     */
    /** @hide */
    public RailingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.htcRailingLayoutStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows the
     * theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @param defStyle
     *            The default style to apply to this view. If 0, no style will
     *            be applied (beyond what is included in the theme). This may
     *            either be an attribute resource, whose value will be retrieved
     *            from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     * @deprecated [Module internal use]
     */
    /** @hide */
    public RailingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mResource = context.getResources();
        init(attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        super.setShowDividers(SHOW_DIVIDER_NONE);

        mDefaultHeight = mResource.getDimensionPixelOffset(R.dimen.railing_layout_height);
        mHorizontalMargin = mResource.getDimensionPixelOffset(R.dimen.margin_l);
        mDefaultLargeHeight = mResource.getDimensionPixelOffset(R.dimen.railing_layout_large_height);

        setLayoutMinHeight();

        if (Debug) {
            android.util.Log.d("RailingLayout", "mHorizontalMargin =" + mHorizontalMargin);
            android.util.Log.d("RailingLayout", "mDefaultHeight =" + mDefaultHeight);
        }
    }

    private void init(AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HtcRailingLayout);
            if (a.hasValue(R.styleable.HtcRailingLayout_displayMode)) {
                mMode = a.getInt(R.styleable.HtcRailingLayout_displayMode, LIGHT_MODE);
            }
            a.recycle();
        }
        final int defStyleAttr = mMode == HtcButtonUtil.BACKGROUND_MODE_THEME ? defStyle : 0;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HtcRailingLayout, defStyleAttr, getDefStyleRes());
        mDivDrawable = a.getDrawable(R.styleable.HtcRailingLayout_android_divider);
        a.recycle();
    }

    private int getDefStyleRes() {
        if (mMode == DARK_MODE) {
            return R.style.HtcRailingLayout;
        } else {
            return R.style.HtcRailingLayout_Light;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.View#onAttachedToWindow()
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void onAttachedToWindow() {
        if (getLayoutParams() != null) {
            ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
            if (lp.leftMargin != mHorizontalMargin || lp.rightMargin != mHorizontalMargin) {
                if (Debug) {
                    android.util.Log.w("RailingLayout", "margin in railinglayout had been changed, correct margin");
                }

                lp.leftMargin = mHorizontalMargin;
                lp.rightMargin = mHorizontalMargin;
                requestLayout();
            }
        }
        super.onAttachedToWindow();
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        Drawable divDrawable = mDivDrawable;
        if (divDrawable == null) {
            return;
        }
        divDrawable.setBounds(0, 0, getWidth(), divDrawable.getIntrinsicHeight());
        divDrawable.draw(canvas);
        divDrawable.setBounds(0, getHeight() - divDrawable.getIntrinsicHeight(), getWidth(), getHeight());
        divDrawable.draw(canvas);

        int dividerWidth = divDrawable.getIntrinsicWidth();

        for (int loop = 0, childCount = getChildCount(); loop < childCount; loop++) {
            final View childView = getChildAt(loop);

            if (loop == 0)
                continue;

            if (childView == null)
                continue;

            if (childView.getVisibility() == View.GONE)
                continue;

            if (loop - 1 >= 0) {
                if (!hasVisibleChildAtBefore(loop)) {
                    continue;
                }
            }
            final int topBound = divDrawable.getIntrinsicHeight();
            final int bottomBound = getHeight() - divDrawable.getIntrinsicHeight();
            int startBound;
            if (this.getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                startBound = childView.getRight();
            } else {
                startBound = childView.getLeft();
            }
            int endBound = startBound + dividerWidth;
            if (startBound > 0) {
                divDrawable.setBounds(startBound - 1, topBound, endBound - 1, bottomBound);
            } else {
                divDrawable.setBounds(startBound, topBound, endBound, bottomBound);
            }

            divDrawable.draw(canvas);
        }
    }

    /**
     * You will not to use this method because we have override its implement,we have our custom
     * divider,so this method will not work
     */
    @Override
    public void setShowDividers(int showDividers) {
        // we have our own dividers so that we do not accept custom dividers.
        // super.setShowDividers(showDividers);
    }

    /**
     * Change mode
     *
     * @param mode One of {@link #LIGHT_MODE}, {@link #DARK_MODE}.
     */
    public void setMode(int mode) {
        if (mMode == mode || !isValidMode(mode)) return;

        mMode = mode;
        init(null, R.attr.htcRailingLayoutStyle);
        invalidate();
        if (Debug) {
            if (mMode == LIGHT_MODE) {
                android.util.Log.w("RailingLayout", "change to Light mode");
            } else {
                android.util.Log.w("RailingLayout", "change to Dark mode");
            }
        }
    }

    private boolean isValidMode(int mode) {
        if (mode == HtcButtonUtil.BACKGROUND_MODE_THEME || mode == DARK_MODE || mode == LIGHT_MODE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * change railing layout height
     *
     * @param enabled
     *            set true if content is icon
     */
    public void setLargerModeEnabled(boolean enabled) {
        if (mLargerEnabled != enabled) {
            mLargerEnabled = enabled;
            setLayoutMinHeight();
        }
    }

    private void setLayoutMinHeight() {
        if (mLargerEnabled) {
            setMinimumHeight(mDefaultLargeHeight);
        } else {
            setMinimumHeight(mDefaultHeight);
        }
    }

    private boolean hasVisibleChildAtBefore(int childIndex) {
        boolean hasVisibleViewBefore = false;
        for (int i = childIndex - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child != null) {
                if (child.getVisibility() != GONE) {
                    hasVisibleViewBefore = true;
                    break;
                }
            }
        }
        return hasVisibleViewBefore;
    }
}
