package com.example.mobilesafe.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompleteReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"手机启动完毕了.");
		//判断用户是否开启了手机防盗的功能.
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			Log.i(TAG,"防盗保护已经开启,检测sim卡是否一致.");
			//用户绑定的sim串号
			String savedSim = sp.getString("sim", "");
			//获取当前手机里面的sim串号
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String currentSim = tm.getSimSerialNumber()+"afa";
			if(savedSim.equals(currentSim)){
				Log.i(TAG,"sim卡一致,还是您的手机");
			}else{
				Log.i(TAG,"sim卡不一致,手机可能被盗,发送报警短信");
				String safenumber = sp.getString("safenumber", "");
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(safenumber, null, "SOS...phone may lost", null, null);
			}
		}
	}

}
