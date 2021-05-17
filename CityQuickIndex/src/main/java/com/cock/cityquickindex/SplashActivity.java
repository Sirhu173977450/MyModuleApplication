package com.cock.cityquickindex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by lhl on 2016/10/31.
 */

public class SplashActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    private static final int RESOULT_CODE = 100;
    private TextView text;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        text = (TextView) findViewById(R.id.tv_text);
    }

    public void jump(View view) {
        startActivityForResult(new Intent(SplashActivity.this, MainActivity.class), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        System.out.println(requestCode + ">>>>>" + resultCode);
        String cityName = data.getStringExtra("cityName");
        text.setText(cityName);
    }
}
