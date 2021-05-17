package com.example.mobilesafe.activities;

import android.os.Bundle;

import com.example.mobilesafe.R;


public class Setup1Activity extends SetupBaseActivity {
	protected static final String TAG = "Setup1Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}



	@Override
	public void next() {
		openNewActivityAndFinish(Setup2Activity.class);
		// 修改Activity切换的动画效果
		overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
	}

	@Override
	public void pre() {

	}
}
