package com.example.mobilesafe.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.domain.AppInfo;
import com.example.mobilesafe.engine.AppInfoProvider;
import com.example.mobilesafe.utils.DensityUtil;
import com.example.mobilesafe.utils.SystemInfoUtils;
import com.example.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;


public class AppManagerActivity extends Activity implements OnClickListener {
	private static final String TAG = "AppManagerActivity";
	private TextView tv_internal_size;
	private TextView tv_sd_size;
	/**
	 * 显示数据的listview
	 */
	private ListView lv_app;
	/**
	 * 正在加载的线性布局
	 */
	private LinearLayout ll_loading;
	/**
	 * 手机里面所有的安装的应用程序信息
	 */
	private List<AppInfo> appInfos;
	/**
	 * 用户的应用程序集合
	 */
	private List<AppInfo> userAppInfos;

	/**
	 * 系统的应用程序集合
	 */
	private List<AppInfo> systemAppInfos;
	private TextView tv_status;
	/**
	 * 代表的是程序信息的悬浮窗体 需求： 在Activity上只有一个悬浮窗体存在。
	 */
	private PopupWindow popupWindow;
	/**
	 * 卸载
	 */
	private LinearLayout ll_uninstall;
	/**
	 * 启动
	 */
	private LinearLayout ll_start;
	/**
	 * 分享
	 */
	private LinearLayout ll_share;
	/**
	 * 显示应用程序信息
	 */
	private LinearLayout ll_showinfo;

	/**
	 * 被点击的条目对象信息
	 */
	private AppInfo clickedAppInfo;

	private AppManagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanger);
		tv_internal_size = (TextView) findViewById(R.id.tv_internal_size);
		tv_sd_size = (TextView) findViewById(R.id.tv_sd_size);
		lv_app = (ListView) findViewById(R.id.lv_app);
		// 用来显示程序的类型
		tv_status = (TextView) findViewById(R.id.tv_status);
		// 给listview注册一个滚动的监听器
		lv_app.setOnScrollListener(new OnScrollListener() {
			// 当滚动的状态发生变化的时候调用的方法， 静止-->滚动 滚动-->静止
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// 一旦滚动就执行的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("系统程序：" + systemAppInfos.size());
					} else {
						tv_status.setText("用户程序：" + userAppInfos.size());
					}
				}
				if (popupWindow != null) {
					popupWindow.dismiss();
					popupWindow = null;
				}
			}
		});
		setAppInfoItemClickListener();
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		String internal_size = Formatter.formatFileSize(this,
				SystemInfoUtils.getInternalStorageFreeSize());
		String sd_size = Formatter.formatFileSize(this,
				SystemInfoUtils.getSDStorageFreeSize());
		tv_internal_size.setText("机身内存可用：" + internal_size);
		tv_sd_size.setText("SD卡可用：" + sd_size);
		// 显示正在加载的ui
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider
						.getAllAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isSystemApp()) {// 系统
						systemAppInfos.add(appInfo);
					} else {// 用户
						userAppInfos.add(appInfo);
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 隐藏正在加载的ui
						ll_loading.setVisibility(View.INVISIBLE);
						adapter = new AppManagerAdapter();
						lv_app.setAdapter(adapter);
					}
				});
			};
		}.start();
	}

	// 给界面上的listview的item注册一个点击事件
	private void setAppInfoItemClickListener() {
		lv_app.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				if (position == 0) {// 第0个位子 是一个textview的标签，显示有几个用户应用程序
					return;
				} else if (position == (userAppInfos.size() + 1)) {
					return;
				} else if (position <= userAppInfos.size()) {// 用户程序
					int newPosition = position - 1;// 减去用户的标签textview占据的位置
					clickedAppInfo = userAppInfos.get(newPosition);
				} else {// 系统程序
					int newPosition = position - 1 - userAppInfos.size() - 1;
					clickedAppInfo = systemAppInfos.get(newPosition);
				}
				View contentView = View.inflate(AppManagerActivity.this,
						R.layout.item_popup_appinfo, null);
				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);
				ll_showinfo = (LinearLayout) contentView
						.findViewById(R.id.ll_showinfo);
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_showinfo.setOnClickListener(AppManagerActivity.this);
				ll_start.setOnClickListener(AppManagerActivity.this);

				if (popupWindow != null) {// 检查屏幕上是否已经有了悬浮窗体，有的话就立刻关闭
					popupWindow.dismiss();
					popupWindow = null;
				}
				popupWindow = new PopupWindow(contentView, -2, -2);
				int[] location = new int[2];
				view.getLocationInWindow(location);
				// ☆☆☆☆☆设置popupwindow的背景,透明的 动画才可以播放。
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int x = 65;//单位dip
				int px = DensityUtil.dip2px(getApplicationContext(), x);
				Log.i(TAG,"px="+px);
				popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP,
						px, location[1]);
				// 指定缩放动画
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(250);
				contentView.startAnimation(sa);
			}
		});
	}

	private class AppManagerAdapter extends BaseAdapter {
		/**
		 * 返回listview里面有多少个条目
		 */
		@Override
		public int getCount() {
			// 为什么要加两个1 ， 增加了两个textview的标签。整个listview条目的个数增加了。
			return 1 + userAppInfos.size() + 1 + systemAppInfos.size();
		}

		/**
		 * 显示每个条目的view对象
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			if (position == 0) {// 第0个位子 是一个textview的标签，显示有几个用户应用程序
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("用户程序：" + userAppInfos.size());
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统程序：" + systemAppInfos.size());
				return tv;
			} else if (position <= userAppInfos.size()) {// 用户程序
				int newPosition = position - 1;// 减去用户的标签textview占据的位置
				appInfo = userAppInfos.get(newPosition);
			} else {// 系统程序
				int newPosition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newPosition);
			}

			// 把好看的xml布局转化成view对象返回回去
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {// 在复用历史缓存view对象的时候，不仅要检查是否为空
				// 还要检查类型是否可以被复用
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_appIcon = (ImageView) view
						.findViewById(R.id.iv_appicon);
				holder.tv_appName = (TextView) view
						.findViewById(R.id.tv_appName);
				holder.tv_apkSize = (TextView) view
						.findViewById(R.id.tv_apkSize);
				holder.iv_install_location = (ImageView) view
						.findViewById(R.id.iv_install_location);
				view.setTag(holder);
			}
			holder.iv_appIcon.setImageDrawable(appInfo.getAppIcon());
			holder.tv_appName.setText(appInfo.getAppName());
			holder.tv_apkSize.setText("程序大小："
					+ Formatter.formatFileSize(getApplicationContext(),
					appInfo.getAppSize()));
			if (appInfo.isInRom()) {
				holder.iv_install_location.setImageResource(R.drawable.memory);
			} else {
				holder.iv_install_location.setImageResource(R.drawable.sd);
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	/**
	 * 存放孩子对象的引用
	 */
	static class ViewHolder {
		ImageView iv_appIcon;
		TextView tv_appName;
		TextView tv_apkSize;
		ImageView iv_install_location;
	}

	/**
	 * 悬浮窗，条目的点击事件
	 *
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		if (popupWindow != null) {
			popupWindow.dismiss();
			popupWindow = null;
		}
		switch (v.getId()) {
			case R.id.ll_share:// 分享
				Log.i(TAG, "分享应用程序" + clickedAppInfo.getAppName());
				shareApplication();
				break;
			case R.id.ll_uninstall:// 卸载
				Log.i(TAG, "卸载应用程序" + clickedAppInfo.getAppName());
				uninstallApplication();
				break;
			case R.id.ll_showinfo:// 显示应用程序信息
				Log.i(TAG, "显示应用程序信息" + clickedAppInfo.getAppName());
				showApplicationInfo();
				break;
			case R.id.ll_start:// 开启
				Log.i(TAG, "开启应用程序" + clickedAppInfo.getAppName());
				startApplication();
				break;
		}
	}
	/**
	 * 显示应用程序详细信息
	 */
	private void showApplicationInfo() {
		Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:"+clickedAppInfo.getPackName()));
		startActivity(intent);
	}
	/**
	 * 分享应用程序
	 */
	private void shareApplication() {
//		 <action android:name="android.intent.action.SEND" />
//         <category android:name="android.intent.category.DEFAULT" />
//         <data android:mimeType="text/plain" />
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐你使用一款软件："+clickedAppInfo.getAppName()+"，真的很好用哦");
		startActivity(intent);
	}

	/**
	 * 开启一个应用程序
	 */
	private void startApplication() {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(clickedAppInfo.getPackName());
		if(intent!=null){
			startActivity(intent);
		}else{
			Toast.makeText(this, "对不起，该应用无法被开启", 0).show();
		}

	}
	/**
	 * 卸载应用程序
	 */
	private void uninstallApplication() {
//        <intent-filter>
//        <action android:name="android.intent.action.VIEW" />
//        <action android:name="android.intent.action.DELETE" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <data android:scheme="package" />
//    </intent-filter>
		//注册应用程序卸载的广播接受者
		AppUninstallReceiver receiver = new AppUninstallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(receiver, filter);
		Intent intent = new Intent();
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+clickedAppInfo.getPackName()));
		startActivity(intent);
		//根据应用程序是否被卸载掉，更新listview里面的数据。

	}

	/**
	 * 当Activity关闭调用的方法
	 */
	@Override
	protected void onDestroy() {
		if (popupWindow != null) {
			popupWindow.dismiss();
			popupWindow = null;
		}
		super.onDestroy();
	}

	private class AppUninstallReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String data = intent.getData().toString();
			String packname = data.replace("package:", "");
			unregisterReceiver(this);
			AppInfo deleteAppInfo = null;
			//更新ui界面
			for(AppInfo appinfo: userAppInfos){
				if(appinfo.getPackName().equals(packname)){
					deleteAppInfo = appinfo;
				}
			}
			if(deleteAppInfo!=null){
				userAppInfos.remove(deleteAppInfo);
			}
			adapter.notifyDataSetChanged();
		}
	}
}
