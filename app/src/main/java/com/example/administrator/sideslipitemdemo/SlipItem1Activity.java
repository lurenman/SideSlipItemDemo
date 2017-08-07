package com.example.administrator.sideslipitemdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/8/4.
 */

public class SlipItem1Activity extends Activity {
    private SideSlipItem slipItem;
    private TextView tv_swipe_delete;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip1);
        tv_swipe_delete= (TextView) findViewById(R.id.tv_swipe_delete);
        slipItem= (SideSlipItem) findViewById(R.id.slide_item);
       // slipItem.setPosition(0);
        slipItem.setItemClickListener(new SideSlipItem.ItemClickListener() {
            @Override
            public void clickItem(int position) {
                Toast.makeText(getApplicationContext(),"条目被点击了",Toast.LENGTH_LONG).show();
            }
        });
        tv_swipe_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"lalalaa",Toast.LENGTH_SHORT).show();
                slipItem.closeDelete(true);
            }
        });

    }
}
