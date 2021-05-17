package com.example.mobilesafe.ui.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.mobilesafe.service.UpdateWidgetService;


public class MyWidget extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		System.out.println("widget被创建出来了。。");
		Intent i = new Intent(context,UpdateWidgetService.class);
		context.startService(i);
		super.onEnabled(context);
	}

	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		System.out.println("widget被销毁了");
		Intent i = new Intent(context,UpdateWidgetService.class);
		context.stopService(i);
	}
}
