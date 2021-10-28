package com.example.mycountapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    Spinner spinner, spinner2;
    int select, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        select = 1;
        type = 0;//默认为支出
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMonth();
        initView();
        initData();

        spinner = (Spinner)findViewById(R.id.spinner);//获取到spacer1
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//通过此方法为下拉列表设置点击事件
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text= spinner.getItemAtPosition(i).toString();
                if(text.equals("本日")) {
                    select = 1;
                }else if(text.equals("本月")) {
                    select = 2;
                }else if(text.equals("本年")) {
                    select = 3;
                }
                caclSum(select,type);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text= spinner2.getItemAtPosition(i).toString();
                if(text.equals("支出")) {
                    type = 0;
                }else if(text.equals("收入")) {
                    type = 1;
                }
                caclSum(select,type);
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
        Cursor cursor=db.query("account",
                null,
                null,
                null,
                null,
                null,
                null);
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
        caclSum(select,type);
    }

    public void getMonth() {
        Calendar calendar = Calendar.getInstance();
        mYear = String.valueOf(calendar.get(Calendar.YEAR));
        mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);        //获取日期的月
        mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));      //获取日期的天
    }

    public void caclSum(int select, int type) {
        flow = new ArrayList<Double>();
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor = null;
        String t = String.valueOf(type);
        if(select == 1) {//日
            cursor=db.query("account",new String[] {"Money"},
                    "Year = ? AND Month = ? AND Day = ? AND Type = ?",
                    new String[] {mYear,mMonth,mDay,t},
                    null,
                    null,
                    null);
        }else if(select == 2) {//月
            cursor=db.query("account",new String[] {"Money"},
                    "Year = ? AND Month = ? AND Type = ?",
                    new String[] {mYear,mMonth,t},
                    null,
                    null,
                    null);
        }else if(select == 3) {//年
            cursor=db.query("account",new String[] {"Money"},
                    "Year = ? AND Type = ?",
                    new String[] {mYear,t},
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
        String text = null;
        if(type == 0) {
            text = "-";
        }else if(type == 1) {
            text = "+";
        }
        text = text + sumFlow;
        Sum.setText(text);
    }

    private void initView() {
        helper = new DBHelper(MainActivity.this);
        listView = findViewById(R.id.list_view);
        Add = findViewById(R.id.add);
        Sum = findViewById(R.id.textView2);
    }

    //事件：添加
    public void addAccount(View view) {//跳转
        Intent intent = new Intent(MainActivity.this, new_cost.class);
        startActivityForResult(intent,1);
    }

    //事件：查看历史账单
    public void checkHistory(View view) {
        Intent intent = new Intent(MainActivity.this, HistoryViewActivity.class);
        startActivity(intent);
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
        String  type = String.valueOf(itemAtPosition.getType());

        Intent changeCost = new Intent(this, AlterCostActivity.class);//this当前activity对象
        changeCost.putExtra("costTitle", costTitle);
        changeCost.putExtra("costMoney", costMoney);
        changeCost.putExtra("id",id);
        changeCost.putExtra("type",type);
        startActivityForResult(changeCost, 2);
    }
}