package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.htc.lib1.cc.R;

/**
 * HtcFlagButton
 */
public class HtcFlagButton extends HtcCompoundButton {
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     */
    public HtcFlagButton(Context context) {
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

     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcFlagButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.htcFlagButtonStyle);
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
    public HtcFlagButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mIsContentMultiplyRequired = false;
        mHasOnState = true;
        mSkipFirstUpDraw = true; //Add by Ahan due to disable animation
        setButtonDrawables(context, attrs, defStyleAttr);
    }

    @Override
    protected int getDefStyleRes() {
        return R.style.HtcCompoundButton_HtcFlagButton;
    }

}