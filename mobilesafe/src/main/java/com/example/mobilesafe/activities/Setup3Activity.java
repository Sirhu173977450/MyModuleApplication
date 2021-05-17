package com.example.mobilesafe.activities;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilesafe.R;


public class Setup3Activity extends SetupBaseActivity {
	private EditText et_setup3_phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_setup3_phone = (EditText) findViewById(R.id.et_setup3_phone);
		et_setup3_phone.setText(sp.getString("safenumber", ""));
	}

	@Override
	public void next() {
		String phone = et_setup3_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "安全号码必须设置", Toast.LENGTH_SHORT).show();
			return;
		}
		Editor editor = sp.edit();
		editor.putString("safenumber", phone);
		editor.commit();
		openNewActivityAndFinish(Setup4Activity.class);
		//修改Activity切换的动画效果
		overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
	}

	@Override
	public void pre() {
		openNewActivityAndFinish(Setup2Activity.class);
		overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);

	}
	/**
	 * 开启新的界面,展现出来联系人,获取联系人的号码
	 * @param view
	 */
	public void selectContacts(View view){
		Intent intent = new Intent(this,SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			String phone = data.getStringExtra("phone");
			et_setup3_phone.setText(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
