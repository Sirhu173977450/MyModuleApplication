package com.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.db.dao.BlackNumberDao;
import com.example.mobilesafe.domain.BlackNumberInfo;
import com.example.mobilesafe.R;

import java.util.List;

public class CallSmsSafeActivity extends Activity {
	private BlackNumberDao dao;
	private ListView lv_callsms_safe;
	private CallSmsSafeAdapter adapter;
	private ImageView iv_callsms_safe_empty;
	private LinearLayout loading;
	/**
	 * 黑名单号码的全部信息集合
	 */
	private List<BlackNumberInfo> infos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsms_safe);
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		iv_callsms_safe_empty = (ImageView) findViewById(R.id.iv_callsms_safe_empty);
		loading = (LinearLayout) findViewById(R.id.loading);
		//获取全部的黑名单号码
		dao = new BlackNumberDao(this);

		loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				infos = dao.findAll();
				adapter = new CallSmsSafeAdapter();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						lv_callsms_safe.setAdapter(adapter);
						loading.setVisibility(View.INVISIBLE);
					}
				});
			};
		}.start();


	}

	private class CallSmsSafeAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			int size =  infos.size();
			if(size>0){
				//listview里面有数据 隐藏图片
				iv_callsms_safe_empty.setVisibility(View.INVISIBLE);
			}else{
				//listview里面没有数据 显示图片
				iv_callsms_safe_empty.setVisibility(View.VISIBLE);
			}
			return size;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view ;
			ViewHolder holder;
			//复用历史缓存的view对象,减少view对象创建的格式.
			if(convertView==null){
				view = View.inflate(CallSmsSafeActivity.this, R.layout.item_call_smssafe, null);
				//相当于姓名本,用来记录孩子的引用
				holder = new ViewHolder();
				holder.tv_phone = (TextView) view.findViewById(R.id.tv_item_blacknumber);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_item_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_item_delete);
				//把姓名本放在当前的view对象里面.
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			//查找子孩子会比较消耗时间,需要对下面的代码进一步的优化.
			final BlackNumberInfo info = infos.get(position);
			holder.tv_phone.setText(info.getPhone());
			String mode = info.getMode(); //1电话 2短信 3全部
			if("1".equals(mode)){
				holder.tv_mode.setText("电话拦截");
			}else if("2".equals(mode)){
				holder.tv_mode.setText("短信拦截");
			}else{
				holder.tv_mode.setText("全部拦截");
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//从数据库把记录给移除
					boolean result = dao.delete(info.getPhone());
					if(result){
						Toast.makeText(CallSmsSafeActivity.this, "删除成功", 0).show();
						//更新ui界面.
						infos.remove(info);//移除listview绑定的集合里面的数据
						adapter.notifyDataSetChanged();//通知listview里面的数据更新.
					}else{
						Toast.makeText(CallSmsSafeActivity.this, "删除失败", 0).show();
					}
				}
			});
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
	 * view 的容器,用来存储子孩子的引用.
	 */
	class ViewHolder{
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	/**
	 * 添加黑名单号码
	 */
	public void addBlackNumber(View view){
		Intent intent = new Intent(this,AddBlackNumberActivity.class);
		//开启新的界面,获取返回值.
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			boolean flag = data.getBooleanExtra("flag", false);
			if(flag){
				//重新获取数据,
				//infos = dao.findAll();
				String phone = data.getStringExtra("phone");
				String mode = data.getStringExtra("mode");
				BlackNumberInfo info = new BlackNumberInfo();
				info.setPhone(phone);
				info.setMode(mode);
				infos.add(info);//把新添加的数据直接加入到集合里面就可以了.避免了重新查询数据库,应用程序的效率得到了提高
				//通知listview刷新界面.
				adapter.notifyDataSetChanged();
				Toast.makeText(this, "添加成功", 0).show();
			}else{
				Toast.makeText(this, "添加失败", 0).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
