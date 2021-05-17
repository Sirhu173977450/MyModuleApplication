package com.example.mobilesafe.activities;


import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.mobilesafe.R;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

public class TrafficManagerActivity extends Activity {
	private TextView tv_total_traffic;
	private TextView tv_mobile_traffic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic_manager);
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 获取要嵌入广告条的布局
		LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
		// 将广告条加入到布局中
		adLayout.addView(adView);
		tv_total_traffic = (TextView) findViewById(R.id.tv_total_traffic);
		tv_mobile_traffic = (TextView) findViewById(R.id.tv_moblie_traffic);
		//获取全部的translate的byte，获取全部上传的流量
		long totalTx = TrafficStats.getTotalTxBytes();
		//获取全部的receive的byte，获取全部的下载的流量
		long totalRx = TrafficStats.getTotalRxBytes();
		long total = totalRx+totalTx;
		tv_total_traffic.setText(Formatter.formatFileSize(this, total));
		//移动网络下载的总数据
		long moblieRx = TrafficStats.getMobileRxBytes();
		//移动网络上传的总数据
		long mobileTx = TrafficStats.getMobileTxBytes();
		long mobileTotal = mobileTx+moblieRx;
		tv_mobile_traffic.setText(Formatter.formatFileSize(this, mobileTotal));
		//根据uid获取应用程序的流量数据。
		long rx = TrafficStats.getUidRxBytes(10078);
		long tx = TrafficStats.getUidTxBytes(10078);
		System.out.println("下载："+Formatter.formatFileSize(this, rx));
		System.out.println("上传："+Formatter.formatFileSize(this, tx));
		//rcv 266239
		//snd 17372
	}
}
