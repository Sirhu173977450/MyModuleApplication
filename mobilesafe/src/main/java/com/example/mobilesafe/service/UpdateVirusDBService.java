package com.example.mobilesafe.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateVirusDBService extends Service {
	protected static final String TAG = "UpdateVirusDBService";
	private Timer timer ;
	private TimerTask task;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				try {
					URL url = new URL("http://192.168.1.101:8080/virusupdate.info");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					InputStream is = conn.getInputStream();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len = 0;
					while(( len = is.read(buffer))!=-1){
						baos.write(buffer, 0, len);
					}
					is.close();
					String json = baos.toString();
					JSONObject obj = new JSONObject(json);
					//服务器新的病毒数据库信息的版本
					int serverVersion = obj.getInt("version");
					SQLiteDatabase db = SQLiteDatabase.openDatabase(getFilesDir().getAbsolutePath()+"/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
					Cursor cursor = db.rawQuery("select subcnt from version", null);
					cursor.moveToNext();
					int currentVersion = cursor.getInt(0);
					if(serverVersion>currentVersion){
						Log.i(TAG,"服务器有新的病毒数据库版本，立刻更新");
						db.beginTransaction();//开启数据库事务，放在更新不同步
						try {
							String md5 = obj.getString("md5");
							String desc = obj.getString("desc");
							String name = obj.getString("name");
							String type = obj.getString("type");
							ContentValues values = new ContentValues();
							values.put("md5", md5);
							values.put("name", name);
							values.put("type", type);
							values.put("desc", desc);
							db.insert("datable", null, values);
							//更新本地数据库的版本号
							ContentValues v2 = new ContentValues();
							v2.put("subcnt", serverVersion);
							db.update("version", v2, null, null);
							db.setTransactionSuccessful();
						} finally {
							db.endTransaction();
							db.close();
						}
					}else{
						Log.i(TAG,"本地病毒数据库版本是最新的，无需更新");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		timer.schedule(task, 5000, 10*1000l);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
