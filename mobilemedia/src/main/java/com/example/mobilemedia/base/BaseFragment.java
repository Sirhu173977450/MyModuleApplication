package com.example.mobilemedia.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements OnClickListener{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return initView(inflater, container, savedInstanceState);
	}
	
	protected abstract View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);

	protected abstract void initListener();

	protected abstract void initData();

	// 可以处理一些共同按钮的点击事件
	protected abstract void processClick(View v);
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initListener();
		initData();
	}
	@Override
	public void onClick(View v) {
		processClick(v);
	}
	
	protected void enterActivity(Bundle bundle,Class<?> targetActivity){
		Intent intent = new Intent(getActivity(),targetActivity);
		if(bundle!=null){
			intent.putExtras(bundle);
		}
		getActivity().startActivity(intent);
	}
}
