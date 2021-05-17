package com.example.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import com.example.mobilesafe.domain.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 业务方法，用于获取系统里面所有的应用程序信息
 */
public class AppInfoProvider {

	/**
	 * 获取系统所有的应用程序信息集合
	 * @param context 上下文
	 * @return
	 */
	public static List<AppInfo> getAllAppInfos(Context context){
		//PackageManager 包管理器，管理手机里面的应用程序信息。
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for(PackageInfo packInfo : packInfos){
			AppInfo appInfo = new AppInfo();
			String packName = packInfo.packageName;
			Drawable appIcon = packInfo.applicationInfo.loadIcon(pm);
			String appName = packInfo.applicationInfo.loadLabel(pm).toString();
			String apkPath = packInfo.applicationInfo.sourceDir;
			//应用程序的标记。flags，可以是很多标记的一个组合。
			int flags = packInfo.applicationInfo.flags;
			if((flags&ApplicationInfo.FLAG_SYSTEM)!=0){
				//系统应用
				appInfo.setSystemApp(true);
			}else{
				//用户应用
				appInfo.setSystemApp(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
				//安装在sd卡
				appInfo.setInRom(false);
			}else{
				//安装在手机内存
				appInfo.setInRom(true);
			}
			File file = new File(apkPath);
			long apkSize = file.length();
			appInfo.setAppIcon(appIcon);
			appInfo.setAppName(appName);
			appInfo.setAppSize(apkSize);
			appInfo.setPackName(packName);
			appInfos.add(appInfo);
		}
		SystemClock.sleep(1000);
		return appInfos;
	}
}
