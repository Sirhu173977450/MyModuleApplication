package com.example.mobilesafe.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.utils.Md5Utils;
import com.example.mobilesafe.R;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;


public class HomeActivity extends Activity {
	protected static final String TAG = "HomeActivity";
	private ImageView iv_home_logo;
	private GridView gv_item;
	private SharedPreferences sp;
	String[] names = new String[] { "手机防盗", "骚扰拦截", "软件管家", "进程管理", "流量统计",
			"手机杀毒", "系统加速", "常用工具" };
	int[] icons = new int[] { R.drawable.sjfd, R.drawable.srlj,
			R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd,
			R.drawable.xtjs, R.drawable.cygj };
	String[] desc = new String[]{"远程定位手机","全面拦截骚扰","管理您的软件","管理正在运行","流量一目了然","病毒无处藏身",
			"系统快如火箭","常用工具大全"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//配置广告,id,密码,测试模式
		AdManager.getInstance(this).init("847377901819a2fb", "f2c5d51b6066058c", true);
		SpotManager.getInstance(this).loadSpotAds();
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		iv_home_logo = (ImageView) findViewById(R.id.iv_home_logo);
		gv_item = (GridView) findViewById(R.id.gv_home_item);
		ObjectAnimator oa = ObjectAnimator.ofFloat(iv_home_logo, "rotationY",
				45, 90, 135, 180, 225, 270, 315);
		oa.setDuration(3000);
		oa.setRepeatCount(ObjectAnimator.INFINITE);
		oa.setRepeatMode(ObjectAnimator.RESTART);
		oa.start();

		gv_item.setAdapter(new HomeAdapter());
		//给gridview的条目设置点击事件
		gv_item.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent;
				switch (position) {
					case 0://手机防盗
						//判断用户是否设置过密码
						if(isSetupPwd()){
							Log.i(TAG,"弹出输入密码的界面");
							showEnterPwdDialog();
						}else{
							Log.i(TAG,"弹出设置密码的界面");
							showSetupPwdDialog();
						}
						break;
					case 1://骚扰拦截
						intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
						startActivity(intent);
						break;
					case 2://程序管理器
						intent = new Intent(HomeActivity.this,AppManagerActivity.class);
						startActivity(intent);
						break;
					case 3://进程管理器
						intent = new Intent(HomeActivity.this, ProcessManagerActivity.class);
						startActivity(intent);
						break;
					case 4://流量统计
						intent = new Intent(HomeActivity.this,TrafficManagerActivity.class);
						startActivity(intent);
						break;
					case 5://手机杀毒
						intent = new Intent(HomeActivity.this,AntiVirusActivity.class);
						startActivity(intent);
						break;
					case 6://手机加速
						intent = new Intent(HomeActivity.this,SystemOptisActivity.class);
						startActivity(intent);
						break;
					case 7: //常用工具
						intent = new Intent(HomeActivity.this,CommonToolsActivity.class);
						startActivity(intent);
						break;
				}
			}
		});
	}

	private AlertDialog dialog;
	/**
	 * 显示设置密码对话框,自定义对话框
	 */
	protected void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_setup_pwd, null);
		builder.setView(view);
		final EditText et_pwd = (EditText) view.findViewById(R.id.et_dialog_pwd);
		final EditText et_pwd_confirm = (EditText) view.findViewById(R.id.et_dialog_pwd_confirm);
		Button bt_dialog_ok = (Button) view.findViewById(R.id.bt_dialog_ok);
		Button bt_dialog_cancle = (Button) view.findViewById(R.id.bt_dialog_cancle);
		bt_dialog_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_dialog_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = et_pwd.getText().toString().trim();
				String pwd_confirm = et_pwd_confirm.getText().toString().trim();
				if(TextUtils.isEmpty(pwd)||TextUtils.isEmpty(pwd_confirm)){
					Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if(!pwd.equals(pwd_confirm)){
					Toast.makeText(HomeActivity.this, "两次密码输入不一致",  Toast.LENGTH_SHORT).show();
					return;
				}
				Editor editor = sp.edit();
				editor.putString("password", Md5Utils.encode(pwd));
				editor.commit();
				//关闭对话框
				dialog.dismiss();
				//弹出输入密码对话框
				showEnterPwdDialog();
			}
		});
		//显示对话框,把对话框的引用赋给类的成员变量
		dialog = builder.show();
	}

	/**
	 * 显示输入密码对话框
	 */
	protected void showEnterPwdDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_enter_pwd, null);
		builder.setView(view);
		final EditText et_pwd = (EditText) view.findViewById(R.id.et_dialog_pwd);
		Button bt_dialog_ok = (Button) view.findViewById(R.id.bt_dialog_ok);
		Button bt_dialog_cancle = (Button) view.findViewById(R.id.bt_dialog_cancle);
		bt_dialog_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_dialog_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//获取用户输入的密码
				String pwd = et_pwd.getText().toString().trim();
				if(TextUtils.isEmpty(pwd)){
					Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				//获取原来用户设置的密码
				String savedpwd = sp.getString("password", null);
				//比较原来设置的密码和这次输入的密码是否一致.
				if(Md5Utils.encode(pwd).equals(savedpwd)){
					dialog.dismiss();
					//判断用户是否进入过设置向导界面,如果用户是第一次使用手机防盗功能,定向页面到设置向导
					boolean configed = sp.getBoolean("configed", false);
					if(configed){
						Log.i(TAG,"用户完成过设置向导,进入手机防盗的ui界面");
						Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
						startActivity(intent);
					}else{
						Log.i(TAG,"用户没有完成过设置向导,进入设置向导界面");
						Intent intent = new Intent(HomeActivity.this, Setup1Activity.class);
						startActivity(intent);
					}

				}else{
					Toast.makeText(HomeActivity.this, "密码输入错误", Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialog = builder.show();
	}

	private class HomeAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this, R.layout.item_home, null);
			ImageView iv= (ImageView) view.findViewById(R.id.iv_homeitem_icon);
			TextView tv_item_title =(TextView) view.findViewById(R.id.tv_homeitem_title);
			TextView tv_item_desc =(TextView) view.findViewById(R.id.tv_homeitem_desc);
			iv.setImageResource(icons[position]);
			tv_item_title.setText(names[position]);
			tv_item_desc.setText(desc[position]);
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
	 * 点击进入设置界面
	 * @param view
	 */
	public void enterSettingActivity(View view){
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}
	/**
	 * 判断用户是否设置过密码
	 * @return
	 */
	private boolean isSetupPwd(){
		String password = sp.getString("password", null);
		if(TextUtils.isEmpty(password)){
			return false;
		}else{
			return true;
		}
	}

	public void onBackPressed() {
		// 如果有需要，可以点击后退关闭插播广告。
		if (!SpotManager.getInstance(this).disMiss()) {
			// 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
			super.onBackPressed();
		}
	}
}