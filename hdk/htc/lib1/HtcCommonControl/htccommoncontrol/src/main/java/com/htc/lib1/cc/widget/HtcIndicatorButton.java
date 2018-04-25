
package com.htc.lib1.cc.widget;

/**
 *  This control is used with HtcExpandableListView.
 *  For each group item in HtcExpandableListView, the HtcIndicatorButton
 *  is required to be added into it.
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.IHtcListItemControl;

public class HtcIndicatorButton extends View implements IHtcListItemControl
{
    /**
         * The default mode of indicator button
         */
    public final static int DEFAULT_MODE = 0;
    /**
         * The dark mode of indicator button
         */
    public final static int DRAK_MODE = 1;
    // Specify the current mode of indicator button
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = DEFAULT_MODE, to = "DEFAULT_MODE"),
            @IntToString(from = DRAK_MODE, to = "DRAK_MODE")
    })
    private int mMode = HtcButtonUtil.BACKGROUND_MODE_THEME;

    /**
     * The default style assets
     */
    private Drawable mCommonExpandOn=null;
    private Drawable mCommonCollapseOn=null;

    private int mOverlayColor;

    @ExportedProperty(category = "CommonControl")
    private int mWidth = 0;

    @ExportedProperty(category = "CommonControl")
    private int mHeight = 0;

    // Used to perform color multiply
    private PorterDuffColorFilter mPorterDuffColorFilter;

    /**
     * Simple constructor to use when creating a HtcIndicatorButton from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcIndicatorButton(Context context)
    {
        this(context,null);
    }

    /**
     * Simple constructor to use when creating a HtcIndicatorButton from code with the background
     * mode. The backgroundMode is either {@link HtcIndicatorButton#DEFAULT_MODE} or
     * {@link HtcIndicatorButton#DRAK_MODE}.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param backgroundMode The background mode that the value is either DEFAULT_MODE or DRAK_MODE
     */
    public HtcIndicatorButton(Context context, int backgroundMode) {
        super(context);
        if (isValidMode(backgroundMode)) {
            mMode = backgroundMode;
        }
        init(null, R.attr.htcIndicatorButtonStyle);
    }

    /**
     * Constructor that is called when inflating a HtcIndicatorButton from XML.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrset The attributes of the XML tag that is inflating the view.
     */
    public HtcIndicatorButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.htcIndicatorButtonStyle);
    }

    /**
     * Constructor that is called when inflating a HtcIndicatorButton from XML.
     *
     * @param context The Context the view is running in, through which it can access the current
     *                theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrset The attributes of the XML tag that is inflating the view.
     * @hide
     */
    public HtcIndicatorButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mOverlayColor = HtcButtonUtil.getOverlayColor(getContext(), null);
        mPorterDuffColorFilter = new PorterDuffColorFilter(mOverlayColor, PorterDuff.Mode.SRC_ATOP);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HtcIndicatorButton);
            if (a.hasValue(R.styleable.HtcIndicatorButton_indicatorMode)) {
                mMode = a.getInt(R.styleable.HtcIndicatorButton_indicatorMode, -1);
            }
            a.recycle();
        }

        final int defStyleAttr = mMode == HtcButtonUtil.BACKGROUND_MODE_THEME ? defStyle : 0;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HtcIndicatorButton, defStyleAttr, getDefStyleRes());
        mCommonCollapseOn = a.getDrawable(R.styleable.HtcIndicatorButton_android_drawable);
        mCommonExpandOn = a.getDrawable(R.styleable.HtcIndicatorButton_android_drawableBottom);
        a.recycle();
        setupMeasurement(mCommonExpandOn);
    }

    private int getDefStyleRes() {
        if (mMode == DRAK_MODE) {
            return R.style.HtcIndicatorButton;
        } else {
            return R.style.HtcIndicatorButton_Light;
        }
    }

    int getMode(){
        return mMode;
    }

    void setMode(int mode){
        if (mMode == mode || !isValidMode(mode)) return;
        mMode = mode;
        init(null, R.attr.htcIndicatorButtonStyle);
    }

    private boolean isValidMode(int mode) {
        if (mode == HtcButtonUtil.BACKGROUND_MODE_THEME || mode == DRAK_MODE || mode == DEFAULT_MODE){
            return true;
        }else{
            return false;
        }
    }

        private void setupMeasurement(Drawable d){
        mWidth = d.getIntrinsicWidth();
        mHeight = d.getIntrinsicHeight();
    }

    int getIndicatorWidth() {
        return mWidth;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(mWidth, mHeight);
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onLayout(boolean changed,int left,int top,int right,int bottom)
    {
        super.onLayout(changed,left,top,right,bottom);

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        //setup drawable bound for drawing
        setDrawableBounds(viewWidth,viewHeight);
    }

    private void setDrawableBounds(int right,int bottom){
            mCommonExpandOn.setBounds(0,0,right,bottom);
            mCommonCollapseOn.setBounds(0,0,right,bottom);
    }

    @ExportedProperty(category = "CommonControl")
    boolean mIsPressed = false;
    @ExportedProperty(category = "CommonControl")
    boolean mIsExpanded = false;
    @ExportedProperty(category = "CommonControl")
    boolean mIsRestOn = false;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if(mIsPressed == pressed) return;
        mIsPressed = pressed;
            if(!mIsExpanded) {
                refreshIndicatorState(mCommonExpandOn);
            }
            else {
                refreshIndicatorState(mCommonCollapseOn);
            }

        this.invalidate();
    }

        /**
         * Set whether the indicator is expanded.
         * This will change the arrow direction (up or down).
         * @param expanded Whether the indicator is expanded.
         */
    public void setExpanded(boolean expanded){
        if(expanded == mIsExpanded) return;
        mIsExpanded = expanded;
        invalidate();
    }
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean isExpanded(){
        return mIsExpanded;
    }

    void refreshIndicatorState(Drawable drawable){
        if(mIsPressed) {
            drawable.setColorFilter(mPorterDuffColorFilter);
        } else {
            drawable.setColorFilter(null);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
            if(!mIsExpanded) {
                mCommonExpandOn.draw(canvas);
            }
            else {
                mCommonCollapseOn.draw(canvas);
            }
        }
}
