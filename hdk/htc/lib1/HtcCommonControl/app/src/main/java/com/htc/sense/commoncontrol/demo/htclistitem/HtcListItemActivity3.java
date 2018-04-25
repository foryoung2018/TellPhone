package com.htc.sense.commoncontrol.demo.htclistitem;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;

import com.htc.lib1.cc.support.widget.QuickContactBadgeCompat;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItemMessageBody;
import com.htc.lib1.cc.widget.HtcListItemQuickContactBadge;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class HtcListItemActivity3 extends HtcListActivity {
    LayoutInflater mInflater = null;
    private final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setListAdapter(new MyListAdapter(this));
        getListView().setCacheColorHint(Color.TRANSPARENT);;
        if(mShowKeep){
            mShowListItem=false;
        }
    }

    @Override
    protected void initMenu() {
        mAddMenuShowListItem = true;
        mAddMenuShowKeep = true;
    }

    private class MyListAdapter extends BaseAdapter {
        Context mContext = null;

        final int layoutsHtcListItem[] = new int[] {
                R.layout.list_item19, R.layout.list_item19, R.layout.list_item22,
                R.layout.list_item22
        };

        final int layoutsListItem[] = new int[] {
                R.layout.list_item19_new, R.layout.list_item19_new, R.layout.list_item22_new,
                R.layout.list_item22_new
        };

        final static int LIST_ITEM_COUNT = 4;

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

            if (position == 0) {
                HtcCheckBox htcCheckBox = (HtcCheckBox) i.findViewById(R.id.checkImgBut);
                HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
                if(mShowKeep){
                    message.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                message.get7Badges1LineBottomStamp().setBadgeImageResource(0, R.drawable.icon_indicator_highpriority);
                message.get7Badges1LineBottomStamp().setBadgeImageResource(1, R.drawable.icon_indicator_calendar);
                message.get7Badges1LineBottomStamp().setBadgeState(0, true);
                message.get7Badges1LineBottomStamp().setBadgeState(1, true);
                message.get7Badges1LineBottomStamp().setBadgeState(6, true);
                message.get7Badges1LineBottomStamp().setTextStamp(R.string.list_item_demo_text_STAMP);

                message.setPrimaryText(R.string.list_item_demo_fling_0);
                message.setSecondaryText(R.string.list_item_demo_fling_0);

                message.setBodyText("BodyText BodyText BodyText BodyText BodyText BodyText BodyText BodyText");
                message.setBodyTextLine(2);
                message.enableBodyText(false);

                if (mShowListItem) {
                    setMargin(htcCheckBox, mMargin[M1], 0, mMargin[M2], 0);
                } else {
                    ((HtcListItem) i).setFirstComponentTopMarginFixed(true);
                }

            } else if (position == 1) {
                HtcCheckBox htcCheckBox = (HtcCheckBox) i.findViewById(R.id.checkImgBut);
                HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
                if(mShowKeep){
                    message.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                message.setPrimaryText(R.string.list_item_demo_fling_1_title);
                message.setSecondaryText(R.string.list_item_demo_fling_1_content);
                message.setBodyText("BodyText. get ready for the next battle. get ready for the next battle. get ready for the next battle. get ready for the next battle. get ready for the next battle. ");
                message.setBodyTextLine(2);

                message.get7Badges1LineBottomStamp().setBadgeImageResource(0, R.drawable.icon_indicator_highpriority);
                message.get7Badges1LineBottomStamp().setBadgeImageResource(1, R.drawable.icon_indicator_calendar);
                message.get7Badges1LineBottomStamp().setBadgeImageResource(2, R.drawable.icon_indicator_highpriority);
                message.get7Badges1LineBottomStamp().setBadgeImageResource(3, R.drawable.icon_indicator_highpriority);
                message.get7Badges1LineBottomStamp().setBadgeImageResource(4, R.drawable.icon_indicator_highpriority);
                message.get7Badges1LineBottomStamp().setBadgeImageResource(5, R.drawable.icon_indicator_calendar);
                message.get7Badges1LineBottomStamp().setBadgeState(0, true);
                message.get7Badges1LineBottomStamp().setBadgeState(1, true);
                message.get7Badges1LineBottomStamp().setBadgeState(6, true);
                message.get7Badges1LineBottomStamp().setTextStamp(R.string.list_item_demo_text_STAMP);

                if (mShowListItem) {
                    setMargin(htcCheckBox, mMargin[M1], 0, mMargin[M2], 0);
                } else {
                    ((HtcListItem) i).setFirstComponentTopMarginFixed(true);
                }

            } else if (position == 2) {
                HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
                if(mShowKeep){
                    message.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                QuickContactBadgeCompat.setOverlay(badge, null);
                CommonUtil.wrapBorderDrawable(R.drawable.icon_category_photo, mContext, badge);
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);
                message.setPrimaryText(R.string.list_item_demo_fling_2);
                message.setSecondaryText(R.string.list_item_demo_quickContactBadge);
                message.setBodyText("BodyText BodyText BodyText BodyText BodyText BodyText BodyText BodyText");
                message.setBodyTextLine(2);
                message.enableBodyText(false);

                if (mShowListItem) {
                    setMargin(image, 0, 0, mMargin[M2], 0);
                }

            } else if (position == 3) {
                HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
                if(mShowKeep){
                    message.notifyItemMode(HtcListItem.MODE_KEEP_MEDIUM_HEIGHT);
                }

                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                QuickContactBadgeCompat.setOverlay(badge, null);
                CommonUtil.wrapBorderDrawable(R.drawable.icon_category_photo, mContext, badge);
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

                message.setPrimaryText(R.string.list_item_demo_fling_3_primary);
                message.setSecondaryText(R.string.list_item_demo_fling_3_secondary);
                message.setBodyText("BodyText BodyText BodyText BodyText123456 BodyText BodyText BodyText BodyText123456");
                message.setBodyTextMaxLines(2);

                if (mShowListItem) {
                    setMargin(image, 0, 0, mMargin[M2], 0);
                }

            }

            return i;
        }
    }
}
