package com.htc.sense.commoncontrol.demo.htclistitemfragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItem1LineTextProgressBar;
import com.htc.lib1.cc.widget.HtcListItem2LineTextProgressBar;
import com.htc.lib1.cc.widget.HtcListItemColorIcon;
import com.htc.lib1.cc.widget.HtcProgressBar;
import com.htc.lib1.cc.widget.ListItem;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class HtcListItemProgressFragment extends MyFragment {
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
                R.layout.list_item27, R.layout.list_item28, R.layout.list_item28,
                R.layout.list_item29, R.layout.list_item29
        };

        final int layoutsListItem[] = new int[] {
                R.layout.list_item27_new, R.layout.list_item28_new, R.layout.list_item28_new,
                R.layout.list_item29_new, R.layout.list_item29_new
        };

        final int LIST_ITEM_COUNT = layoutsHtcListItem.length;

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
            ViewGroup i = (ViewGroup) mInflater.inflate(MainActivity.mShowListItem ? layoutsListItem[position] : layoutsHtcListItem[position], null);

            if (position == 0) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
                text.setText("Text fLing 3, centeredText.");
                ProgressBar pb = new ProgressBar(mContext, null, android.R.attr.progressBarStyleSmall);
                Drawable progressDrawable = getResources().getDrawable(R.drawable.progress_light);
                pb.setIndeterminateDrawable(progressDrawable);
                pb.setVisibility(View.VISIBLE);
                text.setView(pb);

                if (MainActivity.mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 1) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineTextProgressBar progress = (HtcListItem2LineTextProgressBar) i.findViewById(R.id.progress);
                progress.setPrimaryText("Text fLing 4, 2LineTextProgressBar");
                progress.setSecondaryText("Text2");

                if (MainActivity.mShowListItem) {
                    setMargin(image, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(progress, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 2) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineTextProgressBar progress = (HtcListItem2LineTextProgressBar) i.findViewById(R.id.progress);
                progress.setPrimaryText("Text fLing 5, 2LineTextProgressBar");
                progress.setSecondaryText("Text2");
                HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
                mBar.setProgress(50);

                if (MainActivity.mShowListItem) {
                    setMargin(image, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(progress, 0, 0, mMargin[M1], 0);
                } else {
                    ((HtcListItem) i).setFirstComponentTopMarginFixed(true);
                }

            } else if (position == 3) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem1LineTextProgressBar progress = (HtcListItem1LineTextProgressBar) i.findViewById(R.id.progress);
                progress.setPrimaryText("Text fLing 6, 1LineTextProgressBar");
                HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
                mBar.setProgress(50);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                if (MainActivity.mShowListItem) {
                    setMargin(image, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(progress, 0, 0, mMargin[M2], 0);
                    setMargin(button, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 4) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem1LineTextProgressBar progress = (HtcListItem1LineTextProgressBar) i.findViewById(R.id.progress);
                progress.setPrimaryText("Text fLing 7, 1LineTextProgressBar");
                HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
                mBar.setProgress(50);
                progress.setStampText("Stamp");

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (MainActivity.mShowListItem) {
                    setMargin(image, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(progress, 0, 0, mMargin[M2], 0);
                    button.setLayoutParams(new ListItem.LayoutParams(ListItem.LayoutParams.SIZE_147 | ListItem.LayoutParams.CENTER_VERTICAL | ListItem.LayoutParams.DIVIDER_START));
                    setMargin(button, 0, 0, 0, 0);
                } else {
                    ((HtcListItem) i).setVerticalDividerEnabled(true);
                }

            }
            return i;
        }

    }


}
