package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final static int REQUESTCODE = 1; // 返回的结果码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jumpActivity(View view) {

        // 意图实现activity的跳转
        Intent intent = new Intent(MainActivity.this,
                MainActivity2.class);
        intent.putExtra("a", "1");
        intent.putExtra("b", "2");
        // 这种启动方式：startActivity(intent);并不能返回结果
        startActivityForResult(intent, REQUESTCODE); //REQUESTCODE--->1
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == 2) {
            if (requestCode == REQUESTCODE) {
                String three = data.getStringExtra("three");
                //设置结果显示框的显示数值
                Toast.makeText(this,""+three,Toast.LENGTH_SHORT).show();

            }
        }
    }
}
