package com.htc.sense.commoncontrol.demo.griditem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.htc.lib1.cc.widget.HtcGridItem;
import com.htc.lib1.cc.widget.HtcGridView;
import com.htc.sense.commoncontrol.demo.R;

import java.text.NumberFormat;
import java.util.ArrayList;



/**
 * A grid that displays a set of framed photos.
 *
 */
public class HtcGridItemActivity extends HtcGridViewBaseActivity {

    private GridViewAdapter mAdapter;

    final static int SINGLE_LINE_PRIMARY = 0;
    final static int TWO_LINE_PRIMARY = 1;
    final static int PRIMARY_SECONDARY = 2;
    final static int SHOW_INDICATOR = 4;
    int mItemType = TWO_LINE_PRIMARY;
    boolean mShowIndicator = false;
    String mShowIndicatorText = "Show Indicator";
    String mHideIndicatorText = "Hide Indicator";
    LayoutInflater mInflater;

    @Override
    protected void init() {
        setContentView(R.layout.htcgriditem_main);
        mAdapter = new GridViewAdapter(this);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGrid = (HtcGridView) findViewById(R.id.grid);
        mGrid.setAdapter(mAdapter);
        // Specify MODE_GENERIC mode
        mGrid.setMode(HtcGridView.MODE_GENERIC);

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HtcGridItem item = (HtcGridItem) view;
                // Set the deleted state for the clicked grid item.
                if (mAdapter.getCheckState(position)) {
                    item.setItemDeleted(false);
                    mAdapter.setCheckState(position, false);
                } else {
                    item.setItemDeleted(true);
                    mAdapter.setCheckState(position, true);
                }

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mThumbIdArrayList.clear();
        mItemCheckedArrayList.clear();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, SINGLE_LINE_PRIMARY,0, "1 line Primary");
        menu.add(0, TWO_LINE_PRIMARY,0, "2 line Primary");
        menu.add(0, PRIMARY_SECONDARY,0, "Primary + Secondary");

        String title = mShowIndicator ? mHideIndicatorText : mShowIndicatorText;
        menu.add(0, SHOW_INDICATOR, 0, title);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String title = "";
        switch(item.getItemId()){
        case SINGLE_LINE_PRIMARY:
            mItemType = SINGLE_LINE_PRIMARY;
            mGrid.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
//            mGrid.setMode(GridView.MODE_GENERIC);
            break;

        case TWO_LINE_PRIMARY:
            mItemType = TWO_LINE_PRIMARY;
            mGrid.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
//            mGrid.setMode(GridView.MODE_GENERIC);
            break;

        case PRIMARY_SECONDARY:
            mItemType = PRIMARY_SECONDARY;
            mGrid.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
//            mGrid.setMode(GridView.MODE_GENERIC);
            break;

        case SHOW_INDICATOR:
            mShowIndicator = !mShowIndicator;
            title = mShowIndicator ? mHideIndicatorText : mShowIndicatorText;
            item.setTitle(title);
            mGrid.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            break;
        }
        return true;
    }

    /**
     * Total Grid Item Count
     */
    int mItemCount = 150;

    private String[] mPrimaryTexts;

    private String[] mTwoLinePrimaryTexts;

    private String[] mSecondaryTexts;

    private ArrayList<Integer> mThumbIdArrayList = new ArrayList<Integer>();

    // Running state of which positions are currently checked
    private ArrayList<Boolean> mItemCheckedArrayList = new ArrayList<Boolean>();
    /**
     * Image Source Array
     */
    private Integer[] mThumbIds = { R.drawable.htcgridview_sample_0,
            R.drawable.htcgridview_sample_1,
            R.drawable.htcgridview_sample_2,
            R.drawable.htcgridview_sample_3,
            R.drawable.htcgridview_sample_4,
            R.drawable.htcgridview_sample_5,
            R.drawable.htcgridview_sample_6,
            R.drawable.htcgridview_sample_7,};

    public class GridViewAdapter extends BaseAdapter {
        Drawable indicator;
        String item;
        public GridViewAdapter(Context c) {
            Resources res = c.getResources();
            item = res.getString(R.string.gridview_demo_text);
            String secondary = res.getString(R.string.gridview_demo_secondary);
            String primaryText = res.getString(R.string.griditem_demo_primarytext);
            String secondaryText = res.getString(R.string.griditem_demo_secondarytext);
            String twoLineText = res.getString(R.string.griditem_demo_twoline_primarytext);

            NumberFormat nf = NumberFormat.getInstance();
            mPrimaryTexts = new String[mItemCount];
            mSecondaryTexts = new String[mItemCount];
            mTwoLinePrimaryTexts = new String[mItemCount];
            indicator = getResources().getDrawable(R.drawable.icon_indicator_facebook_s);
            for (int i = 0; i < mItemCount; i++) {
                mItemCheckedArrayList.add(i, false);
                mThumbIdArrayList.add(i, mThumbIds[i%8]);
                mPrimaryTexts[i] = item + nf.format(i) + primaryText;
                mSecondaryTexts[i] = secondary + nf.format(i) +  secondaryText;
                mTwoLinePrimaryTexts[i] = item + nf.format(i) + twoLineText;
            }
        }

        public int getCount() {
            return mThumbIdArrayList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = new HtcGridItem(parent.getContext());
            }
            HtcGridItem gridItem = null;
            switch(mItemType) {
                case PRIMARY_SECONDARY:
                    gridItem = (HtcGridItem) convertView;
                    gridItem.setPrimaryText(mPrimaryTexts[position]);
                    ((HtcGridItem)gridItem).setSecondaryText(mSecondaryTexts[position]);
                    break;
                case SINGLE_LINE_PRIMARY:
                    gridItem = (HtcGridItem) convertView;
                    gridItem.setPrimaryText(item + position);
                  break;
                case TWO_LINE_PRIMARY:
                    gridItem = (HtcGridItem) convertView;
                    gridItem.setPrimaryText(mTwoLinePrimaryTexts[position]);
                    // Enable two line primary text
                    gridItem.setTwoLinePrimaryText(true);
                    break;
            }

            // Show/hide indicator
            gridItem.setIndicator(mShowIndicator ? indicator : null);

            // Set the image
            gridItem.getImage().setImageResource(mThumbIdArrayList.get(position));

            // Set the deleted state according to the current checked state of this grid item.
            gridItem.setItemDeleted(this.getCheckState(position));

            return convertView;

        }

        public boolean getCheckState(int index) {
            return mItemCheckedArrayList.get(index);
        }

        public void setCheckState(int index, boolean state) {
            mItemCheckedArrayList.set(index, state);
        }

        public boolean IsChecked(int position) {
            return mItemCheckedArrayList.get(position);
        }
    }

    public HtcGridView getGridView(){
        return mGrid;
    }

    public GridViewAdapter getAdapter() {
        return mAdapter;
    }
}