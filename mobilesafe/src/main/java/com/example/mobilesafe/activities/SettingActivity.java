package com.example.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.example.mobilesafe.service.CallSmsSafeService;
import com.example.mobilesafe.service.ShowAddressService;
import com.example.mobilesafe.service.WatchDogService;
import com.example.mobilesafe.ui.SwitchImageView;
import com.example.mobilesafe.utils.ServiceStatusUtils;
import com.example.mobilesafe.R;


public class SettingActivity extends Activity {
	//共享参数
	private SharedPreferences sp;
	//自动更新的控件声明
	private SwitchImageView siv_setting_update;
	private RelativeLayout rl_setting_update;
	//骚扰拦截的控件声明
	private SwitchImageView siv_callsmssafe;
	private RelativeLayout rl_setting_callsmssafe;

	//归属地显示控件声明
	private SwitchImageView siv_showlocation;
	private RelativeLayout rl_setting_showlocation;

	//归属地风格修改
	private RelativeLayout rl_setting_changestyle;
	private String[] bgNames = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};

	//程序锁控件
	private SwitchImageView siv_applock;

	String s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		//初始化sp
		sp = getSharedPreferences("config", MODE_PRIVATE);
		siv_applock = (SwitchImageView) findViewById(R.id.siv_applock);
		//初始化自动更新设置
		siv_setting_update = (SwitchImageView) findViewById(R.id.siv_setting_update);
		siv_setting_update.setSwitchStatus(sp.getBoolean("update", true));
		rl_setting_update = (RelativeLayout) findViewById(R.id.rl_setting_update);
		rl_setting_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				siv_setting_update.changeSwitchStatus();
				Editor editor = sp.edit();
				editor.putBoolean("update", siv_setting_update.getSwitchStatus());
				editor.commit();
				//保存开关的状态到sp
			}
		});
		//初始化骚扰拦截的设置
		siv_callsmssafe = (SwitchImageView) findViewById(R.id.siv_callsmssafe);
		rl_setting_callsmssafe = (RelativeLayout) findViewById(R.id.rl_setting_callsmssafe);
		final Intent intent = new Intent(SettingActivity.this,CallSmsSafeService.class);
		//获取当前服务运行的状态,根据状态去修改界面显示的内容.
		boolean status = ServiceStatusUtils.isServiceRunning(this, "com.itheima.mobilesafe.service.CallSmsSafeService");
		siv_callsmssafe.setSwitchStatus(status);
		rl_setting_callsmssafe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				siv_callsmssafe.changeSwitchStatus();
				boolean status = siv_callsmssafe.getSwitchStatus();
				if(status){
					startService(intent);
				}else{
					stopService(intent);
				}
			}
		});
		//归属地显示设置
		siv_showlocation = (SwitchImageView) findViewById(R.id.siv_showlocation);
		rl_setting_showlocation = (RelativeLayout) findViewById(R.id.rl_setting_showlocation);
		final Intent showAddressIntent = new Intent(this, ShowAddressService.class);
		boolean showAddressStatus = ServiceStatusUtils.isServiceRunning(this, "com.itheima.mobilesafe.service.ShowAddressService");
		siv_showlocation.setSwitchStatus(showAddressStatus);
		rl_setting_showlocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				siv_showlocation.changeSwitchStatus();
				boolean status = siv_showlocation.getSwitchStatus();
				if(status){
					startService(showAddressIntent);
				}else{
					stopService(showAddressIntent);
				}
			}
		});

		//修改归属地显示风格
		rl_setting_changestyle=(RelativeLayout) findViewById(R.id.rl_setting_changestyle);
		rl_setting_changestyle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//弹出来修改背景的对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setIcon(R.drawable.dialog_title_default_icon);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(bgNames, sp.getInt("which", 0), new  DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						s.equals("haha");
					}
				});
				builder.show();
			}
		});
		//修改看门狗开关的显示状态
		boolean applockStatus = ServiceStatusUtils.isServiceRunning(this, "com.itheima.mobilesafe.service.WatchDogService");
		siv_applock.setSwitchStatus(applockStatus);
	}


	public void changeApplockStatus(View view){
		Intent intent = new Intent(this,WatchDogService.class);
		if(ServiceStatusUtils.isServiceRunning(this, "com.itheima.mobilesafe.service.WatchDogService")){
			//关闭服务
			stopService(intent);
			siv_applock.setSwitchStatus(false);
		}else{
			//开启服务
			startService(intent);
			siv_applock.setSwitchStatus(true);
		}
	}
}
