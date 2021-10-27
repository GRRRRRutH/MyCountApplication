package com.example.mycountapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class AlterCostActivity extends AppCompatActivity {
    private DBHelper helper;
    private EditText et_cost_title;
    private EditText et_cost_money;
    private DatePicker dp_cost_date;

    private String id;
    private String costTitle;
    private String costMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_cost);
        initView();

        //获取传输的数据
        Intent intent=getIntent();
        costTitle = intent.getStringExtra("costTitle");
        costMoney = intent.getStringExtra("costMoney");
        id = intent.getStringExtra("id");
        //修改显示的数据
        et_cost_title.setText(costTitle);
        et_cost_money.setText(costMoney);
    }

    private void initView() {
        helper = new DBHelper(AlterCostActivity.this);
        et_cost_title = findViewById(R.id.et_cost_title);
        et_cost_money = findViewById(R.id.et_cost_money);
        dp_cost_date = findViewById(R.id.dp_cost_date);
    }

    //修改数据库
    public void changeButton(View view) {
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

            long account = db.update("account", values, "_id = ?", new String[] {id});

            if (account > 0) {
                Toast toast = Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                setResult(2);
                finish();
            } else {
                Toast toast = Toast.makeText(this, "请重试", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                db.close();
            }
            setResult(2);
            finish();
        }
    }

    //删除数据
    public void deleteButton(View view) {
        SQLiteDatabase db = helper.getReadableDatabase();

        long account = db.delete("account", "_id = ?", new String[] {id});

        if (account > 0) {
            Toast toast = Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            setResult(2);
            finish();
        } else {
            Toast toast = Toast.makeText(this, "请重试", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            db.close();
        }
        setResult(2);
        finish();
    }
}