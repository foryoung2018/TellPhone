package com.htc.sense.commoncontrol.demo.listview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItemColorIcon;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.IDividerController;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;


public class HtcListViewWidthDividerControllerDemo extends CommonDemoActivityBase {
    private HtcListView mHtcListView = null;
    private static final int DATA_LENGTH = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htclistview_main4);
        mHtcListView = (HtcListView) findViewById(R.id.htc_list);
        if (mHtcListView != null) {
            mHtcListView.setAdapter(new ListViewAdapter(this, getData(), R.layout.list_item09));
            if (null != mHtcListView) {
                mHtcListView.setDividerController(new IDividerController() {
                    @Override
                    public int getDividerType(int position) {
                        if (position % 3 == 0) {
                            return IDividerController.DIVIDER_TYPE_NORAML;
                        } else if (position % 3 == 1) {
                            return IDividerController.DIVIDER_TYPE_SECTION;
                        } else {
                            return IDividerController.DIVIDER_TYPE_NONE;
                        }
                    }
                });
            }

        }
    }

    private String[] getData() {
        String[] data = new String[DATA_LENGTH];
        for (int i = 0; i < DATA_LENGTH; i++) {
            if (i % 3 == 0) {
                data[i] = "DIVIDER_TYPE_NORAML";
            } else if (i % 3 == 1) {
                data[i] = "DIVIDER_TYPE_SECTION";
            } else {
                data[i] = "DIVIDER_TYPE_NONE";
            }
        }
        return data;
    }

    public class ListViewAdapter extends BaseAdapter {
        String[] mData;
        private LayoutInflater mInflater;
        private int mLayoutId;

        public ListViewAdapter(Context context, String[] data, int layout) {
            mInflater = LayoutInflater.from(context);
            mData = data;
            mLayoutId = layout;
        }

        public int getCount() {
            return mData.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = mInflater.inflate(mLayoutId, parent, false);
            }
            ((HtcListItem)convertView).setFirstComponentAlign(true);
            HtcListItemColorIcon image = (HtcListItemColorIcon) convertView.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);

            HtcListItem2LineText text = (HtcListItem2LineText) convertView.findViewById(R.id.text1);
            text.setPrimaryText(mData[position]);
            text.setSecondaryTextVisibility(View.GONE);
            return convertView;
        }
    }
}
