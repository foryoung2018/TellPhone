package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcDebugFlag;

/**
 * The HTC style QuickContactBadge used with HtcListItem.
 * <ul>
 * <li>Image Only
 *
 * <pre class="prettyprint">
 *      &lt;com.htc.widget.HtcListItemQuickContactBadge
 *       android:id="@+id/photo"/&gt;
 * </pre>
 *
 * </ul>
 */
public class HtcListItemQuickContactBadge extends HtcListItemImageComponent implements
        IHtcListItemComponentNoLeftTopMargin {
    private QuickContactBadge mBadge;
    private final static String LOG_TAG = "HtcListItemQuickContactBadge";
    private int mDiameterSize;
    private void init(Context context) {
        mBadge = new QuickContactBadge(context);
        super.setPadding(0, 0, 0, 0);
        setupBadgeLayoutParams();
        addView(mBadge, 0);

        if (HtcDebugFlag.getHtcDebugFlag()) {
            Log.d("LOG_TAG", "current mode is " + mItemMode);
        }
    }

    /**
     * Simple constructor to use when creating a HtcListItemQuickContactBadge
     * from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemQuickContactBadge(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemQuickContactBadge.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemQuickContactBadge(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemQuickContactBadge.
     * @param defStyle The default style to apply to this
     *            HtcListItemQuickContactBadge. If 0, no style will be applied
     *            (beyond what is included in the theme). This may either be an
     *            attribute resource, whose value will be retrieved from the
     *            current theme, or an explicit style resource.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemQuickContactBadge(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.width = mComponentWidth;
        params.height = mComponentHeight;
        super.setLayoutParams(params);
    }

    /**
     * Get the LayoutParams associated with this HtcListItemQuickContactBadge.
     *
     * @return The LayoutParams associated with this view, or null if no
     *         parameters have been set yet
     */
    public ViewGroup.LayoutParams getLayoutParams() {
        if (super.getLayoutParams() != null)
            return super.getLayoutParams();
        else {
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    mComponentWidth,
                    mComponentHeight);
            super.setLayoutParams(params);
            return params;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(mBadge, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mComponentWidth, mComponentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(isRound){
        mBadge.layout((mComponentWidth - mDiameterSize) / 2, (mComponentHeight - mDiameterSize) / 2,
                (mComponentWidth + mDiameterSize) / 2, (mComponentHeight + mDiameterSize) / 2);
        } else {
            mBadge.layout(0, (mComponentHeight - mDiameterSize) / 2,
                    mDiameterSize, (mComponentHeight + mDiameterSize) / 2);
        }
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setPadding(int left, int top, int right, int bottom) {
    }

    /**
     * @return QuickContactBadge
     */
    public QuickContactBadge getBadge() {
        return mBadge;
    }


    private void setupBadgeLayoutParams() {
        mDiameterSize = isRound ? getContext().getResources().getDimensionPixelOffset(R.dimen.htc_list_item_photo_frame_diameter) : mComponentWidth;
        mBadge.setLayoutParams(new LayoutParams(mDiameterSize, mDiameterSize, Gravity.CENTER));
    }

    /**
     * Set whether this badge is round size spec,the default is true.
     *
     * @param round True set badge to round size spec defined in Sense8 UIGL, false to square size spec defined in Sense7 UIGL.
     */
    public void enableRoundSize(boolean round){
        if (round == isRound) return;

        isRound = round;
        mComponentWidth = mComponentHeight + getExtraWidth();
        setupBadgeLayoutParams();
        requestLayout();
    }

    /** @hide */
    @Override
    public int getRightMargin() {
        return isRound ? 0 : M2;
    }

    /** @hide */
    @Override
    protected int getExtraWidth() {
        return isRound ? M2 : 0;
    }
}
