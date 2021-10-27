package com.example.mycountapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private DBHelper helper;
    private ListView listView;
    private ImageButton Add;
    private List<costList> list;
    ArrayList<Double> flow;
    TextView Sum;
    String mMonth, mYear, mDay;
    Spinner spinner;
    int select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        select = 1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMonth();
        initView();
        initData();

        Resources res =getResources();
        String[] city=res.getStringArray(R.array.spingarr);//将province中内容添加到数组city中
        spinner = (Spinner) findViewById(R.id.spinner);//获取到spacer1
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,city);//创建Arrayadapter适配器
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//通过此方法为下拉列表设置点击事件
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text= spinner.getItemAtPosition(i).toString();
                if(text.equals("本日支出")) {
                    select = 1;
                }else if(text.equals("本月支出")) {
                    select = 2;
                }else if(text.equals("本年支出")) {
                    select = 3;
                }
                caclSum(select);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //初始化
    @SuppressLint("Range")
    private void initData() {
        list = new ArrayList<>();
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.query("account",null,null,null,null, null,null);
        while (cursor.moveToNext()) {
            costList clist=new costList();//构造实例
            clist.set_id(cursor.getString(cursor.getColumnIndex("_id")));
            clist.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
            clist.setDate(cursor.getString(cursor.getColumnIndex("Date")));
            clist.setMoney(cursor.getString(cursor.getColumnIndex("Money")));
            clist.setType(cursor.getInt(cursor.getColumnIndex("Type")));
            list.add(clist);
        }
        //绑定适配器
        listView.setAdapter(new ListAdapter(this,list));
        //绑定点击事件
        listView.setOnItemClickListener(this);
        db.close();
        caclSum(select);
    }

    public void getMonth() {
        Calendar calendar = Calendar.getInstance();
        mYear = String.valueOf(calendar.get(Calendar.YEAR));
        mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);        //获取日期的月
        mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));      //获取日期的天
    }

    public void caclSum(int select) {
        flow = new ArrayList<Double>();
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor = null;
        if(select == 1) {//日
            cursor=db.query("account",new String[] {"Money"},
                    "Year = ? AND Month = ? AND Day = ? AND Type = ?",
                    new String[] {mYear,mMonth,mDay,"0"},
                    null,
                    null,
                    null);
        }else if(select == 2) {//月
            cursor=db.query("account",new String[] {"Money"},
                    "Year = ? AND Month = ? AND Type = ?",
                    new String[] {mYear,mMonth,"0"},
                    null,
                    null,
                    null);
        }else if(select == 3) {//年
            cursor=db.query("account",new String[] {"Money"},
                    "Year = ? AND Type = ?",
                    new String[] {mYear,"0"},
                    null,
                    null,
                    null);
        }
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String s = cursor.getString(cursor.getColumnIndex("Money"));
            flow.add(Double.valueOf(s));
        }
        db.close();
        BigDecimal sumFlow = new BigDecimal("0.00");
        for(int i = 0;i < flow.size();i++) {
            BigDecimal temp = new BigDecimal(flow.get(i).toString());
            sumFlow = sumFlow.add(temp);
        }
        Sum.setText(String.valueOf(sumFlow));
    }

    private void initView() {
        helper = new DBHelper(MainActivity.this);
        listView = findViewById(R.id.list_view);
        Add = findViewById(R.id.add);
        Sum = findViewById(R.id.textView2);
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