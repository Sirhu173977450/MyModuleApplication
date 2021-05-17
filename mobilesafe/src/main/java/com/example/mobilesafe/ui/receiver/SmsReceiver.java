package com.example.mobilesafe.ui.receiver;


import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.mobilesafe.service.LocationService;
import com.example.mobilesafe.R;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for(Object obj:objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String body = smsMessage.getMessageBody();
			if("#*location*#".equals(body)){
				Log.i(TAG,"返回手机的位置..");
				//开启一个后台的服务,在服务里面获取手机的位置.
				Intent i = new Intent(context,LocationService.class);
				context.startService(i);
				abortBroadcast();
			}else if ("#*alarm*#".equals(body)){
				Log.i(TAG,"播放报警音乐..");
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.start();
				abortBroadcast();
			}else if ("#*wipedata*#".equals(body)){
				Log.i(TAG,"立刻清除数据..");
				//获取系统的设备管理员的管理器
				DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
				abortBroadcast();
			}else if ("#*lockscreen*#".equals(body)){
				Log.i(TAG,"立刻锁屏..");
				DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				dpm.resetPassword("123", 0);
				dpm.lockNow();
				abortBroadcast();
			}
		}
	}
}
