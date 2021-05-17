package com.example.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.example.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;


public class CallSmsSafeService extends Service {
	public static final String Tag = "CallSmsSafeService";
	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	private BlackNumberDao dao;
	private InnerSmsReceiver receiver;


	private class InnerSmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(Tag,"服务内部广播接受者接收到了短信");
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj: objs){
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String body = smsMessage.getMessageBody();
				if(body.contains("fapiao")){
					//你的头发票亮的很 分词技术
					Log.i(Tag,"发现发票垃圾短信,拦截");
					abortBroadcast();
					return;
				}
				String sender = smsMessage.getOriginatingAddress();
				String mode = dao.find(sender);
				if("2".equals(mode)||"3".equals(mode)){
					Log.i(Tag,"发现黑名单短信,拦截");
					abortBroadcast();
				}
			}
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(Tag,"骚扰拦截服务已经开启.");
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		dao = new BlackNumberDao(this);
		//注册一个电话状态的监听器.
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i(Tag,"骚扰拦截服务已经关闭.");
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

	/**
	 * 电话状态的监听器
	 */
	private class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE://空闲状态

					break;
				case TelephonyManager.CALL_STATE_RINGING://响铃状态
					String mode = dao.find(incomingNumber);
					//1.电话拦截 2 短信 3全部拦截.
					if("1".equals(mode)||"3".equals(mode)){
						Log.i(Tag,"挂断电话");
						//从1.5版本后,挂断电话的api被隐藏一起来了.
						endCall();//-->调用系统底层的服务方法挂断电话.
						//利用内容提供者清除呼叫记录
						deleteCallLog(incomingNumber);
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK://接通电话状态.

					break;
			}
		}
	}

	/**
	 * 挂断电话
	 */
	public void endCall() {
		//ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
		try {
			Class clazz = getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			iTelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除黑名单号码的呼叫记录
	 * @param incomingNumber 来电黑名单号码
	 */
	public void deleteCallLog(final String incomingNumber) {
		final ContentResolver resolver = getContentResolver();
		final Uri uri = Uri.parse("content://call_log/calls");
		//利用内容观察者 观察呼叫记录的数据库,如果生成了呼叫记录就立刻删除呼叫记录
		resolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				//当内容观察者观察到数据库的内容变化的时候调用的方法.
				super.onChange(selfChange);
				resolver.delete(uri, "number=?", new String[]{incomingNumber});
			}
		});
	}
}
