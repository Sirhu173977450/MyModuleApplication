package com.example.mobilesafe.activities;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mobilesafe.R;


public class Setup2Activity extends SetupBaseActivity {
	private RelativeLayout rl_setup2_bind;
	private ImageView iv_setup2_status;
	private TelephonyManager tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		rl_setup2_bind = (RelativeLayout) findViewById(R.id.rl_setup2_bind);
		iv_setup2_status = (ImageView) findViewById(R.id.iv_setup2_status);
		//获取系统里面电话管理的服务.
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//判断用户是否绑定过sim卡.
		String bindsim = sp.getString("sim", null);
		if(TextUtils.isEmpty(bindsim)){
			//如果sp里面没有信息 说明没有绑定
			iv_setup2_status.setImageResource(R.drawable.unlock);
		}else{
			//如果sp里面有信息,说明已经绑定
			iv_setup2_status.setImageResource(R.drawable.lock);
		}
		rl_setup2_bind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//判断sim卡是否被绑定
				String bindsim = sp.getString("sim", null);
				if(TextUtils.isEmpty(bindsim)){
					//没有绑定,需要绑定,把sim串号存入到sp
					String sim = tm.getSimSerialNumber();
					Editor editor = sp.edit();
					editor.putString("sim", sim);
					editor.commit();
					iv_setup2_status.setImageResource(R.drawable.lock);
				}else{
					//已经绑定,解除绑定,把sp里面存储的sim卡串号清空
					Editor editor = sp.edit();
					editor.putString("sim", null);
					editor.commit();
					iv_setup2_status.setImageResource(R.drawable.unlock);
				}
			}
		});
	}


	@Override
	public void next() {
		//判断用户是否绑定了sim卡串号
		String bindsim = sp.getString("sim", null);
		if(TextUtils.isEmpty(bindsim)){
			Toast.makeText(this, "手机防盗生效,必须先绑定sim卡",  Toast.LENGTH_SHORT).show();
			return;
		}
		openNewActivityAndFinish(Setup3Activity.class);
		// 修改Activity切换的动画效果
		overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
	}

	@Override
	public void pre() {
		openNewActivityAndFinish(Setup1Activity.class);
		overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
	}
}
