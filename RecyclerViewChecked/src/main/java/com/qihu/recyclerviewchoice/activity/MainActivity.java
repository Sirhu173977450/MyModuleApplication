package com.qihu.recyclerviewchoice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qihu.recyclerviewchoice.R;

/**
 * Created by cui on 2017/3/4.
 */

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                startActivity(new Intent(MainActivity.this, SingleChoiceActivity.class));
                break;
            case R.id.btn2:
                startActivity(new Intent(MainActivity.this, MultipleChoiceActivity.class));
                break;
            case R.id.btn3:
                startActivity(new Intent(MainActivity.this, BgSingleChoiceActivity.class));
                break;
            case R.id.btn4:
                startActivity(new Intent(MainActivity.this, BgMultipleChoiceActivity.class));
                break;
            case R.id.btn5:
                startActivity(new Intent(MainActivity.this, SingleChoiceHeadFootActivity.class));
                break;
            case R.id.btn6:
                startActivity(new Intent(MainActivity.this, MultipleChoiceHeadFootActivity.class));
                break;
            case R.id.btn7:
//                startActivity(new Intent(MainActivity.this, MultipleChoiceActivity.class));
                break;
        }
    }
}
