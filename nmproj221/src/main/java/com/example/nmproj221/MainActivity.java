package com.example.nmproj221;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 测试压缩视频
		FaveUtil.getInstance().compressVideo("d://zcx.mp4", "d://zcxa.mp4");
	}
}
