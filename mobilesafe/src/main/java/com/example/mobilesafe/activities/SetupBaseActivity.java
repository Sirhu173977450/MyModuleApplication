package com.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class SetupBaseActivity extends Activity {
	protected static final String TAG = "SetupBaseActivity";
	// 1.定义一个手势识别器
	private GestureDetector mGestureDetector;
	protected SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 2.初始化手势识别器
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					// 当用户手指在屏幕上滑动的时候调用的方法
					// e1 手指第一次触摸到屏幕的事件
					// e2 手指离开屏幕一瞬间对应的事件
					// velocityX 水平方向的速度
					// velocityY 垂直方向的速度 单位像素/s
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
										   float velocityX, float velocityY) {
						if (Math.abs(velocityX) < 200) {
							Log.i(TAG, "移动的太慢,无效动作");
							return true;
						}
						if (Math.abs(e2.getRawY() - e1.getRawY()) > 50) {
							Log.i(TAG, "垂直方向移动过大,无效动作");
							return true;
						}
						if ((e1.getRawX() - e2.getRawX()) > 200) {
							Log.i(TAG, "向左滑动,显示下一个界面");
							next();
							return true;
						}
						if ((e2.getRawX() - e1.getRawX()) > 200) {
							Log.i(TAG, "向右滑动,显示上一个界面");
							pre();
							return true;
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});
	}
	/**
	 * 当用户手指在屏幕上触摸的时候调用的方法
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 让手势识别器识别传入进来的事件
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	/**
	 * 显示下一个
	 */
	public abstract void next();
	/**
	 * 显示上一个
	 */
	public abstract void pre();


	public void showNext(View view){
		next();
	}

	/**
	 * 显示上一个界面
	 * @param view
	 */
	public void showPre(View view){
		pre();
	}

	/**
	 * 打开新的界面并且关闭掉当前页面
	 * @param cls
	 */
	public void openNewActivityAndFinish( Class<?> cls){
		Intent intent = new Intent(this,cls);
		startActivity(intent);
		finish();
	}

}
