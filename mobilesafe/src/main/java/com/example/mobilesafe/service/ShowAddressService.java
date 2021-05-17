package com.example.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.example.mobilesafe.db.dao.AddressDBDao;
import com.example.mobilesafe.R;


public class ShowAddressService extends Service {
	protected static final String TAG = "ShowAddressService";
	private TelephonyManager tm;
	private MyPhoneListener listener;
	private OutCallReceiver receiver;
	private WindowManager wm;
	/**
	 * 显示在窗体上的view
	 */
	private View view;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// 获取窗体管理的服务
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
		// 注册一个电话状态监听的服务
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}

	private class MyPhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					String address = AddressDBDao.findLocation(incomingNumber);
					// Toast.makeText(ShowAddressService.this, address, 1).show();
					showMyToast(address);
					break;

				case TelephonyManager.CALL_STATE_IDLE:// 空闲状态
					if (view != null) {
						// 把窗体上的view给移除
						wm.removeView(view);
						view = null;
					}
					break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	private class OutCallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			String address = AddressDBDao.findLocation(number);
			// Toast.makeText(ShowAddressService.this, address, 1).show();
			showMyToast(address);
		}
	}
	private int[] bgIcons={R.drawable.call_locate_white,R.drawable.call_locate_orange,
			R.drawable.call_locate_blue,R.drawable.call_locate_gray,
			R.drawable.call_locate_green};
	private WindowManager.LayoutParams params;
	/**
	 * 显示自定义的土司
	 *
	 * @param text
	 *            归属地
	 */
	public void showMyToast(String text) {
		//自定义的view
		view = View.inflate(this, R.layout.toast_address, null);
		//给view对象设置一个触摸的监听器
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN://按下
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE://移动
						int newX = (int) event.getRawX();
						int newY = (int) event.getRawY();
						int dx = newX - startX;
						int dy = newY - startY;
						params.x+=dx;
						params.y+=dy;
						wm.updateViewLayout(view, params);
						Log.i(TAG,"手在屏幕上的偏移量dx:"+dx);
						Log.i(TAG,"手在屏幕上的偏移量dy:"+dy);
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_UP://离开屏幕
						break;
				}
				return true;
			}
		});
		SharedPreferences sp = getSharedPreferences("config", 0);
		int which = sp.getInt("which",0);
		view.setBackgroundResource(bgIcons[which]);
		TextView tv = (TextView) view.findViewById(R.id.tv_toast_address);
		tv.setText(text);
		params = new LayoutParams();
		params.gravity = Gravity.LEFT+Gravity.TOP;//坐标系为窗体的左上角
		params.x = 20;//水平方向的距离
		params.y = 20;//竖直方向的距离
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		//窗体类型,可以被触摸和点击的系统级别窗体,清单文件声明权限
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE; //2007
		wm.addView(view, params);
	}

}
