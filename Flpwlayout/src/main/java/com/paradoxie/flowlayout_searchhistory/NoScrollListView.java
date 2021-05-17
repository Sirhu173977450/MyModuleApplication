package com.paradoxie.flowlayout_searchhistory;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class NoScrollListView extends ListView {
	/**
	 * 嵌套在scrollview 中避免滑动冲突
	 * @param context
     */
	public NoScrollListView(Context context) {
		super(context); 
	} 
	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs); 
	} 
	public NoScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
		int expandSpec = MeasureSpec.makeMeasureSpec(
				Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec); 
	} 
} 
