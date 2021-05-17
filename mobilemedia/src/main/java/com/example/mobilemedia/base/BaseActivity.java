package com.example.mobilemedia.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.fragment.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initListener();
		initData();
	}

	protected abstract void initView();

	protected abstract void initListener();

	protected abstract void initData();

	// 可以处理一些共同按钮的点击事件
	protected abstract void processClick(View v);

	@Override
	public void onClick(View v) {
		processClick(v);
	}
	
	protected void enterActivity(Class<?> targetActivity){
		startActivity(new Intent(this,targetActivity));
	}
	protected void enterActivity(Bundle bundle,Class<?> targetActivity){
		Intent intent = new Intent(this,targetActivity);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
