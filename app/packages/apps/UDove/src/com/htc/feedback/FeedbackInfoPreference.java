package com.htc.feedback;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

//import com.htc.lib1.cc.widget.preference.HtcPreference;

public class FeedbackInfoPreference extends Preference {

    public FeedbackInfoPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public FeedbackInfoPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    protected View onCreateView(ViewGroup parent) {
        View target = super.onCreateView(parent);
        return target;
    }
}
