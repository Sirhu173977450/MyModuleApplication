package com.example.mobilesafe.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.mobilesafe.R;
import com.example.mobilesafe.db.dao.BlackNumberDao;

public class AddBlackNumberActivity extends Activity {
	private EditText et_blacknumber;
	private RadioGroup rg_mode;
	private BlackNumberDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_blacknumber);
		et_blacknumber = (EditText) findViewById(R.id.et_blacknumber);
		rg_mode = (RadioGroup) findViewById(R.id.rg_mode);
		dao = new BlackNumberDao(this);
	}

	public void save(View view) {
		String blacknumber = et_blacknumber.getText().toString().trim();
		if (TextUtils.isEmpty(blacknumber)) {
			Toast.makeText(this, "黑名单号码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		//判断黑名单号码是否已经存在.
		String numbermode = dao.find(blacknumber);
		if(!TextUtils.isEmpty(numbermode)){
			Toast.makeText(this, "黑名单号码已经存在", Toast.LENGTH_SHORT).show();
			return;
		}
		int id = rg_mode.getCheckedRadioButtonId();
		String mode = "1";
		switch (id) {
			case R.id.rb_all:
				mode = "3";
				break;
			case R.id.rb_sms:
				mode = "2";
				break;
			case R.id.rb_phone:
				mode = "1";
				break;
		}
		boolean result = dao.add(blacknumber, mode);
		Intent data = new Intent();
		if(result){
			//添加成功
			data.putExtra("flag", true);
			data.putExtra("phone", blacknumber);
			data.putExtra("mode", mode);
			setResult(0, data);
		}else{
			//添加失败
			data.putExtra("flag", false);
			setResult(0, data);
		}
		finish();

	}

	public void cancel(View view) {
		finish();
	}
}
