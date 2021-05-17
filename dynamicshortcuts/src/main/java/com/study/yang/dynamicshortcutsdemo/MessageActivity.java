package com.study.yang.dynamicshortcutsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        TextView tvMessage = (TextView) findViewById(R.id.tv_message);
        Intent intent = getIntent();
        String msg = intent.getStringExtra("message");
        tvMessage.setText(msg);
    }
}
