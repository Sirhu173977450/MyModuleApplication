package com.example.mobilemedia.ui.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Video.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.mobilemedia.R;
import com.example.mobilemedia.adapter.VideoListAdapter;
import com.example.mobilemedia.base.BaseFragment;
import com.example.mobilemedia.bean.VideoItem;
import com.example.mobilemedia.db.SimpleQueryHandler;
import com.example.mobilemedia.ui.activity.VitamioPlayerActivity;

import java.util.ArrayList;

public class VideoListFragment extends BaseFragment {
	private ListView listView;
	
	private SimpleQueryHandler queryHandler;
	private VideoListAdapter adapter;
	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_video_list, null);
		listView = (ListView) view.findViewById(R.id.listview);
		return view;
	}

	@Override
	protected void initListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor cursor = (Cursor) adapter.getItem(position);
				Bundle bundle = new Bundle();
				bundle.putInt("currentPosition", position);
				bundle.putSerializable("videoList", cursorToList(cursor));
				enterActivity(bundle, VitamioPlayerActivity.class);
			}
		});
	}

	@Override
	protected void initData() {
		queryHandler = new SimpleQueryHandler(getActivity().getContentResolver());
		adapter = new VideoListAdapter(getActivity(), null);
		listView.setAdapter(adapter);
		
		String[] projection = {Media._ID,Media.TITLE,Media.SIZE,Media.DURATION,Media.DATA};
//		Cursor cursor = getActivity().getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
//		CursorUtil.printCursor(cursor);
		queryHandler.startQuery(0, adapter, Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
	}
	
	/**
	 * 将cursor中的数据取出来放入集合当中
	 * @param cursor
	 * @return
	 */
	private ArrayList<VideoItem> cursorToList(Cursor cursor){
		ArrayList<VideoItem> list = new ArrayList<VideoItem>();
		cursor.moveToPosition(-1);
		while(cursor.moveToNext()){
			list.add(VideoItem.fromCursor(cursor));
		}
		return list;
	}

	@Override
	protected void processClick(View v) {
		
	}
}
