package com.example.mobilesafe.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mobilesafe.R;

import java.io.File;

public class CleanSDFragment extends Fragment {
	//创建fragment显示内容的方法
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.fragment_clean_sd, null);
	}

	@Override
	public void onStart() {
		super.onStart();
		File file = Environment.getExternalStorageDirectory();
		File[] files = file.listFiles();
		for(File f:files){
			if(f.isFile()){//文件
				//.tmp .temp
			}else{//文件夹

			}
		}
	}


}
