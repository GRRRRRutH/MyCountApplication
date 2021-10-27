package com.example.mycountapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class new_cost extends AppCompatActivity {
    private DBHelper helper;
    private EditText et_cost_title;
    private EditText et_cost_money;
    private DatePicker dp_cost_date;
    Spinner spinner;
    int select, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        select = 1;
        type = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cost);
        initView();

        spinner = (Spinner) findViewById(R.id.spinner_ci);//获取到spacer
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//通过此方法为下拉列表设置点击事件
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text= spinner.getItemAtPosition(i).toString();
                if(text.equals("支出")) {
                    select = 1;
                    type = 0;
                    et_cost_title.setHint("Cost Title");
                    et_cost_money.setHint("Cost Money");
                }else if(text.equals("收入")) {
                    select = 2;
                    type = 1;
                    et_cost_title.setHint("Income Title");
                    et_cost_money.setHint("Income Money");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initView() {
        helper = new DBHelper(new_cost.this);
        et_cost_title = findViewById(R.id.et_cost_title);
        et_cost_money = findViewById(R.id.et_cost_money);
        dp_cost_date = findViewById(R.id.dp_cost_date);
    }

    public void okButton(View view) {
        String titleStr = et_cost_title.getText().toString().trim();
        String moneyStr = et_cost_money.getText().toString().trim();
        String dateStr = dp_cost_date.getYear() + "-" + (dp_cost_date.getMonth() + 1) + "-"
                + dp_cost_date.getDayOfMonth();//这里getMonth会比当前月份少一个月，所以要+1
        int yearInt =  dp_cost_date.getYear();
        int monthInt = dp_cost_date.getMonth() + 1;
        int dayInt = dp_cost_date.getDayOfMonth();

        if ("".equals(moneyStr)) {//可以不填写Title但是不能不填金额
            Toast toast = Toast.makeText(this, "请填写金额", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("Title", titleStr);
            values.put("Money", moneyStr);
            values.put("Date", dateStr);
            values.put("Year", yearInt);
            values.put("Month", monthInt);
            values.put("Day", dayInt);
            values.put("Type", type);

            long account = db.insert("account", null, values);
            if (account > 0) {
                Toast toast = Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                setResult(1);
                finish();
            } else {
                Toast toast = Toast.makeText(this, "请重试", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                db.close();
            }
            setResult(1);
            finish();
        }
    }
}