package com.example.administrator.sideslipitemdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/4.
 */

public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private Set<SideSlipItem> sideSlipItems = new HashSet<>();
    private ArrayList<String> mData=new ArrayList<String>();
    public ListViewAdapter(Context context,ArrayList<String> list) {
        mContext=context;
        mData=list;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lv_item, parent, false);
            holder.tv_swipe_delete= (TextView) convertView.findViewById(R.id.tv_swipe_delete);
            holder.slipItem= (SideSlipItem) convertView.findViewById(R.id.slide_item);
            holder.tv_content=(TextView)convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_content.setText("lv_item"+position);
        holder.tv_swipe_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mData!=null&&!mData.isEmpty())
                {
                    Toast.makeText(mContext,position+"",Toast.LENGTH_SHORT).show();
                    mData.remove(position);
                    ListViewAdapter.this.notifyDataSetChanged();
                }
            }
        });
        //这块防止重用布局的改变
        holder.slipItem.closeDelete(false);
        holder.slipItem.setChangeListener(mChangeListener);


        return convertView;
    }

    private SideSlipItem.OtherItemSlideChnageListener mChangeListener = new SideSlipItem.OtherItemSlideChnageListener() {
        @Override
        public void openItem(SideSlipItem item) {
            for (SideSlipItem item1 : sideSlipItems) {
                item1.closeDelete(true);
            }
            sideSlipItems.add(item);
        }

        @Override
        public void closeItem(SideSlipItem item) {
            sideSlipItems.remove(item);
        }
    };


    class ViewHolder
    {
        private TextView tv_swipe_delete;
        private SideSlipItem slipItem;
        private TextView tv_content;
    }
}
