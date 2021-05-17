package com.example.mobilesafe.ui.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class KillAllReceiver extends BroadcastReceiver {

	private static final String TAG = "KillAllReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"自定义的广播消息收到了");
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		 List<RunningAppProcessInfo>  infos = am.getRunningAppProcesses();
		 for(RunningAppProcessInfo info:infos){
			 am.killBackgroundProcesses( info.processName);
		 }
		Toast.makeText(context, "进程清理完毕", Toast.LENGTH_SHORT).show();
	}
}
