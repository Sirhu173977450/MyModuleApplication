package com.example.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 应用程序的业务bean
 * 用来保存应用程序的信息
 */
public class AppInfo {
	/**
	 * 图标
	 */
	private Drawable appIcon;
	/**
	 * 名称
	 */
	private String appName;
	/**
	 * 包名
	 */
	private String packName;
	/**
	 * 大小
	 */
	private long appSize;
	/**
	 * 是否安装在手机的内部存储空间
	 */
	private boolean inRom;
	/**
	 * 是否是系统应用
	 */
	private boolean systemApp;

	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public boolean isSystemApp() {
		return systemApp;
	}
	public void setSystemApp(boolean systemApp) {
		this.systemApp = systemApp;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public long getAppSize() {
		return appSize;
	}
	public void setAppSize(long appSize) {
		this.appSize = appSize;
	}
	@Override
	public String toString() {
		return "AppInfo [appName=" + appName + ", packName=" + packName
				+ ", appSize=" + appSize + "]";
	}

}
