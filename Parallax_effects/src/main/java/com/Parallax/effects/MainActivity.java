package com.Parallax.effects;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// View
		final ParallaxListView plv = (ParallaxListView) findViewById(R.id.plv);

		// 添加一个头布局
		View headerView = View.inflate(this, R.layout.layout_header, null);
		final ImageView iv_header = (ImageView) headerView.findViewById(R.id.iv_header);
		plv.addHeaderView(headerView);
		
		// 等View的树状结构全部渲染完毕时候, 再设置到plv里.
		
		iv_header.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// 宽高已经测量完毕
				plv.setParallaxImage(iv_header);
				iv_header.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		// Model Controller
		plv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.NAMES));
		
	}

}
