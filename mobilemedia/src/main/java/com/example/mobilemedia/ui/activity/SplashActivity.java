package com.example.mobilemedia.ui.activity;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.example.mobilemedia.R;
import com.example.mobilemedia.base.BaseActivity;


public class SplashActivity extends BaseActivity {

	@Override
	protected void initView() {
		setContentView(R.layout.activity_splash);
		delayEnterMainActivity(true);
	}

	private void delayEnterMainActivity(boolean isDelay) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!hasEnterMain){
					hasEnterMain = true;
					enterActivity(MainActivity.class);
					finish();
				}
			}
		}, isDelay ? 2000 : 0);
	}

	private boolean hasEnterMain = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			delayEnterMainActivity(false);
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void initListener() {

	}

	@Override
	protected void initData() {

	}

	@Override
	protected void processClick(View v) {

	}

}
