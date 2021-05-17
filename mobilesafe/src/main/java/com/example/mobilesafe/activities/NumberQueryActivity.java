package com.example.mobilesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.db.dao.AddressDBDao;
import com.example.mobilesafe.R;


public class NumberQueryActivity extends Activity {
	private EditText et_number;
	private TextView tv_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_query);
		tv_location = (TextView) findViewById(R.id.tv_location);
		et_number = (EditText) findViewById(R.id.et_number);
		// 给文本输入框注册一个内容变化的监听器.
		et_number.addTextChangedListener(new TextWatcher() {
			// 当文本变化的时候调用的方法
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				if (s.length() >= 10) {
					String location = AddressDBDao.findLocation(s.toString());
					tv_location.setText("归属地为:" + location);
				}
			}

			// 当文本变化之前调用的方法
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			// 当文本变化之后调用的方法
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	public void query(View view) {
		String number = et_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "号码不能为空", 0).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_number.startAnimation(shake);
			return;
		}
		String location = AddressDBDao.findLocation(number);
		tv_location.setText("归属地为:" + location);
	}
}
