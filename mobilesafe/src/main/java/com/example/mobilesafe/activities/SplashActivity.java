package com.example.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.utils.PackageInfoUtils;
import com.example.mobilesafe.utils.StreamTools;
import com.example.mobilesafe.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
	public static final String TAG = "SplashActivity";
	public static final int SHOW_UPDATE_DIALOG = 1;
	public static final int ERROR = 2;
	private TextView tv_splash_version;
	private ProgressDialog pd;
	/**
	 * 新版本apk的下载路径
	 */
	private String downloadpath;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case SHOW_UPDATE_DIALOG:// 显示应用更新对话框
					String desc = (String) msg.obj;
					showUpdateDialog(desc);
					break;
				case ERROR:
					Toast.makeText(SplashActivity.this, "错误码-" + msg.obj,  Toast.LENGTH_SHORT).show();
					loadMainUI();
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		String verison = PackageInfoUtils.getPackageVersion(this);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本:" + verison);
		// 检查sp里面的状态,看自动更新是否开启
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean update = sp.getBoolean("update", true);
		if (update) {
			// 开启子线程获取服务器的版本信息
//			new Thread(new CheckVersionTask()).start();

			SystemClock.sleep(1500);
			loadMainUI();
		} else {
			new Thread() {
				public void run() {
					SystemClock.sleep(1500);
					loadMainUI();
				};
			}.start();
		}
		//拷贝归属地数据库
		copyDB("address.db");
		//拷贝查杀病毒数据库
		copyDB("antivirus.db");

//		Intent intent = new Intent(this,UpdateVirusDBService.class);
//		startService(intent);
	}

	/**
	 * 拷贝数据库
	 */
	private void copyDB(final String dbname) {
		File file = new File(getFilesDir(), dbname);
		if (file.exists() && file.length() > 0) {
			Log.i(TAG, "数据库存在,无需拷贝");
		} else {
			new Thread() {
				public void run() {
					// 把asset资产目录里面的数据库文件(在apk里面的)拷贝到手机系统里面
					try {
						InputStream is = getAssets().open(dbname);
						File file = new File(getFilesDir(), dbname);
						FileOutputStream fos = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
						int len = -1;
						while ((len = is.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}
						fos.close();
						is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	/**
	 * 显示自动更新的对话框
	 *
	 * @param desc
	 *            描述
	 */
	protected void showUpdateDialog(String desc) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setCancelable(false);
		builder.setTitle("升级提醒");
		builder.setMessage(desc);
		builder.setPositiveButton("立刻升级", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pd = new ProgressDialog(SplashActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				HttpUtils http = new HttpUtils();
				File sdDir = Environment.getExternalStorageDirectory();
				File file = new File(sdDir, SystemClock.uptimeMillis() + ".apk");
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					http.download(downloadpath, file.getAbsolutePath(),
							new RequestCallBack<File>() {

								@Override
								public void onFailure(HttpException arg0,
													  String arg1) {
									Toast.makeText(SplashActivity.this, "下载失败",
											0).show();
									loadMainUI();
									pd.dismiss();
								}

								@Override
								public void onLoading(long total, long current,
													  boolean isUploading) {
									pd.setMax((int) total);
									pd.setProgress((int) current);
									super.onLoading(total, current, isUploading);

								}

								@Override
								public void onSuccess(
										ResponseInfo<File> fileinfo) {
									pd.dismiss();
									Toast.makeText(SplashActivity.this, "下载成功",
											0).show();
									// 覆盖安装apk文件
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(
											Uri.fromFile(fileinfo.result),
											"application/vnd.android.package-archive");
									startActivity(intent);
								}

							});
				} else {
					// 进入应用程序主界面.
					Toast.makeText(SplashActivity.this, "sd卡不可用,无法自动更新", 0)
							.show();
					loadMainUI();
				}
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadMainUI();
			}
		});
		builder.show();
	}

	private void loadMainUI() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();// 关闭自己,从任务栈退出
	}

	/**
	 * 获取服务器配置的最新版本号
	 */
	private class CheckVersionTask implements Runnable {
		@Override
		public void run() {
			Message msg = Message.obtain();
			long startTime = System.currentTimeMillis();
			try {
				String path = getResources().getString(R.string.url);
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(2000);
				int code = conn.getResponseCode();
				if (code == 200) {
					InputStream is = conn.getInputStream();
					String result = StreamTools.readStream(is);
					JSONObject json = new JSONObject(result);
					String serverVersion = json.getString("version");
					String description = json.getString("description");
					downloadpath = json.getString("downloadpath");
					Log.i(TAG, "新版本的下载路径:" + downloadpath);
					Log.i(TAG, "服务器版本:" + serverVersion);
					String localVersion = PackageInfoUtils
							.getPackageVersion(SplashActivity.this);
					if (localVersion.equals(serverVersion)) {
						Log.i(TAG, "版本号一致,无需升级,进入程序主界面");
						SystemClock.sleep(1500);
						loadMainUI();
					} else {
						Log.i(TAG, "版本号不一致,提示用户升级.");
						msg.what = SHOW_UPDATE_DIALOG;
						msg.obj = description;
					}
				} else {
					msg.what = ERROR;
					msg.obj = "code:410";
				}
			} catch (NotFoundException e) {
				msg.what = ERROR;
				msg.obj = "code:404";
				handler.sendMessage(msg);
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				msg.what = ERROR;
				msg.obj = "code:405";
			} catch (IOException e) {
				e.printStackTrace();
				msg.what = ERROR;
				msg.obj = "code:408";
			} catch (JSONException e) {
				e.printStackTrace();
				msg.what = ERROR;
				msg.obj = "code:409";
			} finally {
				// 计算代码走到这花费的时间
				long endTime = System.currentTimeMillis();
				long dTime = endTime - startTime;
				if (dTime > 2000) {

				} else {
					SystemClock.sleep(2000 - dTime);
				}
				handler.sendMessage(msg);
			}

		}
	}
}
