package com.example.mobilemedia.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	public static void showToast(Context context,String msg){
		Toast.makeText(context, msg, 0).show();
	}
}
