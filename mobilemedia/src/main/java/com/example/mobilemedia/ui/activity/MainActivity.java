package com.example.mobilemedia.ui.activity;


import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.mobilemedia.R;
import com.example.mobilemedia.adapter.MainPagerAdapter;
import com.example.mobilemedia.base.BaseActivity;
import com.example.mobilemedia.ui.fragment.AudioListFragment;
import com.example.mobilemedia.ui.fragment.VideoListFragment;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
	private TextView tab_video, tab_audio;
	private ViewPager view_pager;
	private View indicate_line;

	private MainPagerAdapter adapter;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private int lineWidth;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		view_pager = (ViewPager) findViewById(R.id.view_pager);
		tab_video = (TextView) findViewById(R.id.tab_video);
		tab_audio = (TextView) findViewById(R.id.tab_audio);
		indicate_line = findViewById(R.id.indicate_line);
	}

	@Override
	protected void initListener() {
		tab_video.setOnClickListener(this);
		tab_audio.setOnClickListener(this);
		view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				lightAndScaleTabTitle();
			}
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				int targetPosition = position*lineWidth + positionOffsetPixels/fragments.size();
				ViewPropertyAnimator.animate(indicate_line).translationX(targetPosition).setDuration(0);
//				LogUtil.e(this, "position: "+position +"   positionOffset: "+positionOffset+" positionOffsetPixels:  "+positionOffsetPixels);
//				ViewHelper.setTranslationX(indicate_line, targetPosition);
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	/**
	 * 使TabTitle高亮
	 */
	protected void lightAndScaleTabTitle() {
		int currentPage = view_pager.getCurrentItem();
		tab_video.setTextColor(currentPage == 0 ? getResources().getColor(
				R.color.indicate_line) : getResources().getColor(
				R.color.gray_white));
		tab_audio.setTextColor(currentPage == 1 ? getResources().getColor(
				R.color.indicate_line) : getResources().getColor(
				R.color.gray_white));

		ViewPropertyAnimator.animate(tab_video)
				.scaleX(currentPage == 0 ? 1.2f : 1).setDuration(200);
		ViewPropertyAnimator.animate(tab_video)
				.scaleY(currentPage == 0 ? 1.2f : 1).setDuration(200);
		ViewPropertyAnimator.animate(tab_audio)
				.scaleX(currentPage == 1 ? 1.2f : 1).setDuration(200);
		ViewPropertyAnimator.animate(tab_audio)
				.scaleY(currentPage == 1 ? 1.2f : 1).setDuration(200);
	}
	
	/**
	 * 动态计算指示线的宽度
	 */
	private void calculateIndicateLineWidth(){
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		lineWidth = screenWidth/fragments.size();
		indicate_line.getLayoutParams().width = lineWidth;
		indicate_line.requestLayout();
	}

	@Override
	protected void initData() {
		fragments.add(new VideoListFragment());
		fragments.add(new AudioListFragment());
		
		calculateIndicateLineWidth();
		
		adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
		view_pager.setAdapter(adapter);

		lightAndScaleTabTitle();
	
	}

	@Override
	protected void processClick(View v) {
		switch (v.getId()) {
		case R.id.tab_video:
			view_pager.setCurrentItem(0);
			break;
		case R.id.tab_audio:
			view_pager.setCurrentItem(1);
			break;
		}
	}

}
