package com.example.mobilemedia.ui.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.mobilemedia.R;
import com.example.mobilemedia.adapter.AudioListAdapter;
import com.example.mobilemedia.base.BaseFragment;
import com.example.mobilemedia.bean.AudioItem;
import com.example.mobilemedia.db.SimpleQueryHandler;
import com.example.mobilemedia.ui.activity.AudioPlayerActivity;

import java.util.ArrayList;

public class AudioListFragment extends BaseFragment {
	private ListView listView;
	private AudioListAdapter adapter;
	private SimpleQueryHandler queryHandler;
	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_audio_list, null);
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
				bundle.putSerializable("audioList", cursorToList(cursor));
				enterActivity(bundle, AudioPlayerActivity.class);
			}
		});
	}

	@Override
	protected void initData() {
		adapter = new AudioListAdapter(getActivity(), null);
		listView.setAdapter(adapter);
		
		queryHandler = new SimpleQueryHandler(getActivity().getContentResolver());
		String[] projection = {Media._ID,Media.DISPLAY_NAME,Media.ARTIST,Media.DATA,Media.DURATION};
		queryHandler.startQuery(0, adapter, Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
	}

	private ArrayList<AudioItem> cursorToList(Cursor cursor){
		ArrayList<AudioItem> list = new ArrayList<AudioItem>();
		cursor.moveToPosition(-1);
		while(cursor.moveToNext()){
			list.add(AudioItem.fromCursor(cursor));
		}
		return list;
	}
	
	@Override
	protected void processClick(View v) {
		
	}
}
