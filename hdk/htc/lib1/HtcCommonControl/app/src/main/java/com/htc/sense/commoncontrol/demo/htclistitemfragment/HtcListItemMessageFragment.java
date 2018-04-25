package com.htc.sense.commoncontrol.demo.htclistitemfragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.QuickContactBadge;

import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItemMessageBody;
import com.htc.lib1.cc.widget.HtcListItemQuickContactBadge;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class HtcListItemMessageFragment extends MyFragment {
    private static final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";
    LayoutInflater mInflater = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        setHasOptionsMenu(true);
        ListView rv = (ListView) inflater.inflate(
                R.layout.htclistitem_fragment, container, false);
        setupListView(rv);
        return rv;
    }
    private void setupListView(ListView listview) {
        listview.setAdapter(new MyListAdapter());
    }
    private class MyListAdapter extends BaseAdapter {
        Context mContext = getActivity();

        final int layoutsHtcListItem[] = new int[] {
                R.layout.list_item19, R.layout.list_item19, R.layout.list_item22,
                R.layout.list_item22
        };

        final int layoutsListItem[] = new int[] {
                R.layout.list_item19_new, R.layout.list_item19_new, R.layout.list_item22_new,
                R.layout.list_item22_new
        };

        final static int LIST_ITEM_COUNT = 4;

        public int getCount() {
            return LIST_ITEM_COUNT;
        }

        public Object getItem(int position) {
            return layoutsHtcListItem[position];
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return LIST_ITEM_COUNT;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup i = (ViewGroup) mInflater.inflate(MainActivity.mShowListItem? layoutsListItem[position] : layoutsHtcListItem[position], null);

            if (position == 0) {
                HtcCheckBox htcCheckBox = (HtcCheckBox) i.findViewById(R.id.checkImgBut);
                HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
                message.get7Badges1LineBottomStamp().setBadgeImageResource(0, R.drawable.icon_indicator_highpriority);
                message.get7Badges1LineBottomStamp().setBadgeImageResource(1, R.drawable.icon_indicator_calendar);
                message.get7Badges1LineBottomStamp().setBadgeState(0, true);
                message.get7Badges1LineBottomStamp().setBadgeState(1, true);
                message.get7Badges1LineBottomStamp().setBadgeState(6, true);
                message.get7Badges1LineBottomStamp().setTextStamp("STAMP");

                message.setPrimaryText("Text fLing 0");
                message.setSecondaryText("Text fLing 0");

                message.setBodyText("BodyText BodyText BodyText BodyText BodyText BodyText BodyText BodyText");
                message.setBodyTextLine(2);
                message.enableBodyText(false);

                if (MainActivity.mShowListItem) {
                    setMargin(htcCheckBox, mMargin[M1], 0, mMargin[M2], 0);
                } else {
                    ((HtcListItem) i).setFirstComponentTopMarginFixed(true);
                }

            } else if (position == 1) {
                HtcCheckBox htcCheckBox = (HtcCheckBox) i.findViewById(R.id.checkImgBut);
                HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
                message.setPrimaryText("Text fLing 1, a very very very long title here");
                message.setSecondaryText("Text fLing 1, a very very very long content here");
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
                message.get7Badges1LineBottomStamp().setTextStamp("STAMP");

                if (MainActivity.mShowListItem) {
                    setMargin(htcCheckBox, mMargin[M1], 0, mMargin[M2], 0);
                } else {
                    ((HtcListItem) i).setFirstComponentTopMarginFixed(true);
                }

            } else if (position == 2) {
                HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                CommonUtil.wrapBorderDrawable(R.drawable.icon_category_photo, mContext, badge);
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);
                message.setPrimaryText("Text fLing 2");
                message.setSecondaryText("QuickContactBadge has problem in handling mask");
                message.setBodyText("BodyText BodyText BodyText BodyText BodyText BodyText BodyText BodyText");
                message.setBodyTextLine(2);
                message.enableBodyText(false);

                if (MainActivity.mShowListItem) {
                    setMargin(image, 0, 0, mMargin[M2], 0);
                }

            } else if (position == 3) {
                HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);

                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();

                CommonUtil.wrapBorderDrawable(R.drawable.icon_category_photo, mContext, badge);
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

                message.setPrimaryText("Text fLing 3 primary");
                message.setSecondaryText("Text fLing 3 secondary");
                message.setBodyText("BodyText BodyText BodyText BodyText123456 BodyText BodyText BodyText BodyText123456");
                message.setBodyTextMaxLines(2);

                if (MainActivity.mShowListItem) {
                    setMargin(image, 0, 0, mMargin[M2], 0);
                }

            }

            return i;
        }
    }
}
