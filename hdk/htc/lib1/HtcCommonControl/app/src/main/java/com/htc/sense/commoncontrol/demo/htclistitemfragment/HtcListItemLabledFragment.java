package com.htc.sense.commoncontrol.demo.htclistitemfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.htc.lib1.cc.widget.HtcListItemLabeledLayout;
import com.htc.sense.commoncontrol.demo.R;


public class HtcListItemLabledFragment extends MyFragment {
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
    class MyListAdapter extends android.widget.BaseAdapter {
        int ITEM_TYPE = 4;
        Context mContext = getActivity();

        public int getCount() {
            return 4;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean isEnabled(int position) {
            return true;
        }

        public int getViewType(int position) {
            return position % ITEM_TYPE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (position == 0) {
                if (MainActivity.mShowListItem) {
                    convertView = inflater.inflate(R.layout.list_item30_new, null, false);
                } else {
                    convertView = inflater.inflate(R.layout.list_item30, null, false);
                }

            } else if (position == 1) {
                HtcListItemLabeledLayout labeledLayout = new HtcListItemLabeledLayout(mContext);
                AutoCompleteTextView input = new AutoCompleteTextView(mContext);
                input.setTextAppearance(mContext, com.htc.lib1.cc.R.style.list_primary_m);
                labeledLayout.addView(input);
                labeledLayout.setLabelText(R.string.label_text);
                convertView = labeledLayout;

            } else if (position == 2) {
                if (MainActivity.mShowListItem) {
                    convertView = inflater.inflate(R.layout.list_item34_new, null, false);
                } else {
                    convertView = inflater.inflate(R.layout.list_item34, null, false);
                }

            } else if (position == 3) {
                HtcListItemLabeledLayout labeledLayout = new HtcListItemLabeledLayout(mContext);
                AutoCompleteTextView input = new AutoCompleteTextView(mContext);
                input.setTextAppearance(mContext, com.htc.lib1.cc.R.style.list_primary_m);
                labeledLayout.addView(input);
                labeledLayout.setLabelTextAllCapsFalse();
                labeledLayout.setLabelText(R.string.description_text);
                convertView = labeledLayout;
            }

            return convertView;
        }
    }
}
