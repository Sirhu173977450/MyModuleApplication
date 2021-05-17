package com.example.mobilesafe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.example.mobilesafe.activities.EnterPasswordActivity;
import com.example.mobilesafe.db.dao.ApplockDao;

import java.util.List;

public class WatchDogService extends Service {
	public static final String TAG = "WatchDogService";
	private ActivityManager am;
	private boolean flag;
	private ApplockDao dao;
	private InnerReceiver receiver;
	/**
	 * 临时停止保护的包名
	 */
	private String tempStopProtectPackname;
	/**
	 * 所有的被锁定的包名
	 */
	private List<String> lockedPacknames;

	private MyObserver observer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		dao = new ApplockDao(this);
		receiver = new InnerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.itheima.mobilesafe.tempstopprotect");
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		registerReceiver(receiver, filter);
		// 获取的是数据库锁定的全部的包名
		lockedPacknames = dao.findAll();
		Uri uri = Uri.parse("content://com.itheima.mobilesafe.applockdb");
		// 注册内容观察者观察数据库内容的变化
		observer = new MyObserver(new Handler());
		getContentResolver().registerContentObserver(uri, true, observer);

		// 开启看门狗 监视系统运行的状态。
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		startWatchDog();

		super.onCreate();
	}

	private void startWatchDog() {
		new Thread() {
			public void run() {
				flag = true;
				List<RunningTaskInfo> infos;
				String packname;
				// 弹出来一个输入密码的界面容用户输入密码。
				Intent intent = new Intent(WatchDogService.this,
						EnterPasswordActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				while (flag) {
					// 获取系统里面正在运行的任务栈信息，最近运行的在最前面。这个集合是排序过的集合。
					infos = am.getRunningTasks(1);
					packname = infos.get(0).topActivity.getPackageName();
					// if(dao.find(packname)){//需要被保护//查询的是数据库，修改为查询内存
					if (lockedPacknames.contains(packname)) {// 查询内存
						// 检查是否需要临时停止保护
						if (packname.equals(tempStopProtectPackname)) {
							SystemClock.sleep(30);
							continue;
						}
						intent.putExtra("packname", packname);
						startActivity(intent);
					}
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		getContentResolver().unregisterContentObserver(observer);
		observer = null;
	}

	private class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("com.itheima.mobilesafe.tempstopprotect".equals(action)) {
				tempStopProtectPackname = intent.getStringExtra("packname");
			} else if (Intent.ACTION_SCREEN_ON.equals(action)) {
				Log.i(TAG, "屏幕激活了，开启看门狗子线程");
				if (flag == false) {
					startWatchDog();
				}
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				Log.i(TAG, "屏幕锁屏了，关闭看门狗子线程");
				flag = false;
			}
		}
	}

	private class MyObserver extends ContentObserver {

		public MyObserver(Handler handler) {
			super(handler);
		}

		// 内容观察者观察到数据变化调用的方法
		@Override
		public void onChange(boolean selfChange) {
			Log.i(TAG, "看门狗里面的内容观察者观察到了数据库变化。。。。");
			lockedPacknames = dao.findAll();
			super.onChange(selfChange);
		}
	}
}
