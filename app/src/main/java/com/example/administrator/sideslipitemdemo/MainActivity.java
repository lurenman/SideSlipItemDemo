package com.example.administrator.sideslipitemdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_click1)
    TextView tv_Click1;
    @BindView(R.id.tv_click_2)
    TextView tv_Click2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_click1, R.id.tv_click_2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_click1:
                //单个条目演示
                Intent intent=new Intent(MainActivity.this,SlipItem1Activity.class);
                startActivity(intent);
                break;
            case R.id.tv_click_2:
                //多个条目演示
                Intent intent1=new Intent(MainActivity.this,SlipItem2Activity.class);
                startActivity(intent1);

                break;
        }
    }
}
