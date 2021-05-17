package com.example.mobilesafe.activities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.db.dao.ApplockDao;
import com.example.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;


public class ApplockActivity extends Activity implements OnClickListener {
	private TextView tv_locked;
	private TextView tv_unlock;
	private LinearLayout ll_locked;
	private LinearLayout ll_unlock;
	/**
	 * 未加锁的listview
	 */
	private ListView lv_unlock_items;
	/**
	 * 已加锁的listview
	 */
	private ListView lv_locked_items;

	private TextView tv_locked_count;
	private TextView tv_unlock_count;


	private PackageManager pm;

	/**
	 * 手机系统里面所有的安装的应用程序信息
	 */
	private List<PackageInfo> packinfos;

	/**
	 * 已锁定应用程序集合信息
	 */
	private List<PackageInfo> lockedPackinfos;
	/**
	 * 未锁定应用程序集合信息
	 */
	private List<PackageInfo> unlockPackinfos;


	private ApplockDao dao;


	private UnlockItemsAdapter unlockItemsAdapter;
	private LockedItemsAdapter lockedItemsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);
		dao  = new ApplockDao(this);
		tv_locked = (TextView) findViewById(R.id.tv_locked);
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		ll_locked = (LinearLayout) findViewById(R.id.ll_locked);
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		tv_locked.setOnClickListener(this);
		tv_unlock.setOnClickListener(this);
		lv_unlock_items = (ListView) findViewById(R.id.lv_unlock_items);
		lv_locked_items = (ListView) findViewById(R.id.lv_locked_items);
		tv_locked_count = (TextView) findViewById(R.id.tv_locked_count);
		tv_unlock_count = (TextView) findViewById(R.id.tv_unlock_count);
		//得到系统里面的所有的应用程序
		pm = getPackageManager();
		packinfos = pm.getInstalledPackages(0);
		unlockPackinfos = new ArrayList<PackageInfo>();
		lockedPackinfos = new ArrayList<PackageInfo>();
		for(PackageInfo info:packinfos){
			if(dao.find(info.packageName)){
				//被锁定的应用程序
				lockedPackinfos.add(info);
			}else{
				//没有被锁定的应用程序
				unlockPackinfos.add(info);
			}
		}
		unlockItemsAdapter = new UnlockItemsAdapter();
		lv_unlock_items.setAdapter(unlockItemsAdapter);
		lockedItemsAdapter = new LockedItemsAdapter();
		lv_locked_items.setAdapter(lockedItemsAdapter);

		//给未加锁的item注册点击事件
		lv_unlock_items.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									final int position, long id) {
				final PackageInfo packInfo = unlockPackinfos.get(position);
				dao.add(packInfo.packageName);
				TranslateAnimation ta  = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f,
						Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				ta.setDuration(300);
				//动画的播放不是阻塞式的操作。
				view.startAnimation(ta);
				ta.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						//动画开始播放了。。。
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						//动画重复播放了。
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						//动画播放完毕了。
						//从当前页面把应用程序条目给移除
						unlockPackinfos.remove(position);
						unlockItemsAdapter.notifyDataSetChanged();
						//把数据加入到已加锁的列表里面
						lockedPackinfos.add(packInfo);
						lockedItemsAdapter.notifyDataSetChanged();
					}
				});

			}
		});

		//给已加锁的item注册点击事件
		lv_locked_items.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									final int position, long id) {
				final PackageInfo packInfo = lockedPackinfos.get(position);
				dao.delete(packInfo.packageName);
				TranslateAnimation ta  = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f,
						Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				ta.setDuration(300);
				view.startAnimation(ta);
				ta.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						lockedPackinfos.remove(position);
						lockedItemsAdapter.notifyDataSetChanged();
						unlockPackinfos.add(packInfo);
						unlockItemsAdapter.notifyDataSetChanged();
					}
				});

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_unlock:
				tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				tv_locked.setBackgroundResource(R.drawable.tab_right_default);
				ll_unlock.setVisibility(View.VISIBLE);
				ll_locked.setVisibility(View.INVISIBLE);
				break;
			case R.id.tv_locked:
				tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
				tv_locked.setBackgroundResource(R.drawable.tab_right_pressed);
				ll_unlock.setVisibility(View.INVISIBLE);
				ll_locked.setVisibility(View.VISIBLE);
				break;
		}
	}
	/**
	 * 未加锁条目的数据适配器
	 */
	private class UnlockItemsAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			tv_unlock_count.setText("未加锁程序："+unlockPackinfos.size()+"个");
			return unlockPackinfos.size();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView!=null&&convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(), R.layout.item_unlock, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(unlockPackinfos.get(position).applicationInfo.loadIcon(pm));
			holder.tv_name.setText(unlockPackinfos.get(position).applicationInfo.loadLabel(pm));
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}
	/**
	 * 已加锁条目的数据适配器
	 * @author zzh
	 *
	 */
	private class LockedItemsAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			tv_locked_count.setText("已加锁程序："+lockedPackinfos.size()+"个");
			return lockedPackinfos.size();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView!=null&&convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(), R.layout.item_locked, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(lockedPackinfos.get(position).applicationInfo.loadIcon(pm));
			holder.tv_name.setText(lockedPackinfos.get(position).applicationInfo.loadLabel(pm));
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}


	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
	}
}
