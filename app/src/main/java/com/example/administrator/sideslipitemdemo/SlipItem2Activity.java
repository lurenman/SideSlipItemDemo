package com.example.administrator.sideslipitemdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/4.
 */

public class SlipItem2Activity extends Activity {
    private ListView lv_list;
    private ListViewAdapter listViewAdapter;
    private ArrayList<String> mData=new ArrayList<String>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip2);
        for (int i=0;i<8;i++)
        {
            mData.add("data-----"+Integer.toString(i));
        }
        lv_list= (ListView) findViewById(R.id.lv_list);
        listViewAdapter=new ListViewAdapter(getApplicationContext(),mData);
        lv_list.setAdapter(listViewAdapter);
    }
}
