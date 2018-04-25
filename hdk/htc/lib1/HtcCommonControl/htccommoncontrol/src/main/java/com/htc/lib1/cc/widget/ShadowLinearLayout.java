package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.LinearLayout;

import com.htc.lib1.cc.R;

/**
 * A LinearLayout that draws 2 shadows on the borders of the content part.
 *
 * This is a replacement for AlertDialog's parentPanel,
 * which draws top and bottom shadows depending on the visibilities of the
 * header and footer.
 *
 * Caution! Do not use this class directly!
 *
 * @author henrycy_lee@htc.com
 * @hide
 */
public class ShadowLinearLayout extends LinearLayout {

    public static final int NORMAL = -4; // default layout. fixed width, but different in landscape and portrait
    public static final int KEEP_PORTRAIT_WIDTH = -8; // keep portrait width in landscape
//    public static final int SQUARE = -12; // width and height are the same as portrait width in any orientation
//    public static final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT; // wrap width/height, for Progress Dialog

    private final Rect mRect = new Rect();
    private /*final*/ DisplayMetrics mDM;
    @ExportedProperty(category = "CommonControl")
    private /*final*/ int mMarginPortrait;
    @ExportedProperty(category = "CommonControl")
    private /*final*/ int mMarginLandscape;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = KEEP_PORTRAIT_WIDTH, to = "KEEP_PORTRAIT_WIDTH"),
            @IntToString(from = NORMAL, to = "NORMAL")
    })
    private int mLayoutArg = NORMAL;

    public ShadowLinearLayout(Context context) {
        super(context);
        init();
    }

    public ShadowLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShadowLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Resources res = getContext().getResources();
        mDM  = res.getDisplayMetrics();
        mMarginPortrait = res.getDimensionPixelOffset(R.dimen.margin_m) * 2;
        mMarginLandscape = (int) (0.105f * Math.min(mDM.heightPixels, mDM.widthPixels) * 2);
    }

    /**
     * sense 7 refactor to merge 2 old variables into 1.
     * 3 modes are supported:
     * 1. normal: fixed width, but different in landscape and portrait. used for normal dialog
     * 2. LayoutParams.WRAP_CONTENT: wrap content x wrap content. used for progress dialog
     * 3. keep_portrait: fixed width, keep portrait width in landscape. used for Camera
     * @hide
     */
    public void setLayoutArg(int arg) {
        mLayoutArg = arg;
    }

    /**
     * @hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // adjust width
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (MeasureSpec.EXACTLY != widthMode) { // respect EXACTLY mode
            int widthSize;
            if (mDM.widthPixels < mDM.heightPixels) {
                // portrait, leave M2 on left and right
                widthSize = mDM.widthPixels - mMarginPortrait;
            } else {
                // landscape, leave 10.5% on left and right
                if (KEEP_PORTRAIT_WIDTH == mLayoutArg) {
                    widthSize = mDM.heightPixels - mMarginPortrait;
                } else {
                    widthSize = mDM.widthPixels - mMarginLandscape;
                }
            }
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, LayoutParams.WRAP_CONTENT != mLayoutArg ? MeasureSpec.EXACTLY : widthMode);
        }

        // adjust height
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.EXACTLY != heightMode) { // respect EXACTLY mode
            getWindowVisibleDisplayFrame(mRect);
            int heightSize = (0 == mRect.bottom - mRect.top) ? mDM.heightPixels : (mRect.bottom - mRect.top);
            if (mDM.widthPixels < mDM.heightPixels) {
                // portrait, margin=10.5%
                heightSize -= mMarginLandscape;
            } else {
                // landscape, margin=M2
                heightSize -= mMarginPortrait;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, LayoutParams.WRAP_CONTENT != mLayoutArg ? MeasureSpec.AT_MOST : heightMode);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
