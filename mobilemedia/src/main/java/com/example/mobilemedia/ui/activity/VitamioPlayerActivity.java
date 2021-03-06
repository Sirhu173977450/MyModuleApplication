package com.example.mobilemedia.ui.activity;


import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.mobilemedia.R;
import com.example.mobilemedia.base.BaseActivity;
import com.example.mobilemedia.bean.VideoItem;
import com.example.mobilemedia.ui.view.VideoView;
import com.example.mobilemedia.util.LogUtil;
import com.example.mobilemedia.util.StringUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import io.vov.vitamio.LibsChecker;

public class VitamioPlayerActivity extends BaseActivity {
	private VideoView video_view;
	//top control 
	private TextView tv_name,tv_system_time;
	private ImageView iv_battery;
	private ImageView iv_voice;
	private SeekBar voice_seekbar;
	
	//bottom control
	private ImageView btn_play,btn_exit,btn_pre,btn_next,btn_screen;
	private TextView tv_current_position,tv_duration;
	private SeekBar video_seekbar;
	private LinearLayout ll_top_control,ll_bottom_control;
	private LinearLayout ll_loading,ll_buffer;
	
	private int currentPosition;//当前播放视频的位置
	private ArrayList<VideoItem> videoList;//当前的视频列表
	private BatteryChangeReceiver batteryChangeReceiver;
	private AudioManager audioManager;
	private int touchSlop;
	private GestureDetector gestureDetector;
	
	private final int MSG_UPDATE_SYSTEM_TIME = 0;//更新系统时间
	private final int MSG_UPDATE_PLAY_PROGRESS = 1;//更新播放进度
	private final int MSG_HIDE_CONTROL = 2;//延时隐藏控制面板
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_SYSTEM_TIME:
				updateSystemTime();
				break;
			case MSG_UPDATE_PLAY_PROGRESS:
				updatePlayProgress();
				break;
			case MSG_HIDE_CONTROL:
				hideControlLayout();
				break;
			}
		};
	};
	
	private int maxVolume;//系统中音乐和视频类型最大音量
	private int currentVolume;//系统音乐和视频类型当前的音量
	private boolean isMute = false;//是否是静音模式
	private int screenWidth,screenHeight;
	private boolean isShowControlLayout = false;//是否是显示控制面板
	
	/**
	 * 更新系统时间
	 */
	private void updateSystemTime(){
		tv_system_time.setText(StringUtil.formatSystemTime());
		handler.sendEmptyMessageDelayed(MSG_UPDATE_SYSTEM_TIME, 1000);
	}
	
	/**
	 * 更新播放进度
	 */
	private void updatePlayProgress(){
		tv_current_position.setText(StringUtil.formatVideoDuration(video_view.getCurrentPosition()));
		video_seekbar.setProgress((int) video_view.getCurrentPosition());
		handler.sendEmptyMessageDelayed(MSG_UPDATE_PLAY_PROGRESS, 500);
	}
	
	@Override
	protected void initView() {
		//检查vitamio类库是否正确加载
		LibsChecker.checkVitamioLibs(this);
		
		setContentView(R.layout.activity_video_player);
		video_view = (VideoView) findViewById(R.id.video_view);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_system_time = (TextView) findViewById(R.id.tv_system_time);
		iv_battery = (ImageView) findViewById(R.id.iv_battery);
		iv_voice = (ImageView) findViewById(R.id.iv_voice);
		voice_seekbar = (SeekBar) findViewById(R.id.voice_seekbar);
		
		btn_play = (ImageView) findViewById(R.id.btn_play);
		btn_exit = (ImageView) findViewById(R.id.btn_exit);
		btn_pre = (ImageView) findViewById(R.id.btn_pre);
		btn_next = (ImageView) findViewById(R.id.btn_next);
		btn_screen = (ImageView) findViewById(R.id.btn_screen);
		tv_current_position =(TextView) findViewById(R.id.tv_current_position);
		tv_duration =(TextView) findViewById(R.id.tv_duration);
		video_seekbar =(SeekBar) findViewById(R.id.video_seekbar);
		ll_top_control = (LinearLayout) findViewById(R.id.ll_top_control);
		ll_bottom_control = (LinearLayout) findViewById(R.id.ll_bottom_control);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
	}

	@Override
	protected void initListener() {
		ll_top_control.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ll_top_control.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				ViewPropertyAnimator.animate(ll_top_control).translationY(-ll_top_control.getHeight()).setDuration(0);
			}
		});
		ll_bottom_control.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ll_bottom_control.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				ViewPropertyAnimator.animate(ll_bottom_control).translationY(ll_bottom_control.getHeight()).setDuration(0);
			}
		});
		
		iv_voice.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		btn_pre.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		btn_screen.setOnClickListener(this);
		btn_play.setOnClickListener(this);
		voice_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL, 5000);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				handler.removeMessages(MSG_HIDE_CONTROL);
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){//表示是用户手动拖动
					isMute = false;
					currentVolume = progress;
					updateSystemVolume();
				}
			}
		});
		video_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL, 5000);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				handler.removeMessages(MSG_HIDE_CONTROL);
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					video_view.seekTo(progress);
					tv_current_position.setText(StringUtil.formatVideoDuration(progress));
				}
			}
		});
		video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				btn_play.setBackgroundResource(R.drawable.selector_btn_play);
			}
		});
		video_view.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				//percent:0-100
				long bufferProgress = (long) ((video_view.getDuration()/100f)*percent);
				video_seekbar.setSecondaryProgress((int) bufferProgress);
			}
		});
		video_view.setOnInfoListener(new MediaPlayer.OnInfoListener() {
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				switch (what) {
				case MediaPlayer.MEDIA_INFO_BUFFERING_START :
//					Toast.makeText(VideoPlayerActivity.this, "开始卡顿", 0).show();
					ll_buffer.setVisibility(View.VISIBLE);
					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_END :
					ll_buffer.setVisibility(View.GONE);
//					Toast.makeText(VideoPlayerActivity.this, "卡顿结束。。。。。。。。", 0).show();
					break;
				}
				return true;
			}
		});
		video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				switch (what) {
				case MediaPlayer.MEDIA_ERROR_UNKNOWN :
					//未知格式，视频文件错误
//					Toast.makeText(VitamioPlayerActivity.this, "视频格式不支持", 0).show();
					Builder builder = new Builder(VitamioPlayerActivity.this);
					builder.setTitle("提示")
					.setMessage("视频文件错误或者格式不支持，点击确定退出播放器")
					.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					builder.create().show();
					break;
				}
				return true;
			}
		});
	}
	/**
	 * 更新系统音量
	 */
	private void updateSystemVolume(){
		if(isMute){
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			voice_seekbar.setProgress(0);
		}else {
			voice_seekbar.setProgress(currentVolume);
			//第三个参数如果是1，会显示音量改变的浮动面板
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
		}
	}

	@Override
	protected void initData() {
		gestureDetector = new GestureDetector(this,new MyOnGestureListner());
		touchSlop = ViewConfiguration.getTouchSlop();
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		updateSystemTime();
		registerBatteryReceiver();
		initVolume();
		
		Uri videoUri = getIntent().getData();
		if(videoUri!=null){
			//从文件发起的请求
			LogUtil.e(this, "uri: "+videoUri.getPath());
			video_view.setVideoURI(videoUri);
			btn_pre.setEnabled(false);
			btn_next.setEnabled(false);
			tv_name.setText(videoUri.getPath());
		}else {
			//正常从视频列表中进入的
			currentPosition = getIntent().getExtras().getInt("currentPosition");
			videoList = (ArrayList<VideoItem>) getIntent().getExtras().getSerializable("videoList");
			
			playVideo();
		}
		
		
		video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				ViewPropertyAnimator.animate(ll_loading).alpha(0).setDuration(1000).setListener(new AnimatorListener() {
					@Override
					public void onAnimationStart(Animator arg0) {
					}
					@Override
					public void onAnimationRepeat(Animator arg0) {
					}
					@Override
					public void onAnimationEnd(Animator arg0) {
						ll_loading.setVisibility(View.GONE);
					}
					@Override
					public void onAnimationCancel(Animator arg0) {
					}
				});
//				
				video_view.start();
				
				updatePlayProgress();
				video_seekbar.setMax((int) video_view.getDuration());
				tv_current_position.setText("00:00");
				tv_duration.setText(StringUtil.formatVideoDuration(video_view.getDuration()));
				
				btn_play.setBackgroundResource(R.drawable.selector_btn_pause);
			}
		});
		
//		video_view.setMediaController(new MediaController(this));
	}
	
	private float downY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = event.getY();
			handler.removeMessages(MSG_HIDE_CONTROL);
			break;
		case MotionEvent.ACTION_MOVE:
			float moveY = event.getY();
			float moveDistance = moveY - downY;
			
			//对滑动距离进行一个值的限制
			if(Math.abs(moveDistance)<touchSlop)break;
			
			isMute = false;
			
			float totalDistance = Math.min(screenHeight, screenWidth);
			float movePercent = Math.abs(moveDistance)/totalDistance;
			LogUtil.e(this, "movePercent: "+movePercent);
			int moveVolume = (int) (movePercent*maxVolume);//这个值一定是0
			
			if(moveDistance>0){
				//减小音量
				currentVolume -= 1;
			}else {
				//增大音量
				currentVolume += 1;
			}
			updateSystemVolume();
			
			downY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL, 5000);
			break;
		}
		return super.onTouchEvent(event);
	}
	
	/**
	 * 初始化系统音量
	 */
	private void initVolume(){
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		//maxVolume:0-15   
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		voice_seekbar.setMax(maxVolume);
		voice_seekbar.setProgress(currentVolume);
	}
	
	/**
	 * 注册系统电量变化的广播接受者
	 */
	private void registerBatteryReceiver(){
		batteryChangeReceiver = new BatteryChangeReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryChangeReceiver, filter);
		
	}
	
	/**
	 * 播放当前位置的视频
	 */
	private void playVideo(){
		btn_pre.setEnabled(currentPosition!=0);
		btn_next.setEnabled(currentPosition!=(videoList.size()-1));
		
		VideoItem videoItem = videoList.get(currentPosition);
		tv_name.setText(videoItem.getTitle());
		video_view.setVideoURI(Uri.parse(videoItem.getPath()));
	}

	@Override
	protected void processClick(View v) {
		switch (v.getId()) {
		case R.id.btn_exit:
			finish();
			break;
		case R.id.iv_voice:
			isMute = !isMute;
			updateSystemVolume();
			break;
		case R.id.btn_play:
			if(video_view.isPlaying()){
				video_view.pause();
			}else {
				video_view.start();
			}
			updateBtnPlayBg();
			break;
		case R.id.btn_pre:
			if(currentPosition>0){
				currentPosition --;
				playVideo();
			}
			break;
		case R.id.btn_next:
			if(currentPosition<(videoList.size()-1)){
				currentPosition ++;
				playVideo();
			}
			break;
		case R.id.btn_screen:
			video_view.switchScreen();
			updateScreenBtnBg();
			break;
		}
	}
	/**
	 * 根据是否是全屏设置屏幕按钮的背景图片
	 */
	private void updateScreenBtnBg(){
		btn_screen.setBackgroundResource(video_view.isFullScreen()?
				R.drawable.selector_btn_defaultscreen:R.drawable.selector_btn_fullscreen);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
		unregisterReceiver(batteryChangeReceiver);
	}
	
	private class MyOnGestureListner extends SimpleOnGestureListener{
		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			processClick(btn_play);
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			processClick(btn_screen);
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if(isShowControlLayout){
				//隐藏操作
				hideControlLayout();
			}else {
				//显示操作
				showControlLayout();
			}
			return super.onSingleTapConfirmed(e);
		}
		
	}
	
	private void showControlLayout(){
		ViewPropertyAnimator.animate(ll_top_control).translationY(0).setDuration(200);
		ViewPropertyAnimator.animate(ll_bottom_control).translationY(0).setDuration(200);
		isShowControlLayout = true;
		handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL, 5000);
	}
	
	private void hideControlLayout(){
		ViewPropertyAnimator.animate(ll_top_control).translationY(-ll_top_control.getHeight()).setDuration(200);
		ViewPropertyAnimator.animate(ll_bottom_control).translationY(ll_bottom_control.getHeight()).setDuration(200);
		isShowControlLayout = false;
	}
	
	/**
	 * 电池电量变化的广播接受者
	 * @author Administrator
	 *
	 */
	private class BatteryChangeReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//level:0-100
			int level = intent.getIntExtra("level", 0);
			updateBatteryBg(level);
		}
	}
	
	private void updateBatteryBg(int level){
		if(level==0){
			iv_battery.setBackgroundResource(R.drawable.ic_battery_0);
		}else if (level>0 && level<=10) {
			iv_battery.setBackgroundResource(R.drawable.ic_battery_10);
		}else if (level>10 && level<=20) {
			iv_battery.setBackgroundResource(R.drawable.ic_battery_20);
		}else if (level>20 && level<=40) {
			iv_battery.setBackgroundResource(R.drawable.ic_battery_40);
		}else if (level>40 && level<=60) {
			iv_battery.setBackgroundResource(R.drawable.ic_battery_60);
		}else if (level>60 && level<=80) {
			iv_battery.setBackgroundResource(R.drawable.ic_battery_80);
		}else {
			iv_battery.setBackgroundResource(R.drawable.ic_battery_100);
		}
	}
	
	/**
	 * 根据是否正在播放更改播放按钮的背景图片
	 */
	private void updateBtnPlayBg(){
		btn_play.setBackgroundResource(video_view.isPlaying()?R.drawable.selector_btn_pause:R.drawable.selector_btn_play);
	}

}
