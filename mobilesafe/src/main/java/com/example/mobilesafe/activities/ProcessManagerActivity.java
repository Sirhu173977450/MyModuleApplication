package com.example.mobilesafe.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.domain.ProcessInfo;
import com.example.mobilesafe.engine.ProcessInfoProvider;
import com.example.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 进程管理
 */
public class ProcessManagerActivity extends Activity {
	/**
	 * 进程数量
	 */
	private TextView tv_processcount;
	/**
	 * 内存状态
	 */
	private TextView tv_memory_status;
	/**
	 * 进程列表
	 */
	private ListView lv_processinfos;

	/**
	 * 所有进程信息的集合
	 */
	private List<ProcessInfo> infos;

	/**
	 * 所有用户进程信息的集合
	 */
	private List<ProcessInfo> userInfos;

	/**
	 * 所有系统进程信息的集合
	 */
	private List<ProcessInfo> systemInfos;

	private ProcessInfoAdapter adapter;
	/**
	 * 记录正在运行的进程数量
	 */
	private int runningProcessCount;
	/**
	 * 记录剩余的内存空间
	 */
	private long availMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//加载ui界面
		setContentView(R.layout.activity_process_manager);
		tv_processcount = (TextView) findViewById(R.id.tv_processcount);
		tv_memory_status = (TextView) findViewById(R.id.tv_memory_status);
		fillData();
		//给listview注册条目点击事件
		lv_processinfos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				//得到listview某个位置对应的对象。
				Object obj = lv_processinfos.getItemAtPosition(position);
				if(obj!=null){
					ProcessInfo info = (ProcessInfo) obj;
					if(info.getPackName().equals(getPackageName())){
						return;
					}
					CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
					if(info.isChecked()){
						//取消checkbox的勾选
						cb.setChecked(false);
						info.setChecked(false);
					}else{
						//勾选checkbox
						cb.setChecked(true);
						info.setChecked(true);
					}
				}
			}
		});
	}
	//填充数据
	private void fillData() {
		runningProcessCount = getRunningProcessCount();
		tv_processcount.setText("运行中进程："+runningProcessCount+"个");
		availMem = getAvailMemory();
		tv_memory_status.setText("可用内存："+Formatter.formatFileSize(this, availMem));
		infos = ProcessInfoProvider.getRunningProcessInfos(this);
		userInfos = new ArrayList<ProcessInfo>();
		systemInfos = new ArrayList<ProcessInfo>();
		for(ProcessInfo info: infos){
			if(info.isUserProcess()){
				userInfos.add(info);
			}else{
				systemInfos.add(info);
			}
		}
		lv_processinfos = (ListView) findViewById(R.id.lv_processinfos);
		adapter = new ProcessInfoAdapter();
		lv_processinfos.setAdapter(adapter);
	}

	private class ProcessInfoAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return userInfos.size()+1+systemInfos.size()+1;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProcessInfo info;
			if(position==0){
				TextView  tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户进程："+userInfos.size()+"个");
				return tv;
			}else if(position == (userInfos.size()+1)){
				TextView  tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统进程："+systemInfos.size()+"个");
				return tv;
			}else if(position<=userInfos.size()){//用户进程
				info = userInfos.get(position-1);
			}else{//系统进程
				info = systemInfos.get(position-1-userInfos.size()-1);
			}
			View view;
			ViewHolder holder;
			if(convertView!=null&&convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(), R.layout.item_process_item, null);
				holder = new ViewHolder();
				holder.iv = (ImageView) view.findViewById(R.id.iv_process_icon);
				holder.tv_mem = (TextView) view.findViewById(R.id.tv_process_memsize);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_process_name);
				holder.cb = (CheckBox) view.findViewById(R.id.cb);
				view.setTag(holder);
			}
			if(info.getPackName().equals(getPackageName())){
				//当前item为手机卫士自己的应用程序
				holder.cb.setVisibility(View.INVISIBLE);
			}else{
				holder.cb.setVisibility(View.VISIBLE);
			}
			holder.iv.setImageDrawable(info.getAppIcon());
			holder.tv_name.setText(info.getAppName());
			holder.tv_mem.setText("占用内存："+Formatter.formatFileSize(getApplicationContext(), info.getMemSize()));
			//通过item里面保存的状态更新界面checkbox的状态
			holder.cb.setChecked(info.isChecked());
			return view;
		}
		@Override
		public Object getItem(int position) {
			ProcessInfo info;
			if(position==0){
				return null;
			}else if(position == (userInfos.size()+1)){
				return null;
			}else if(position<=userInfos.size()){//用户进程
				info = userInfos.get(position-1);
			}else{//系统进程
				info = systemInfos.get(position-1-userInfos.size()-1);
			}
			return info;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	static class ViewHolder{
		ImageView iv;
		TextView tv_name ;
		TextView tv_mem;
		CheckBox cb;
	}

	/**
	 * 获取正在运行的进程的数量
	 * @return
	 */
	private int getRunningProcessCount(){
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}
	/**
	 * 获取手机可用的内存空间
	 * @return
	 */
	private long getAvailMemory(){
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		//获取系统当前的内存信息，数据放在outInfo对象里面
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	/**
	 * 清理掉所有的选中的进程
	 * @param view
	 */
	public void killSelected(View view){
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ProcessInfo> killedProcessInfos = new ArrayList<ProcessInfo>();
		for(ProcessInfo info: userInfos){
			if(info.isChecked()){
				am.killBackgroundProcesses(info.getPackName());
				killedProcessInfos.add(info);
			}
		}
		for(ProcessInfo info: systemInfos){
			if(info.isChecked()){
				am.killBackgroundProcesses(info.getPackName());
				killedProcessInfos.add(info);
			}
		}
		//进程清理完毕，刷新listview的界面。
		//fillData();
		long total = 0;
		for(ProcessInfo info:killedProcessInfos){
			total+=info.getMemSize();
			if(info.isUserProcess()){
				userInfos.remove(info);
			}else{
				systemInfos.remove(info);
			}
		}
		adapter.notifyDataSetChanged();
		Toast.makeText(this, "清理了"+killedProcessInfos.size()+"个进程,释放了"+Formatter.formatFileSize(this, total)+"的内存", 1).show();
		runningProcessCount -=killedProcessInfos.size();
		tv_processcount.setText("运行中进程："+runningProcessCount+"个");
		availMem += total;
		tv_memory_status.setText("可用内存："+Formatter.formatFileSize(this, availMem));

	}
	/**
	 * 一键全选
	 * @param view
	 */
	public void selectAll(View view){
		for(ProcessInfo info: userInfos){
			if(info.getPackName().equals(getPackageName())){
				continue;
			}
			info.setChecked(true);
		}
		for(ProcessInfo info: systemInfos){
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}
	/**
	 * 一键反选
	 * @param view
	 */
	public void selectOther(View view){
		for(ProcessInfo info: userInfos){
			if(info.getPackName().equals(getPackageName())){
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		for(ProcessInfo info: systemInfos){
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}
}
