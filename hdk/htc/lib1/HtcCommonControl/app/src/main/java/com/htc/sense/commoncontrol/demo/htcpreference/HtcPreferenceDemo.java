package com.htc.sense.commoncontrol.demo.htcpreference;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.Htc1LineListAdapter;
import com.htc.sense.commoncontrol.demo.R;

public class HtcPreferenceDemo extends CommonDemoActivityBase implements  OnItemClickListener{
    private ListView mListView = null;
    private final String[] INTENTS = {"android.intent.action.custom.activitypreferences",
            "android.intent.action.custom.fragmentpreferences",
            "android.intent.action.custom.preferencewithheaders",
            "android.intent.action.custom.preferencesdialog",
            "android.intent.action.custom.preferencesfromcode",
            "android.intent.action.custom.DefaultValues"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        mListView = (ListView) findViewById(android.R.id.list);
        Htc1LineListAdapter adapter=new Htc1LineListAdapter(this,new String[]{"1.Add Preference Using Activity","2.Add Preference from Using Fragment","3.Add Preference with header","4.Add Preference with Dialog"});
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
            int position, long id) {
        Intent intent =new Intent(INTENTS[position]);
        if(INTENTS[position] == "android.intent.action.custom.preferencesfromxml")
        {
            intent.putExtra(":android:show_fragment", true);
            intent.putExtra(":android:show_fragment_title", 2);
            intent.putExtra(":android:show_fragment_short_title", 2);

            intent.putExtra("extra_prefs_show_button_bar",true);
            intent.putExtra(":android:no_headers", true);
        }

        startActivityForResult(intent, 1);
    }
}
