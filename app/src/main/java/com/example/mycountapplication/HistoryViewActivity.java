package com.example.mycountapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryViewActivity extends AppCompatActivity {
    private DBHelper helper;
    private ListView listView;
    private TextView tv_money, tv_year, tv_month, tv_day;
    private Button check;
    private List<costList> list;
    ArrayList<Double> flow;
    Spinner spinner2;
    String mMonth, mYear, mDay;
    int select, type;
    TextView Sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        type = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);
        initView();
        initData();

        spinner2 = findViewById(R.id.spinner3);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text= spinner2.getItemAtPosition(i).toString();
                if(text.equals("支出")) {
                    type = 0;
                }else if(text.equals("收入")) {
                    type = 1;
                }
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
        db.close();
    }

    private void initView() {
        helper = new DBHelper(HistoryViewActivity.this);
        tv_money = findViewById(R.id.textView2);
        tv_year = findViewById(R.id.et_year_input);
        tv_month = findViewById(R.id.et_month_input);
        tv_day = findViewById(R.id.et_day_input);
        check = findViewById(R.id.checkButton);
        Sum = findViewById(R.id.textView2);
        listView = findViewById(R.id.list_view2);
    }

    //事件：查看历史账单
    public void checkMyHistory(View view) {
        getMonth();//获取输入的时间
        select = checkDate();
        caclSum(select, type);
    }

    private void caclSum(int select, int type) {
        String text = null;
        String selection = null;
        flow = new ArrayList<Double>();//现金流
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor = null;
        list = new ArrayList<>();
        switch (select) {
            case 0:
                text = "请输入正确数据";
                break;
            case 1:
                text = mYear+"年";
                selection = "Year = ? AND Type = ?";
                cursor=db.query("account",
                        null,
                        selection,
                        new String[] {mYear,String.valueOf(type)},
                        null,
                        null,
                        null);
                doCursor(cursor);
                break;
            case 2:
                text = mYear+"年"+mMonth+"月";
                selection = "Year = ? AND Month = ? AND Type = ?";
                cursor=db.query("account",
                        null,
                        selection,
                        new String[] {mYear,mMonth,String.valueOf(type)},
                        null,
                        null,
                        null);
                doCursor(cursor);
                break;
            case 3:
                text = mYear+"年"+mMonth+"月"+mDay+"日";
                selection = "Year = ? AND Month = ? AND Day = ? AND Type = ?";
                cursor=db.query("account",
                        null,
                        selection,
                        new String[] {mYear,mMonth,mDay,String.valueOf(type)},
                        null,
                        null,
                        null);
                doCursor(cursor);
                break;
        }
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        //绑定适配器
        listView.setAdapter(new ListAdapter(this,list));
        db.close();

        BigDecimal sumFlow = new BigDecimal("0.00");
        for(int i = 0;i < flow.size();i++) {
            BigDecimal temp = new BigDecimal(flow.get(i).toString());
            sumFlow = sumFlow.add(temp);
        }
        String sign = null;
        if(type == 0) {
            sign = "-";
        }else if(type == 1) {
            sign = "+";
        }
        sign = sign + sumFlow;
        Sum.setText(sign);
    }

    @SuppressLint("Range")
    public void doCursor(Cursor cursor) {
        flow = new ArrayList<Double>();
        String tempFlow = null;
        while (cursor.moveToNext()) {
            costList clist=new costList();//构造实例
            clist.set_id(cursor.getString(cursor.getColumnIndex("_id")));
            clist.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
            clist.setDate(cursor.getString(cursor.getColumnIndex("Date")));
            clist.setMoney(cursor.getString(cursor.getColumnIndex("Money")));
            clist.setType(cursor.getInt(cursor.getColumnIndex("Type")));
            list.add(clist);
            tempFlow = cursor.getString(cursor.getColumnIndex("Money"));
            flow.add(Double.valueOf(tempFlow));
        }
    }

    public void getMonth() {
        mYear = tv_year.getText().toString();
        mMonth = tv_month.getText().toString();
        mDay = tv_day.getText().toString();
    }

    public int checkDate() {
        if(mYear.length() == 4) {//有年份
            if(mMonth.length() == 1 | mMonth.length() == 2){//有月份
                if(mDay.length() == 1 | mDay.length() == 2) {//有日期
                    return 3;
                }else {//无日期
                    return 2;
                }
            }else {//无月份
                return 1;
            }
        }
        return 0;
    }
}