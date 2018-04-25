
package com.htc.sense.commoncontrol.demo.htclistitem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.QuickContactBadge;

import com.htc.lib1.cc.support.widget.QuickContactBadgeCompat;
import com.htc.lib1.cc.util.BitmapUtil;
import com.htc.lib1.cc.util.ShapeUtil;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItem2LineStamp;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItem7Badges1LineBottomStamp;
import com.htc.lib1.cc.widget.HtcListItem7Badges1LineBottomStamp.OnFlagButtonCheckedChangeListener;
import com.htc.lib1.cc.widget.HtcEditText;
import com.htc.lib1.cc.widget.HtcListItemBubbleCount;
import com.htc.lib1.cc.widget.HtcListItemColorIcon;
import com.htc.lib1.cc.widget.HtcListItemQuickContactBadge;
import com.htc.lib1.cc.widget.HtcListItemReversed2LineText;
import com.htc.lib1.cc.widget.HtcListItemSerialNumber;
import com.htc.lib1.cc.widget.HtcListItemSingleText;
import com.htc.lib1.cc.widget.HtcListItemStockBoard;
import com.htc.lib1.cc.widget.HtcListItemTileImage;
import com.htc.lib1.cc.widget.ListItem;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class HtcListItemActivity1 extends HtcListActivity {
    LayoutInflater mInflater = null;
    private static final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setListAdapter(new MyListAdapter(this));

        if (mEnableAutomotive) {
            getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg_dark);
        }
        if (mShowKeep) {
            mShowListItem = false;
        }

        getListView().setCacheColorHint(Color.TRANSPARENT);;

        getListView().setOnItemClickListener(mOnClickListener);
    }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // TODO Auto-generated method stub

            HtcCheckBox checkBox = (HtcCheckBox) v.findViewById(R.id.checkBut);

            if (checkBox != null) {
                checkBox.setChecked(!checkBox.isChecked());
                checkBox.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
        }

    };

    @Override
    protected void initMenu() {
        mAddMenuEnableAutomotive = true;
        mAddMenuShowListItem = true;
        mAddMenuShowKeep = true;
    }

    private class MyListAdapter extends BaseAdapter {
        Context mContext = null;

        final int layoutsHtcListItem[] = new int[] {
                R.layout.list_item01, R.layout.list_item02, R.layout.list_item03,
                R.layout.list_item04, R.layout.list_item05, R.layout.list_item06,
                R.layout.list_item07, R.layout.list_item08, R.layout.list_item09,
                R.layout.list_item10, R.layout.list_item11, R.layout.list_item36,
                R.layout.list_item12, R.layout.list_item13, R.layout.list_item35,
                R.layout.list_item14,
                R.layout.list_item15,
                R.layout.list_item16,
                R.layout.list_item17,
                R.layout.list_item18,
                // no item19
                R.layout.list_item20, R.layout.list_item21, R.layout.listitem_001,
                R.layout.listitem_001, R.layout.listitem_002, R.layout.listitem_002,
                R.layout.list_item31, R.layout.list_item32, R.layout.list_item33,
                R.layout.list_item18, R.layout.list_item27, R.layout.list_item27,
                R.layout.list_item27, R.layout.list_item12,
        };

        final int layoutsListItem[] = new int[] {
                R.layout.list_item01_new, R.layout.list_item02_new, R.layout.list_item03_new,
                R.layout.list_item04_new, R.layout.list_item05_new, R.layout.list_item06_new,
                R.layout.list_item07_new, R.layout.list_item08_new, R.layout.list_item09_new,
                R.layout.list_item10_new, R.layout.list_item11_new, R.layout.list_item36_new,
                R.layout.list_item12_new, R.layout.list_item13_new, R.layout.list_item35_new,
                R.layout.list_item14_new,
                R.layout.list_item15_new,
                R.layout.list_item16_new,
                R.layout.list_item17_new,
                R.layout.list_item18_new,
                // no item19
                R.layout.list_item20_new, R.layout.list_item21_new, R.layout.listitem_001_new,
                R.layout.listitem_001_new, R.layout.listitem_002_new, R.layout.listitem_002_new,
                R.layout.list_item31_new, R.layout.list_item32_new, R.layout.list_item33_new,
                R.layout.list_item18_new, R.layout.list_item27_new, R.layout.list_item27_new,
                R.layout.list_item27_new, R.layout.list_item12_new,
        };

        final int LIST_ITEM_COUNT = layoutsHtcListItem.length;

        public MyListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return LIST_ITEM_COUNT;
        }

        public Object getItem(int position) {
            return mShowListItem ? layoutsListItem[position] : layoutsHtcListItem[position];
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return LIST_ITEM_COUNT;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup i = (ViewGroup) mInflater.inflate(mShowListItem ? layoutsListItem[position] : layoutsHtcListItem[position], null);
            if (mShowListItem) {
                ((ListItem) i).setAutoMotiveMode(mEnableAutomotive);
            } else {
                ((HtcListItem) i).setAutoMotiveMode(mEnableAutomotive);
            }

            if (position == 0) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_0_text2);
                text.setSecondaryTextVisibility(View.GONE);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 1) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_1_text2);
                text.setSecondaryTextVisibility(View.GONE);

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText(R.string.list_item_demo_fling_1_text2_aaa);
                stamp.setSecondaryTextVisibility(View.GONE);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(stamp, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 2) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_2);
                text.setSecondaryText(R.string.list_item_demo_fling_2_text2);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 3) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_3);
                text.setSecondaryText(R.string.list_item_demo_fling_3_text2);

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText(R.string.list_item_demo_fling_exchange);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(stamp, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 4) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling4);
                text.setSecondaryText(R.string.list_item_demo_fling_4_text2);

                HtcListItemStockBoard board = (HtcListItemStockBoard) i.findViewById(R.id.stock);

                GradientDrawable gd = new GradientDrawable();
                gd.setCornerRadius(5);
                gd.setColor(getResources().getColor(R.color.stock_green));
                // As designer's comment, the outer of FHD is 2px and it of HD
                // is 1px.
                gd.setStroke(2, getResources().getColor(R.color.stock_gray));
                board.setBoardImageDrawable(gd); // API has not ready.

                board.setFrontText("15.99");
                board.setTextLine(0, "+0.79");
                board.setTextLine(1, "+1.20%");

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(board, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 5) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setTextNoContentStyle();
                text.setText(R.string.list_item_demo_fling_nocontent);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 6) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_6_bubble);
                text.setSecondaryTextVisibility(View.GONE);

                HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
                bubble.setBubbleCount(6);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                    setMargin(bubble, mMargin[M2], 0, 0, 0);
                } else {
                    ((HtcListItem) i).setLastComponentAlign(true);
                }

            } else if (position == 7) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_7_bubble);
                text.setSecondaryText(R.string.list_item_demo_fling_2);
                text.setIndicatorResource(R.drawable.icon_indicator_calendar);

                HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
                bubble.setUpperBound(77);
                bubble.setBubbleCount(100000);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(bubble, 0, 0, mMargin[M2], 0);
                } else {
                    ((HtcListItem) i).setVerticalDividerEnabled(true);
                }

            } else if (position == 8) {

                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_8_coloricon_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_2);

                if (mShowListItem) {
                    setMargin(text, 0, 0, mMargin[M1], 0);
                    if (mEnableAutomotive) {
                        setMargin(image, 0, 0, 0, 0);
                    } else {
                        image.setLayoutParams(new ListItem.LayoutParams(ListItem.LayoutParams.SIZE_WRAP_CONTENT | ListItem.LayoutParams.CENTER_VERTICAL));
                        setMargin(image, mMargin[M2], 0, mMargin[M2], 0);
                    }
                } else {
                    ((HtcListItem) i).setFirstComponentAlign(true);
                }

            } else if (position == 9) {
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                CommonUtil.wrapBorderDrawable(R.drawable.icon_category_photo, mContext, badge);
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);
                QuickContactBadgeCompat.setOverlay(badge, null);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_9_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_2);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 10) {
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                CommonUtil.wrapBorderDrawable(R.drawable.icon_category_photo, mContext, badge);
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);
                QuickContactBadgeCompat.setOverlay(badge, null);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_10_text2);
                text.setSecondaryTextSingleLine(false);
                text.setSecondaryText(R.string.list_item_demo_fling_10_photo);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 11) {
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                image.enableRoundSize(false);
                QuickContactBadge badge = image.getBadge();
                badge.setImageDrawable(getResources().getDrawable(R.drawable.icon_category_photo));
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);
                QuickContactBadgeCompat.setOverlay(badge, null);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_10_text2);
                text.setSecondaryTextSingleLine(false);
                text.setSecondaryText(R.string.list_item_demo_fling_10_photo);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            }else if (position == 12) {
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                QuickContactBadgeCompat.setOverlay(badge, null);
                CommonUtil.wrapBorderDrawable(R.drawable.icon_category_photo, mContext, badge);
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_11_text2);
                text.setSecondaryTextVisibility(View.GONE);

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText(R.string.list_item_demo_fling_exchange);
                stamp.setSecondaryTextVisibility(View.GONE);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, 0, 0);
                    setMargin(stamp, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 13) {

                HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
                CommonUtil.wrapBorderDrawable(R.drawable.icon_category_photo, mContext, image);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_12_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_2_ScaleType);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            }else if (position == 14) {

                HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
                image.enableRoundSize(false);
                image.setTileImageDrawable(getResources().getDrawable(R.drawable.icon_category_photo));

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_12_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_2_ScaleType);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }
            } else if (position == 15) {
                HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
                CommonUtil.wrapBorderDrawable(R.drawable.head, mContext, image);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_13_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_2_darkmode);

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText(R.string.list_item_demo_fling_exchange);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, 0, 0);
                    setMargin(stamp, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 16) {
                HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
                CommonUtil.wrapBorderDrawable(R.drawable.head, mContext, image);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_14_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_2);

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText(R.string.list_item_demo_fling_today);
                stamp.setSecondaryText(R.string.list_item_demo_fling_time);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, 0, 0);
                    setMargin(stamp, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 17) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_15_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_2);

                final HtcCheckBox checkBox = (HtcCheckBox) i.findViewById(R.id.checkBut);
                if (checkBox != null) {
                    checkBox.setChecked(true);
                }

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                } else {
                    ((HtcListItem) i).setLastComponentAlign(true);
                }

            } else if (position == 18) {
                HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
                CommonUtil.wrapBorderDrawable(R.drawable.head, mContext, image);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_16_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_2);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M2], 0);
                } else {
                    ((HtcListItem) i).setLastComponentAlign(true);
                    ((HtcListItem) i).setVerticalDividerEnabled(true);
                }

            } else if (position == 19) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_17);
                text.setSecondaryText(R.string.list_item_demo_fling_2_image);

                HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);

                stamp.setBadgeImageResource(0, R.drawable.icon_indicator_highpriority);
                stamp.setBadgeImageResource(1, R.drawable.icon_indicator_calendar);
                stamp.setBadgeImageResource(2, R.drawable.icon_indicator_highpriority);
                stamp.setBadgeImageResource(3, R.drawable.icon_indicator_highpriority);
                stamp.setBadgeImageResource(4, R.drawable.icon_indicator_highpriority);
                stamp.setBadgeImageResource(5, R.drawable.icon_indicator_calendar);

                stamp.setBadgeState(0, true);
                stamp.setBadgeState(1, true);
                stamp.setBadgeState(2, true);
                stamp.setBadgeState(3, true);
                stamp.setBadgeState(4, true);
                stamp.setBadgeState(5, true);
                stamp.setBadgeState(6, true);
                stamp.setTextStamp(R.string.list_item_demo_fling_exchange);

                stamp.setBadgesVerticalCenter(true);

                stamp.setFlagButtonOnCheckedChangeListener(new OnFlagButtonCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(HtcListItem7Badges1LineBottomStamp view,
                            boolean isChecked) {
                        Toast.makeText(HtcListItemActivity1.this,
                                isChecked ? "flag button checked " : "flag button unchecked",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                    setMargin(stamp, getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, mMargin[M1] + mMargin[M2] - getResources().getDimensionPixelOffset(R.dimen.margin_m), 0);
                }

            } else if (position == 20) {

                HtcListItemSingleText text = (HtcListItemSingleText) i.findViewById(R.id.text2);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setText(R.string.list_item_demo_fling_18_text);

                text.setText(R.string.list_item_demo_fling_18_text2);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                    setMargin(button, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 21) {
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                QuickContactBadgeCompat.setOverlay(badge, null);
                CommonUtil.wrapBorderDrawable(R.drawable.icon_category_photo, mContext, badge);
                badge.setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        Toast.makeText(HtcListItemActivity1.this, "Ah-Ha", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_19_click);
                text.setSecondaryText(R.string.list_item_demo_fling_2_quickbadge);
                text.setSecondaryTextSingleLine(false);

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText(R.string.list_item_demo_fling_time1);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M2], 0);
                    setMargin(stamp, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 22) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_20_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_20_text);

                if (mShowListItem) {
                    setMargin(text, 0, 0, mMargin[M1], 0);
                    if (mEnableAutomotive) {
                        setMargin(image, 0, 0, 0, 0);
                    } else {
                        image.setLayoutParams(new ListItem.LayoutParams(ListItem.LayoutParams.SIZE_WRAP_CONTENT | ListItem.LayoutParams.CENTER_VERTICAL));
                        setMargin(image, mMargin[M2], 0, mMargin[M2], 0);
                    }
                } else {
                    ((HtcListItem) i).setFirstComponentAlign(true);
                }

            } else if (position == 23) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_21_text2);
                text.setSecondaryTextSingleLine(false);
                text.setSecondaryText(R.string.list_item_demo_fling_20_text);

                if (mShowListItem) {
                    setMargin(text, 0, 0, mMargin[M1], 0);
                    if (mEnableAutomotive) {
                        setMargin(image, 0, 0, 0, 0);
                    } else {
                        image.setLayoutParams(new ListItem.LayoutParams(ListItem.LayoutParams.SIZE_WRAP_CONTENT | ListItem.LayoutParams.CENTER_VERTICAL));
                        setMargin(image, mMargin[M2], 0, mMargin[M2], 0);
                    }
                } else {
                    ((HtcListItem) i).setFirstComponentAlign(true);
                    ((HtcListItem) i).setFirstComponentTopMarginFixed(true);
                }

            } else if (position == 24) {
                HtcListItemSerialNumber number = (HtcListItemSerialNumber) i.findViewById(R.id.number);
                number.setNumber(2);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_22_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_default);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 25) {
                HtcListItemSerialNumber number = (HtcListItemSerialNumber) i.findViewById(R.id.number);
                number.setNumber(3);
                number.setDarkMode(true);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_23_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_dark_mode);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 26) {
                AutoCompleteTextView text = (AutoCompleteTextView) i.findViewById(R.id.autotext);
                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 27) {
                HtcEditText text = (HtcEditText) i.findViewById(R.id.autotext);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, getResources().getDimensionPixelOffset(R.dimen.margin_m));
                } else {
                    ((HtcListItem) i).setLastComponentAlign(true);
                }

            } else if (position == 28) {
                HtcListItemReversed2LineText text = (HtcListItemReversed2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_26_text2);
                text.setSecondaryText(R.string.list_item_demo_fling_26_text);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 29) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_27);
                text.setSecondaryText(R.string.list_item_demo_fling_27_text);

                HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
                stamp.setBubbleCount(6);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                    setMargin(stamp, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 30) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setText(R.string.list_item_demo_fling_28);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 31) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setText(R.string.list_item_demo_fling_29);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                    ((ListItem) i).setStartIndent(true);
                } else {
                    ((HtcListItem) i).setLeftIndent(true);
                }

            }
            else if (position == 32) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
                if(mShowKeep){
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setGravityCenterHorizontal(true);
                text.setText(getResources().getString(R.string.centeredtext_label));

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 33) {
                final Resources res = mContext.getResources();
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                Drawable drawable = ShapeUtil.createRoundStroke(mContext, res.getDimensionPixelOffset(R.dimen.htc_list_item_photo_frame_diameter));
                QuickContactBadgeCompat.setOverlay(badge, drawable);
                RoundedBitmapDrawable dr = null;
                try {
                    dr = BitmapUtil.getRoundedBitmapDrawable(mContext, R.drawable
                        .icon_category_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                badge.setImageDrawable(dr);
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                if (mShowKeep) {
                    text.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                text.setPrimaryText(R.string.list_item_demo_fling_11_text2);
                text.setSecondaryTextVisibility(View.GONE);

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText(R.string.list_item_demo_fling_exchange);
                stamp.setSecondaryTextVisibility(View.GONE);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, 0, 0);
                    setMargin(stamp, mMargin[M2], 0, mMargin[M1], 0);
                }

            }

            return i;
        }
    }
}
