package com.example.mobilesafe.fragment;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mobilesafe.R;

import java.lang.reflect.Method;
import java.util.List;


public class CleanCacheFragment extends Fragment {
	protected static final int SCANING = 1;
	protected static final int SCAN_FINISH = 2;
	protected static final int FOUND_CACHE = 3;
	private ProgressBar pb;
	private TextView tv_scan_status;
	private LinearLayout ll_container;
	private PackageManager pm;
	private Button bt_clean_all;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case SCANING:
					String name = (String) msg.obj;
					tv_scan_status.setText("正在扫描：" + name);
					break;

				case SCAN_FINISH:
					tv_scan_status.setText("扫描完毕");
					break;
				case FOUND_CACHE:
					final CacheInfo info = (CacheInfo) msg.obj;
					View view = View.inflate(getActivity(),
							R.layout.item_cache_info, null);
					view.setBackgroundResource(R.drawable.dg_cancel_selector);
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							//pm.deleteApplicationCacheFiles(info.packName,	new ClearCacheObserver());
//						Method[]  methods = PackageManager.class.getMethods();
//						for(Method method:methods){
//							if("deleteApplicationCacheFiles".equals(method.getName())){
//								try {
//									method.invoke(pm, info.packName,new ClearCacheObserver());
//									Toast.makeText(getActivity(), "清理成功", 0).show();
//								} catch (Exception e) {
//									e.printStackTrace();
//									Toast.makeText(getActivity(), "清理失败", 0).show();
//								}
//								return;
//							}
//						}
							Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							intent.setData(Uri.parse("package:"+info.packName));
							startActivity(intent);
						}
					});
					TextView tv_cache_size = (TextView) view
							.findViewById(R.id.tv_cache_size);
					ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
					TextView tv_appname = (TextView) view
							.findViewById(R.id.tv_appname);
					tv_appname.setText(info.appName);
					tv_cache_size.setText(Formatter.formatFileSize(getActivity(),
							info.cacheSize));
					iv_icon.setImageDrawable(info.appIcon);
					ll_container.addView(view);
					break;
			}
		};
	};

	// 创建fragment显示内容的方法
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_clean_cache,
				null);
		pb = (ProgressBar) view.findViewById(R.id.pb);
		tv_scan_status = (TextView) view.findViewById(R.id.tv_scan_status);
		ll_container = (LinearLayout) view.findViewById(R.id.ll_container);
		bt_clean_all = (Button) view.findViewById(R.id.bt_clean_all);
		bt_clean_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//模拟一个超级大存储空间的请求。
				//freeStorageAndNotify
				Method[] methods = PackageManager.class.getDeclaredMethods();
				for(Method method:methods){
					if("freeStorageAndNotify".equals(method.getName())){
						try {
							method.invoke(pm, Integer.MAX_VALUE,new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {

								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}
				}
				Toast.makeText(getActivity(), "清理完毕", Toast.LENGTH_SHORT).show();
				tv_scan_status.setText("系统已经优化完毕，没有缓存文件存在了");
				ll_container.removeAllViews();
			}
		});
		pm = getActivity().getPackageManager();
		return view;
	}

	// 界面可见调用的方法
	@Override
	public void onStart() {
		super.onStart();
		ll_container.removeAllViews();
		new Thread() {
			public void run() {
				List<PackageInfo> packInfos = pm.getInstalledPackages(0);
				pb.setMax(packInfos.size());
				int progress = 0;
				for (PackageInfo packInfo : packInfos) {
					String packname = packInfo.packageName;
					getPackSizeInfo(packname);
					SystemClock.sleep(50);
					progress++;
					pb.setProgress(progress);
					String name = packInfo.applicationInfo.loadLabel(pm)
							.toString();
					Message msg = Message.obtain();
					msg.obj = name;
					msg.what = SCANING;
					handler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	// 利用反射获取包的大小信息
	private void getPackSizeInfo(final String packname) {
		// 根据包名利用反射获取缓存信息
		Method[] methods = PackageManager.class.getDeclaredMethods();
		for (Method method : methods) {
			if ("getPackageSizeInfo".equals(method.getName())) {
				try {
					method.invoke(pm, packname,
							new IPackageStatsObserver.Stub() {
								@Override
								public void onGetStatsCompleted(
										PackageStats pStats, boolean succeeded)
										throws RemoteException {
									long cacheSize = pStats.cacheSize;
									if (cacheSize > 0) {
										CacheInfo info = new CacheInfo();
										info.packName = packname;
										try {
											info.appName = pm
													.getApplicationInfo(
															packname, 0)
													.loadLabel(pm).toString();
											info.appIcon = pm
													.getApplicationInfo(
															packname, 0)
													.loadIcon(pm);
										} catch (NameNotFoundException e) {
											e.printStackTrace();
										}
										info.cacheSize = cacheSize;
										Message msg = Message.obtain();
										msg.what = FOUND_CACHE;
										msg.obj = info;
										handler.sendMessage(msg);
									}
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

	class CacheInfo {
		String packName;
		String appName;
		long cacheSize;
		Drawable appIcon;
	}

	class ClearCacheObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(final String packageName,
									  final boolean succeeded) {
			System.out.println(packageName + "---" + succeeded);
		}
	}


}
