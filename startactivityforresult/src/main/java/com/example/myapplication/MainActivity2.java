package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        String a = getIntent().getStringExtra("a");
        String b = getIntent().getStringExtra("b");
        Toast.makeText(this,"接收到传递数据："+a+"__________"+b,Toast.LENGTH_SHORT).show();
    }

    public void backresult(View view) {
        Intent intent = new Intent();
        intent.putExtra("three", "finish"); //将计算的值回传回去
        //通过intent对象返回结果，必须要调用一个setResult方法，
        //setResult(resultCode, data);第一个参数表示结果返回码，一般只要大于1就可以，但是
        setResult(2, intent);
        finish(); //结束当前的activity的生命周期
    }
}
