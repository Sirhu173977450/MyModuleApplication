package com.example.mobilesafe.activities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobilesafe.R;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;


public class AntiVirusActivity extends Activity {
	protected static final int FOUND_VIRUS = 1;
	protected static final int NOT_VIRUS = 2;
	protected static final int SCAN_FINISH = 3;
	private ImageView iv_scan;
	private ProgressBar pb_scan_status;
	/**
	 * 扫描结果的容器
	 */
	private LinearLayout ll_container;
	private TextView tv_scan_status;

	private Handler hanlder = new Handler(){
		public void handleMessage(android.os.Message msg) {
			PackageInfo info;
			switch (msg.what) {
				case FOUND_VIRUS:
					info = (PackageInfo) msg.obj;
					TextView tv = new TextView(getApplicationContext());
					tv.setTextColor(Color.RED);
					tv.setText("发现病毒："+info.applicationInfo.loadLabel(getPackageManager()));
					ll_container.addView(tv, 0);
					break;
				case NOT_VIRUS:
					info = (PackageInfo) msg.obj;
					TextView tv2 = new TextView(getApplicationContext());
					tv2.setTextColor(Color.BLACK);
					tv2.setText("扫描安全："+info.applicationInfo.loadLabel(getPackageManager()));
					ll_container.addView(tv2,0);
					break;
				case SCAN_FINISH:
					iv_scan.clearAnimation();
					iv_scan.setVisibility(View.INVISIBLE);
					tv_scan_status.setText("扫描完毕");
					break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		pb_scan_status = (ProgressBar) findViewById(R.id.pb_scan_status);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(2000);
		ra.setRepeatCount(Animation.INFINITE);
		ra.setRepeatMode(Animation.RESTART);
		iv_scan.startAnimation(ra);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		scanVirus();

	}
	/**
	 * 查杀病毒的方法
	 */
	private void scanVirus() {
		new Thread(){
			public void run() {
				//遍历系统里面的每一个应用程序apk，获取他的特征码
				PackageManager pm = getPackageManager();
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb_scan_status.setMax(infos.size());
				int progress = 0;
				for(PackageInfo info:infos){
					String path = info.applicationInfo.sourceDir;
					String md5 = getFileMd5(path);
					//查询数据库里面是不是有这个记录，如果有就是病毒程序，如果没有，说明应用程序扫描安全
					SQLiteDatabase db = SQLiteDatabase.openDatabase(getFilesDir().getAbsolutePath()+"/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
					Cursor cursor = db.rawQuery("select desc from datable where md5=?", new String[]{md5});
					if(cursor.moveToNext()){
						String desc = cursor.getString(0);
						Message msg = Message.obtain();
						msg.what = FOUND_VIRUS;
						msg.obj = info;
						hanlder.sendMessage(msg);
					}else{
						Message msg = Message.obtain();
						msg.what = NOT_VIRUS;
						msg.obj = info;
						hanlder.sendMessage(msg);
					}
					progress++;
					SystemClock.sleep(50);
					pb_scan_status.setProgress(progress);
				}
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				hanlder.sendMessage(msg);

			};
		}.start();
	}
	/**
	 * 根据文件路径得到文件的md5算法生成的数字摘要
	 * @param path
	 * @return
	 */
	private String getFileMd5(String path){
		try {
			File file = new File(path);
			//得到一个数字摘要器
			MessageDigest digest = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = fis.read(buffer))!=-1){
				digest.update(buffer,0,len);
			}
			byte[] result = digest.digest();
			StringBuilder sb = new StringBuilder();
			for(byte b:result){
				int number = b&0xff;
				String str = Integer.toHexString(number);
				if(str.length()==1){
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}


}
