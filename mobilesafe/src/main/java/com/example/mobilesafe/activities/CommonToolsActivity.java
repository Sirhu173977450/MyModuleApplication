package com.example.mobilesafe.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mobilesafe.utils.SmsTools;
import com.example.mobilesafe.R;


public class CommonToolsActivity extends Activity {
	private static final String TAG = "CommonToolsActivity";
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_tools);
	}
	/**
	 * 进入号码归属地查询
	 * @param view
	 */
	public void enterNumberQueryActivity(View view){
		Intent intent = new Intent(this, NumberQueryActivity.class);
		startActivity(intent);
	}
	/**
	 * 短信的还原
	 * @param view
	 */
	public void smsRestore(View view){
		Log.i(TAG,"短信还原");
	}
	/**
	 * 短信的备份
	 * <xml头>
	 * <infos>
	 * 	<info>
	 * 		<address>5556</address>
	 * 		<body>xx</body>
	 * 		<date></date>
	 * 		<type></type>
	 * 	</info>
	 * </infos>
	 * @param view
	 * 问君能有几多愁，恰是修完bug改需求。
	 */
	public void smsBackup(View view){
		Log.i(TAG,"短信备份");
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){
			public void run() {
				SmsTools.backUpSms(CommonToolsActivity.this, new SmsTools.BackupCallback() {
					@Override
					public void onSmsBackup(int progress) {
						pd.setProgress(progress);
					}

					@Override
					public void beforeSmsBackup(int max) {
						pd.setMax(max);
					}
				});
				pd.dismiss();
			};
		}.start();
	}
	/**
	 * 进入程序锁功能
	 * @param view
	 */
	public void enterAppLock(View view){
		Intent intent = new Intent(this,ApplockActivity.class);
		startActivity(intent);
	}
}
