
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.LogUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * A widget can be used in Htc action bar.
 */
public class ActionBarDropDown extends ViewGroup {
    /**
     * @hide
     */
    public static final int MODE_DEFAULT = Integer.MIN_VALUE;
    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    public static final int MODE_EXTERNAL = 1;

    /**
     * Automotive mode.
     * @deprecated Deprecated in Sense80,please do not use it!
     */
    public static final int MODE_AUTOMOTIVE = 2;

    /**
     * One TextView multiline mode.
     */
    public static final int MODE_ONE_MULTIILINE_TEXTVIEW = 3;

    private TextView mPrimaryView = null;
    private TextView mSecondaryView = null;
    private TextView mCounterView = null;
    private ImageView mArrowView = null;

    private boolean mEnableOneMultiline = false;
    private int mSingleTextAppearance;
    private int mPrimaryTextAppearance;
    private int mSecondaryTextAppearance;
    private int mMultilinePrimaryTextAppearance;
    private Drawable mArrowDrawable;
    private int mArrowDrawableLeftMargin;
    private float mPrimaryTopOffset;
    private float mSecondaryTopOffset;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param mode contruct with specific mode
     */
    public ActionBarDropDown(Context context, int mode) {
        this(context, null, R.attr.actionBarDropDownStyle, mode);
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ActionBarDropDown(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called when a view is
     * being constructed from an XML file, supplying attributes that were specified in the XML file.
     * This version uses a default style of 0, so the only attribute values applied are those in the
     * Context's Theme and the given AttributeSet.
     * <p>
     * The method onFinishInflate() will be called after all children have been added.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     * @hide
     */
    public ActionBarDropDown(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.actionBarDropDownStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute. This
     * constructor of View allows subclasses to use their own base style when they are inflating.
     * For example, a Button class's constructor would call this version of the super class
     * constructor and supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes (in particular its
     * background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     *            resource that supplies default values for the view. Can be 0 to not look for
     *            defaults.
     * @see #View(Context, AttributeSet)
     * @hide
     */
    public ActionBarDropDown(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, HtcButtonUtil.BACKGROUND_MODE_THEME);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute. This
     * constructor of View allows subclasses to use their own base style when they are inflating.
     * For example, a Button class's constructor would call this version of the super class
     * constructor and supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes (in particular its
     * background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     *            resource that supplies default values for the view. Can be 0 to not look for
     *            defaults.
     * @see #View(Context, AttributeSet)
     * @hide
     */
    public ActionBarDropDown(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr);
        if (isValidMode(mode)) {
            if (mode == MODE_ONE_MULTIILINE_TEXTVIEW) {
                setOneMultiLineTextView(true);
            } else {
                mSupportMode = mode;
            }
        }

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        // setup the module overall environment
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        // inflate the external layout and merge to current layout
        LayoutInflater.from(context).inflate(R.layout.action_dropdown, this, true);

        mArrowView = (ImageView) findViewById(R.id.arrow);
        mPrimaryView = (TextView) findViewById(R.id.primary);
        mSecondaryView = (TextView) findViewById(R.id.secondary);
        mCounterView = (TextView) findViewById(R.id.counter);
        // check the layout resource correctness
        if (mPrimaryView == null || mSecondaryView == null || mArrowView == null) throw new RuntimeException("inflate layout resource incorrect");

        init(attrs, defStyleAttr);

        setBackground(ActionBarUtil.getActionMenuItemBackground(context));

    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final int defStyleAttrId = mSupportMode == HtcButtonUtil.BACKGROUND_MODE_THEME ? defStyleAttr : 0;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HtcActionBarDropDown, defStyleAttrId, getDefStyleRes());
        mSingleTextAppearance = a.getResourceId(R.styleable.HtcActionBarDropDown_android_textAppearance, R.style.fixed_title_primary_m);
        mPrimaryTextAppearance = a.getResourceId(R.styleable.HtcActionBarDropDown_android_titleTextStyle, R.style.fixed_title_primary_m);
        mSecondaryTextAppearance = a.getResourceId(R.styleable.HtcActionBarDropDown_android_subtitleTextStyle, R.style.fixed_title_secondary_m);
        mMultilinePrimaryTextAppearance = a.getResourceId(R.styleable.HtcActionBarDropDown_android_textAppearanceSmall, R.style.fixed_title_primary_xs);
        mArrowDrawable = a.getDrawable(R.styleable.HtcActionBarDropDown_android_drawable);
        mArrowDrawableLeftMargin = a.getDimensionPixelOffset(R.styleable.HtcActionBarDropDown_android_drawablePadding, getResources().getDimensionPixelOffset(R.dimen.spacing));
        mPrimaryTopOffset = a.getFloat(R.styleable.HtcActionBarDropDown_android_innerRadiusRatio, 0.14f);
        mSecondaryTopOffset = a.getFloat(R.styleable.HtcActionBarDropDown_android_thicknessRatio, 1.04f);
        a.recycle();
        getDefaultHeight();
        setupArrowView();
        adjustPrimaryState();
        adjustSecondaryState();
    }

    /**
     * @param enable
     * @hide
     */
    public void setOneMultiLineTextView(boolean enable) {
        if (mEnableOneMultiline == enable) return;

        mEnableOneMultiline = enable;

        setSecondaryVisibility(mEnableOneMultiline ? View.GONE : View.VISIBLE);
        adjustPrimaryState();

    }

    private boolean isValidMode(int mode) {
        if (mode == HtcButtonUtil.BACKGROUND_MODE_THEME || mode == MODE_EXTERNAL || mode == MODE_ONE_MULTIILINE_TEXTVIEW) {
            return true;
        } else {
            if (ActionBarContainer.ENABLE_DEBUG) Log.d("ActionBarDropDown", "Invalid mode:" + mode);
            return false;
        }
    }

    private int getDefStyleRes() {
        switch (mSupportMode) {
            case MODE_EXTERNAL:
                return R.style.ActionBarDropDown;
            default:
                return R.style.ActionBarDropDown;
        }
    }

    /**
     * Get primary text.
     *
     * @return return primary text
     */
    public CharSequence getPrimaryText() {
        return mPrimaryView == null ? null : mPrimaryView.getText();
    }

    /**
     * Get secondary text.
     *
     * @return return secondary text
     */
    public CharSequence getSecondaryText() {
        return mSecondaryView == null ? null : mSecondaryView.getText();
    }

    /**
     * Set primary text.
     *
     * @param text the string set to primary text
     */
    public void setPrimaryText(String text) {
        if (mPrimaryView != null) {
            mPrimaryView.setText(text);
            setPrimaryVisibility(VISIBLE);
            requestLayout();
        }
    }

    /**
     * Set primary text.
     *
     * @param resource the string resource id set to primary text
     */
    public void setPrimaryText(int resource) {
        if (mPrimaryView != null) {
            mPrimaryView.setText(resource);
            setPrimaryVisibility(VISIBLE);
            requestLayout();
        }
    }

    /**
     * Set secondary text.
     *
     * @param text the string set to secondary text
     */
    public void setSecondaryText(String text) {
        if (mSecondaryView != null) {
            mSecondaryView.setText(text);
            setSecondaryVisibility(VISIBLE);
            requestLayout();
        }
    }

    /**
     * Set secondary text.
     *
     * @param resource the string resource id set to secondary text
     */
    public void setSecondaryText(int resource) {
        if (mSecondaryView != null) {
            mSecondaryView.setText(resource);
            setSecondaryVisibility(VISIBLE);
            requestLayout();
        }
    }

    /**
     * Get primary text visibility.
     *
     * @return visibility of primary text
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public int getPrimaryVisibility() {
        return mPrimaryView == null ? GONE : mPrimaryView.getVisibility();
    }

    /**
     * Get secondary text visibility.
     *
     * @return visibility of secondary text
     */
    public int getSecondaryVisibility() {
        return mSecondaryView == null ? GONE : mSecondaryView.getVisibility();
    }

    /**
     * Set primary text visibility.
     *
     * @param visibility Set the visibility state of this view.
     */
    public void setPrimaryVisibility(int visibility) {
        if (mPrimaryView != null && mPrimaryView.getVisibility() != visibility) {
            mPrimaryView.setVisibility(visibility);
        }
    }

    /**
     * Set secondary text visibility.
     *
     * @param visibility Set the visibility state of this view.
     */
    public void setSecondaryVisibility(int visibility) {
        if (mSecondaryView != null && mSecondaryView.getVisibility() != visibility) {
            mSecondaryView.setVisibility(visibility);
            adjustPrimaryState();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set arrow enable.
     *
     * @param enable Set the enabled state of arrow view.
     */
    public void setArrowEnabled(boolean enable) {
        if (mArrowView != null && mArrowView.getVisibility() != (enable ? VISIBLE : GONE)) mArrowView.setVisibility(enable ? VISIBLE : GONE);
    }

    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    @Override
    public void setLayerType(int layerType, android.graphics.Paint paint) {
        super.setLayerType(layerType, paint);
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_DEFAULT, to = "MODE_DEFAULT"),
            @IntToString(from = MODE_EXTERNAL, to = "MODE_EXTERNAL"),
            @IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = MODE_ONE_MULTIILINE_TEXTVIEW, to = "MODE_ONE_MULTIILINE_TEXTVIEW")
    })
    private int mSupportMode = HtcButtonUtil.BACKGROUND_MODE_THEME;

    private void adjustPrimaryState() {
        if (null == mPrimaryView || null == mSecondaryView) return;

        if (mEnableOneMultiline) {
            mPrimaryView.setEllipsize(android.text.TextUtils.TruncateAt.END);
            mPrimaryView.setSingleLine(false);
            mPrimaryView.setMaxLines(2);
            mPrimaryView.setHorizontalFadingEdgeEnabled(false);
            mPrimaryView.setTextAppearance(getContext(), mMultilinePrimaryTextAppearance);
        } else {
            if (View.VISIBLE == mSecondaryView.getVisibility()) {
                mPrimaryView.setTextAppearance(getContext(), mPrimaryTextAppearance);
            } else {
                mPrimaryView.setTextAppearance(getContext(), mSingleTextAppearance);
            }
            mPrimaryView.setEllipsize(null);
            mPrimaryView.setSingleLine(true);
            mPrimaryView.setMaxLines(1);
            mPrimaryView.setHorizontalFadingEdgeEnabled(true);
        }
    }

    private void adjustSecondaryState() {
        if (null == mSecondaryView) {
            return;
        }
        mSecondaryView.setTextAppearance(getContext(), mSecondaryTextAppearance);
    }

    /**
     * @hide
     **/
    public void setCounter(boolean enable) {
        if (mCounterView != null && mCounterView.getVisibility() != (enable ? VISIBLE : GONE)) {
            mCounterView.setVisibility(enable ? VISIBLE : GONE);
        }
    }

    // support special usage for automotive mode only
    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    public void setSupportMode(int mode) {
        // skip to avoid useless operation
        if (mSupportMode == mode || !isValidMode(mode)) return;

        setOneMultiLineTextView(mode == MODE_ONE_MULTIILINE_TEXTVIEW);
        if (mode == MODE_ONE_MULTIILINE_TEXTVIEW) {
            mode = HtcButtonUtil.BACKGROUND_MODE_THEME;
        }
        mSupportMode = mode;
        init(null, R.attr.actionBarDropDownStyle);
    }

    // special support for automotive usage
    private void setupArrowView() {
        // reset the margin value for arrow view
        if (mArrowView != null) {
            mArrowView.setImageDrawable(mArrowDrawable);
            MarginLayoutParams mlp = (MarginLayoutParams) mArrowView.getLayoutParams();
            if (null != mlp) {
                if (ActionBarUtil.IS_SUPPORT_RTL) {
                    mlp.setMarginStart(mArrowDrawableLeftMargin);
                } else {
                    mlp.leftMargin = mArrowDrawableLeftMargin;
                }
                mArrowView.setLayoutParams(mlp);
            }
        }
    }

    /**
     * Use to get primary textview.
     *
     * @return the TextView of primary text
     */
    public TextView getPrimaryView() {
        return mPrimaryView;
    }

    /**
     * Use to get secondary textview.
     *
     * @return the TextView of secondary text
     */
    public TextView getSecondaryView() {
        return mSecondaryView;
    }

    /**
     * Use to get counterView.
     *
     * @return the TextView of counterView
     * @hide
     */
    public TextView getCounterView() {
        return mCounterView;
    }


    private int getTopOffset(float base, float fraction) {
        if (fraction == 0) return 0;

        return (int) (base * fraction + 0.5f);
    }

    /**
     * (non-Javadoc).
     *
     * @see android.widget.RelativeLayout#onLayout(boolean, int, int, int, int)
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final boolean isLayoutRtl;
        final int currentStart;
        if (ActionBarUtil.IS_SUPPORT_RTL) {
            isLayoutRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
            currentStart = getPaddingStart();
        } else {
            isLayoutRtl = false;
            currentStart = getPaddingLeft();
        }
        final int currentTop = getPaddingTop();
        final int parentWidth = r - l;
        final int parentHeight = b - t - currentTop - getPaddingBottom();
        int topPrimary = 0, topSecondary = 0, primaryUsed = 0, secondaryUsed = 0;
        int childWidth, childHeight, childLeft, childTop = 0;
        MarginLayoutParams mlp;

        if (null != mPrimaryView && VISIBLE == mPrimaryView.getVisibility()) {
            childWidth = mPrimaryView.getMeasuredWidth();
            childHeight = mPrimaryView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mPrimaryView.getLayoutParams();
            childLeft = ActionBarUtil.getChildLeft(parentWidth, primaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);

            if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
                if (isDefaultFontEnabled()) {
                    childTop = getTopOffset(mNormalPrimaryHeight, mPrimaryTopOffset) + mNormalPrimaryBaseline - getPrimaryBaseline();
                } else {
                    childTop = getTopOffset(childHeight, mPrimaryTopOffset);
                }
                topPrimary = childTop += currentTop;
                childTop += mlp.topMargin;
            } else {
                childTop = (parentHeight - childHeight) / 2;
                topPrimary = childTop += currentTop;
                childTop += mlp.topMargin - mlp.bottomMargin;
            }
            ActionBarUtil.setChildFrame(mPrimaryView, childLeft, childTop, childWidth, childHeight);
            primaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
        }

        if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility() && null != mPrimaryView) {
            childWidth = mSecondaryView.getMeasuredWidth();
            childHeight = mSecondaryView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mSecondaryView.getLayoutParams();

            if (isDefaultFontEnabled()) {
                childTop = getTopOffset(mNormalPrimaryHeight, mSecondaryTopOffset) + mNormalSecondaryBaseline - getSecondrayBaseline();
            } else {
                childTop = getTopOffset(mPrimaryView.getMeasuredHeight(), mSecondaryTopOffset);
            }

            topSecondary = childTop += currentTop;
            childTop += mlp.topMargin;

            childLeft = ActionBarUtil.getChildLeft(parentWidth, secondaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);

            ActionBarUtil.setChildFrame(mSecondaryView, childLeft, childTop, childWidth, childHeight);
            secondaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
        }

        if (null != mCounterView && VISIBLE == mCounterView.getVisibility()) {
            childWidth = mCounterView.getMeasuredWidth();
            childHeight = mCounterView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mCounterView.getLayoutParams();

            if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
                childTop = topSecondary + getSecondrayBaseline() - mCounterView.getBaseline();
                childTop += mlp.topMargin;
                childLeft = ActionBarUtil.getChildLeft(parentWidth, secondaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);
            } else {
                if (mEnableOneMultiline) {
                    childTop = (parentHeight - childHeight) / 2 + currentTop + mlp.topMargin - mlp.bottomMargin;
                } else {
                    childTop = topPrimary + getPrimaryBaseline() - mCounterView.getBaseline();
                    childTop += mlp.topMargin;
                }

                childLeft = ActionBarUtil.getChildLeft(parentWidth, primaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);
                primaryUsed += ActionBarUtil.getChildUsedWidth(childWidth, mlp);
            }
            ActionBarUtil.setChildFrame(mCounterView, childLeft, childTop, childWidth, childHeight);
        }

        if (null != mArrowView && VISIBLE == mArrowView.getVisibility()) {
            childWidth = mArrowView.getMeasuredWidth();
            childHeight = mArrowView.getMeasuredHeight();
            mlp = (MarginLayoutParams) mArrowView.getLayoutParams();

            boolean showArrow = true;
            if (mEnableOneMultiline) {
                childTop = (parentHeight - childHeight) / 2 + currentTop + mlp.topMargin - mlp.bottomMargin;
                if (null != mPrimaryView) {
                    Layout tl = mPrimaryView.getLayout();
                    showArrow = (null != tl && tl.getLineCount() > 0 && tl.getEllipsisCount(tl.getLineCount() - 1) > 0);
                }
            } else {
                childTop = topPrimary + getPrimaryBaseline() - ((HtcResUtil.getHeightOfChar(mPrimaryView, "e") + childHeight) / 2);
                childTop += mlp.topMargin;
            }
            childLeft = ActionBarUtil.getChildLeft(parentWidth, primaryUsed, ActionBarUtil.getStartMarginForPlatform(mlp) + currentStart, childWidth, isLayoutRtl);
            ActionBarUtil.setChildFrame(mArrowView, childLeft, childTop, showArrow ? childWidth : 0, showArrow ? childHeight : 0);
        }
    }

    /**
     * (non-Javadoc).
     *
     * @see android.widget.RelativeLayout#onMeasure(int, int)
     * @hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        MarginLayoutParams mlp;
        int primaryUsedWidth = 0;
        int secondaryUsedWidth = 0;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);

        if (ActionBarUtil.IS_SUPPORT_RTL) {
            width = width - getPaddingStart() - getPaddingEnd();
        } else {
            width = width - getPaddingLeft() - getPaddingRight();
        }

        int countUsedWidth = 0;
        if (null != mCounterView && VISIBLE == mCounterView.getVisibility()) {
            mCounterView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            mlp = (MarginLayoutParams) mCounterView.getLayoutParams();
            countUsedWidth = ActionBarUtil.getChildUsedWidth(mCounterView.getMeasuredWidth(), mlp);
        }
        if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
            secondaryUsedWidth += countUsedWidth;
        } else {
            primaryUsedWidth += countUsedWidth;
        }

        int arrowUsedWidth = 0;
        if (null != mArrowView && VISIBLE == mArrowView.getVisibility()) {
            mArrowView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            mlp = (MarginLayoutParams) mArrowView.getLayoutParams();

            arrowUsedWidth = ActionBarUtil.getChildUsedWidth(mArrowView.getMeasuredWidth(), mlp);
            primaryUsedWidth += arrowUsedWidth;
        }

        if (null != mPrimaryView && VISIBLE == mPrimaryView.getVisibility()) {
            mlp = (MarginLayoutParams) mPrimaryView.getLayoutParams();
            int margin = ActionBarUtil.getChildUsedWidth(0, mlp);
            int usedWidth = (mEnableOneMultiline ? countUsedWidth : primaryUsedWidth);

            if (width - usedWidth - margin < 0) {
                LogUtil.logE("ActionBarDropDown", "primaryWidth < 0 : ",
                        "width = ", width,
                        ",usedWidth = ", usedWidth,
                        ",margin = ", margin);
            }
            ActionBarUtil.measureActionBarTextView(mPrimaryView, width, usedWidth + margin, !mEnableOneMultiline);

            if (mEnableOneMultiline) {
                Layout l = mPrimaryView.getLayout();
                if (null != l && l.getLineCount() > 0 && l.getEllipsisCount(l.getLineCount() - 1) > 0) {
                    int primaryWidth = width - primaryUsedWidth - margin;
                    if (primaryWidth < 0) {
                        primaryWidth = 0;
                        LogUtil.logE("ActionBarDropDown", "multiLinePrimaryWidth < 0 : ",
                                "width = ", width,
                                ",primaryUsedWidth = ", primaryUsedWidth,
                                ",margin = ", margin);
                    }
                    int primaryWidthMeasureSpec = MeasureSpec.makeMeasureSpec(primaryWidth, MeasureSpec.EXACTLY);
                    int primaryHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

                    mPrimaryView.measure(primaryWidthMeasureSpec, primaryHeightMeasureSpec);
                } else {
                    mArrowView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
                }
            }
            getPrimaryBaseline();
        }

        if (null != mSecondaryView && VISIBLE == mSecondaryView.getVisibility()) {
            mlp = (MarginLayoutParams) mSecondaryView.getLayoutParams();
            int margin = ActionBarUtil.getChildUsedWidth(0, mlp);

            if (ActionBarContainer.ENABLE_DEBUG) {
                if (width - secondaryUsedWidth - margin <= 0) {
                    LogUtil.logE("ActionBarDropDown", "secondaryWidth <= 0 : ",
                            "width = ", width,
                            ",secondaryUsedWidth = ", secondaryUsedWidth,
                            ",margin = ", margin);
                }
            }
            ActionBarUtil.measureActionBarTextView(mSecondaryView, width, secondaryUsedWidth + margin, true);
            getSecondrayBaseline();
        }
    }

    /**
     * (non-Javadoc).
     *
     * @see android.view.ViewGroup#generateDefaultLayoutParams()
     * @hide
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * (non-Javadoc).
     *
     * @see android.view.ViewGroup#generateLayoutParams(android.util.AttributeSet)
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * (non-Javadoc).
     *
     * @see android.view.ViewGroup#generateLayoutParams(android.view.ViewGroup. LayoutParams)
     */
    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    /**
     * (non-Javadoc).
     *
     * @see android.view.ViewGroup#checkLayoutParams(android.view.ViewGroup.LayoutParams )
     * @hide
     */
    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    @ExportedProperty(category = "CommonControl")
    private int mNormalPrimaryHeight;
    @ExportedProperty(category = "CommonControl")
    private int mNormalPrimaryBaseline;
    @ExportedProperty(category = "CommonControl")
    private int mNormalSecondaryBaseline;

    private void getDefaultHeight() {
        FontMetricsInt primaryFontMetricsInt;
        FontMetricsInt secondaryFontMetricsInt;
        primaryFontMetricsInt = HtcResUtil.getFontStyleMetrics(getContext(), mPrimaryTextAppearance);
        secondaryFontMetricsInt = HtcResUtil.getFontStyleMetrics(getContext(), mSecondaryTextAppearance);
        if (primaryFontMetricsInt != null) {
            mNormalPrimaryBaseline = 0 - primaryFontMetricsInt.top;
            mNormalPrimaryHeight = primaryFontMetricsInt.bottom - primaryFontMetricsInt.top;
        }
        if (secondaryFontMetricsInt != null) {
            mNormalSecondaryBaseline = 0 - secondaryFontMetricsInt.top;
        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean isDefaultFontEnabled() {
        return mNormalPrimaryBaseline > 0 && mNormalPrimaryHeight > 0 && mNormalSecondaryBaseline > 0;
    }

    @ExportedProperty(category = "CommonControl")
    private int mPrimaryBaseline = -1;
    @ExportedProperty(category = "CommonControl")
    private int mSecondaryBaseline = -1;

    private int getPrimaryBaseline() {
        if (mPrimaryView != null) {
            int baseline = mPrimaryView.getBaseline();
            if (baseline != -1 && baseline != mPrimaryBaseline) {
                mPrimaryBaseline = baseline;
            }
        }
        return mPrimaryBaseline;
    }

    private int getSecondrayBaseline() {
        if (mSecondaryView != null) {
            int baseline = mSecondaryView.getBaseline();
            if (baseline != -1 && baseline != mSecondaryBaseline) {
                mSecondaryBaseline = baseline;
            }
        }
        return mSecondaryBaseline;
    }
}
