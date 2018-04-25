package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.widget.FrameLayout;

import com.htc.lib1.cc.htcjavaflag.HtcDebugFlag;
import com.htc.lib1.cc.util.CheckUtil;

class HtcListItemImageComponent extends FrameLayout implements IHtcListItemComponent,
        IHtcListItemAutoMotiveControl {
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    protected int mComponentWidth = 0;
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @ExportedProperty(category = "CommonControl")
    protected int mComponentHeight = 0;

    @ExportedProperty(category = "CommonControl")
    boolean mIsAutomotiveMode = false;

    /** @hide */
    protected int M2;
    /** @hide */
    protected boolean isRound = true;

    private final static String LOG_TAG = "HtcListItemImageComponent";

    private void initComponentSize() {
        M2 = HtcListItemManager.getM2(getContext());
        mComponentHeight = HtcListItemManager.getInstance(getContext()).getDesiredListItemHeight(
                mItemMode);
        mComponentWidth = mComponentHeight + getExtraWidth();
    }

    private void init(Context context, AttributeSet attrs) {
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        final int[] itemModes = HtcListItemManager.resolveListItemMode(getContext(), attrs);
        mItemMode = itemModes[0];
        mIsAutomotiveMode = mItemMode == HtcListItem.MODE_AUTOMOTIVE;
        initComponentSize();
    }

    /**
     * Simple constructor to use when creating a HtcListItemImageComponent from
     * code.
     *
     * @param context The Context the HtcListItemImageComponent is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemImageComponent(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the HtcListItemImageComponent is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemImageComponent.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemImageComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the HtcListItemImageComponent is running in,
     *            through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemImageComponent.
     * @param defStyle The default style to apply to this
     *            HtcListItemImageComponent. If 0, no style will be applied
     *            (beyond what is included in the theme). This may either be an
     *            attribute resource, whose value will be retrieved from the
     *            current theme, or an explicit style resource.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemImageComponent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * this onLayout will only measure the bubble or indicator, then call
     * setMeasuredDimension(0, 0).
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onMeasure(int w, int h) {
        setMeasuredDimension(0, 0);
    }

    /**
     * this onLayout will only layout the bubble or indicator.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    /**
     * if false, views will be disabled and alpha value will be set to 0.4
     *
     * @param enabled true if enabled, false otherwise.
     */
    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            super.setEnabled(enabled);
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.setEnabled(enabled);
                HtcListItemManager.setViewOpacity(child, enabled);
            }
        }
    }

    /**
     * {@hide}
     *
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setAutoMotiveMode(boolean enable) {
    }

    int mItemMode = HtcListItem.MODE_DEFAULT;

    /**
     * To notify this component the item mode.
     *
     * @param itemMode The item mode to notify this component.
     */
    public void notifyItemMode(int itemMode) {
        if (HtcDebugFlag.getHtcDebugFlag() && (mItemMode != itemMode)) {
            Log.e(LOG_TAG, this.getClass().getSimpleName() + " notifyItemMode,current mode is " + itemMode);
        }
        mItemMode = itemMode;
        mIsAutomotiveMode = mItemMode == HtcListItem.MODE_AUTOMOTIVE;
        initComponentSize();
    }


    /**
     * @hide
     */
    protected int getExtraWidth() {
        return M2;
    }

}
