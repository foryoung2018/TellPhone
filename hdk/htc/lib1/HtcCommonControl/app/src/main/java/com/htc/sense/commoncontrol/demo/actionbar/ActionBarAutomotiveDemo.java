
package com.htc.sense.commoncontrol.demo.actionbar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.htc.lib1.cc.widget.HtcAutoCompleteTextView;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ActionBarSearch;
import com.htc.lib1.cc.widget.ActionBarText;

public class ActionBarAutomotiveDemo extends CommonDemoActivityBase {

    private LinearLayout mBackground = null;
    private RadioGroup mCenterViewRadio = null;
    private ActionBarContainer mActionBarContainer = null;
    private ActionBarDropDown mActionBarDropDown = null;
    private ActionBarItemView mActionBarItemViewRight = null;
    private ActionBarSearch mActionBarSearch = null;
    private ActionBarText mActionBarText = null;
    private CheckBox mProgressCheckBox = null;
    private CheckBox mRightCheckBox = null;
    private CheckBox mCounterCheckBox = null;
    private boolean mEnableCounter = false;

    private void initActionBarModule() {
        ActionBarDemoUtil.clearInstance();
        ActionBarDemoUtil abdu = ActionBarDemoUtil.getInstance(this, true);

        mActionBarContainer = abdu.initActionBarContainer();
        mBackground.addView(mActionBarContainer, 0);

        mActionBarText = abdu.initActionBarText();
        mActionBarText.setSecondaryVisibility(View.GONE);

        mActionBarSearch = abdu.initActionBarSearch();
        mHtcAutoCompleteTextView = (HtcAutoCompleteTextView) mActionBarSearch.getAutoCompleteTextView();
        mHtcAutoCompleteTextView.setOnSelectionChangedListener(mSelectionChangedListener);

        mActionBarDropDown = abdu.initActionBarDropDown();
        mActionBarDropDown.setSecondaryVisibility(View.GONE);

        mActionBarItemViewRight = abdu.initActionBarItemViewRight();
    }


    private HtcAutoCompleteTextView mHtcAutoCompleteTextView;
    private HtcAutoCompleteTextView.OnSelectionChangedListener mSelectionChangedListener = new HtcAutoCompleteTextView.OnSelectionChangedListener() {
        private int mSelStart, mSelEnd;
        private ForegroundColorSpan mSpan = new ForegroundColorSpan(Color.RED);

        @Override
        public void onSelectionChanged(int selStart, int selEnd) {
            Editable s = mHtcAutoCompleteTextView.getText();
            if (s == null) return;

            if (mSelStart == selStart && mSelEnd == selEnd) return;

            mSelStart = selStart;
            mSelEnd = selEnd;
            s.removeSpan(mSpan);
            if (selStart == selEnd) return;

            s.setSpan(mSpan, mSelStart, mSelEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mHtcAutoCompleteTextView.postInvalidate();
        }

        @Override
        public void onTextContextMenuItem(int id) {
            if (id == android.R.id.cut || id == android.R.id.copy) {
                Editable s = mHtcAutoCompleteTextView.getText();
                if (s == null) return;
                s.removeSpan(mSpan);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionbar_automotive_main);

        mBackground = (LinearLayout) findViewById(R.id.background);

        initActionBarModule();

        CheckBox cb = (CheckBox) findViewById(R.id.secondary_show_hide);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mActionBarText.setSecondaryVisibility((isChecked) ? View.VISIBLE : View.GONE);
                mActionBarDropDown.setSecondaryVisibility((isChecked) ? View.VISIBLE : View.GONE);
            }
        });

        mCenterViewRadio = (RadioGroup) findViewById(R.id.radioGroup1);
        mRightCheckBox = (CheckBox) findViewById(R.id.checkBox1);
        mProgressCheckBox = (CheckBox) findViewById(R.id.checkBox2);
        mCounterCheckBox = (CheckBox) findViewById(R.id.checkBox3);

        mCenterViewRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                switch (checkedId) {
                    case R.id.radioButton1:
                        ActionBarDemoUtil.removeActionBarContainerCenterChild(mActionBarContainer);

                        mActionBarContainer.addCenterView(mActionBarText);
                        break;

                    case R.id.radioButton3:
                        ActionBarDemoUtil.removeActionBarContainerCenterChild(mActionBarContainer);

                        mActionBarContainer.addCenterView(mActionBarDropDown);
                        break;

                    case R.id.radioButton5:
                        ActionBarDemoUtil.removeActionBarContainerCenterChild(mActionBarContainer);

                        mActionBarContainer.addCenterView(mActionBarSearch);
                        break;

                }
            }
        });

        mRightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mActionBarContainer.addEndView(mActionBarItemViewRight);
                } else {
                    mActionBarContainer.removeView(mActionBarItemViewRight);
                }
            }
        });

        mProgressCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mActionBarContainer.setProgressVisibility(View.VISIBLE);
                } else {
                    mActionBarContainer.setProgressVisibility(View.GONE);
                }
            }
        });

        mCounterCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mEnableCounter = true;
                } else {
                    mEnableCounter = false;
                }
            }
        });
    }

    @Override
    protected void applyCustomWindowFeature() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.CarTheme);
    }

}
