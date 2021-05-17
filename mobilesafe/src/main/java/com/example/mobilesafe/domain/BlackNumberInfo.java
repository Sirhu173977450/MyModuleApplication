package com.example.mobilesafe.domain;
/**
 * 黑名单号码的业务bean
 */
public class BlackNumberInfo {
	private String id;
	private String phone;
	private String mode;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
}
