package com.example.mycountapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ListAdapter extends BaseAdapter{
    List<costList> mList;

    public ListAdapter(List<costList>list)
    {
        mList=list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //0是支出，1是收入
        costList item = mList.get(position);
        int type = item.getType();
        View view = null;
        if(type == 0) {
            view = mLayoutInflater.inflate(R.layout.list_item2,null);
        }else if (type == 1) {
            view = mLayoutInflater.inflate(R.layout.list_item,null);
        }
        //绑定
        TextView tv_title=view.findViewById(R.id.tv_title);
        TextView tv_date=view.findViewById(R.id.tv_date);
        TextView tv_money=view.findViewById(R.id.tv_money);
        //赋值
        tv_title.setText(item.getTitle());
        tv_date.setText(item.getDate());
        tv_money.setText(item.getMoney());
        return view;
    }

    private List<costList>getmList;
    private LayoutInflater mLayoutInflater;

    public ListAdapter(Context context,List<costList>list)
    {
        mList=list;
        //通过外部传来的Context初始化LayoutInflater对象
        mLayoutInflater=LayoutInflater.from(context);
    }
}