package com.example.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 进程信息的数据实体
 */
public class ProcessInfo {

	/**
	 * checkbox的状态
	 */
	private boolean checked;
	/**
	 * 进程图标，应用程序图标
	 */
	private Drawable appIcon;
	/**
	 * 应用程序名称
	 */
	private String appName;
	/**
	 * 应用程序包名
	 */
	private String packName;
	/**
	 * 内存占用的大小
	 */
	private long memSize;
	/**
	 * 是否是用户进程
	 */
	private boolean userProcess;
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
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isUserProcess() {
		return userProcess;
	}
	public void setUserProcess(boolean userProcess) {
		this.userProcess = userProcess;
	}
	@Override
	public String toString() {
		return "ProcessInfo [appName=" + appName + ", packName=" + packName
				+ ", memSize=" + memSize + ", userProcess=" + userProcess + "]";
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
