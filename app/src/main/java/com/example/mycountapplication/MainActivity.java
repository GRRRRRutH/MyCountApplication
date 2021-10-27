package com.example.mycountapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private DBHelper helper;
    private  ListView listView;
    private  ImageButton Add;
    private List<costList>list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    //初始化
    @SuppressLint("Range")
    private void initData() {
        list=new ArrayList<>();
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.query("account",null,null,null,null, null,null);
        while (cursor.moveToNext()){
            costList clist=new costList();//构造实例
            clist.set_id(cursor.getString(cursor.getColumnIndex("_id")));
            clist.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
            clist.setDate(cursor.getString(cursor.getColumnIndex("Date")));
            clist.setMoney(cursor.getString(cursor.getColumnIndex("Money")));
            list.add(clist);
        }
        //绑定适配器
        listView.setAdapter(new ListAdapter(this,list));
        //绑定点击事件
        listView.setOnItemClickListener(this);
        db.close();
    }

    private void initView() {
        helper=new DBHelper(MainActivity.this);
        listView = findViewById(R.id.list_view);
        Add=findViewById(R.id.add);
    }

    //事件：添加
    public void addAccount(View view){//跳转
        Intent intent=new Intent(MainActivity.this,new_cost.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==1)
        {
            this.initData();
        }else if(requestCode==2&&resultCode==2) {
            this.initData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        costList itemAtPosition = (costList) listView.getItemAtPosition(i);
        String costTitle = itemAtPosition.getTitle().trim();
        String costMoney = itemAtPosition.getMoney().trim();
        String id = itemAtPosition.get_id();

        Intent changeCost = new Intent(this, AlterCostActivity.class);//this当前activity对象
        changeCost.putExtra("costTitle", costTitle);
        changeCost.putExtra("costMoney", costMoney);
        changeCost.putExtra("id",id);
        startActivityForResult(changeCost, 2);
    }
}