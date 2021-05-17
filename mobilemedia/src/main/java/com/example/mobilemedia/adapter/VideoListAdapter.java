package com.example.mobilemedia.adapter;


import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.mobilemedia.R;
import com.example.mobilemedia.bean.VideoItem;
import com.example.mobilemedia.util.StringUtil;

public class VideoListAdapter extends CursorAdapter{

	public VideoListAdapter(Context context, Cursor c) {
		super(context, c);
	}
	
	/**
	 * 直接从布局中加载view返回
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return View.inflate(context, R.layout.adapter_video_list, null);
	}
	
	ViewHolder holder;
	/**
	 * 将数据设置给view
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		holder = getHolder(view);
		
		VideoItem videoItem = VideoItem.fromCursor(cursor);
		
		holder.tv_title.setText(videoItem.getTitle());
		holder.tv_duration.setText(StringUtil.formatVideoDuration(videoItem.getDuration()));
		holder.tv_size.setText(Formatter.formatFileSize(context, videoItem.getSize()));
		
	}
	
	private ViewHolder getHolder(View view){
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		if(viewHolder==null){
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		}
		return viewHolder;
	}
	
	class ViewHolder{
		TextView tv_title,tv_duration,tv_size;
		public ViewHolder(View view){
			tv_title = (TextView) view.findViewById(R.id.tv_title);
			tv_duration = (TextView) view.findViewById(R.id.tv_duration);
			tv_size = (TextView) view.findViewById(R.id.tv_size);
		}
	}

}
