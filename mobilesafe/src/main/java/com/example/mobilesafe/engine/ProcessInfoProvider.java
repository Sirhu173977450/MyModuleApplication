package com.example.mobilesafe.engine;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import com.example.mobilesafe.domain.ProcessInfo;
import com.example.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 进程信息的业务类
 */
public class ProcessInfoProvider {

	/**
	 * 获取所有的正在运行的进程信息
	 *
	 * @param context
	 *            上下文
	 * @return
	 */
	public static List<ProcessInfo> getRunningProcessInfos(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		List<ProcessInfo>  processInfos = new ArrayList<ProcessInfo>();
		for (RunningAppProcessInfo info : infos) {
			ProcessInfo processInfo = new ProcessInfo();
			// 进程名实际上就是应用程序的包名
			String packName = info.processName;
			processInfo.setPackName(packName);
			long memSize = am.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty()*1024;
			processInfo.setMemSize(memSize);
			try {
				PackageInfo packInfo = pm.getPackageInfo(packName, 0);
				Drawable appIcon = packInfo.applicationInfo.loadIcon(pm);
				processInfo.setAppIcon(appIcon);
				String appName = packInfo.applicationInfo.loadLabel(pm).toString();
				processInfo.setAppName(appName);
				if((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)!=0){
					//系统进程
					processInfo.setUserProcess(false);
				}else{
					//用户进程
					processInfo.setUserProcess(true);
				}
			} catch (NameNotFoundException e) {
				processInfo.setAppName(packName);
				processInfo.setAppIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
			}
			processInfos.add(processInfo);
		}
		return processInfos;
	}
}
