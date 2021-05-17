package com.example.mobilemedia.util;

import android.database.Cursor;

public class CursorUtil {
	private static String tag = CursorUtil.class.getSimpleName();
	/**
	 * 打印cursor
	 * @param cursor
	 */
	public static void printCursor(Cursor cursor){
		if(cursor==null) return;
		LogUtil.e(tag, "共"+cursor.getCount()+"条记录");
		while (cursor.moveToNext()) {
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				String columnName = cursor.getColumnName(i);
				String columnValue = cursor.getString(i);
				LogUtil.e(tag, columnName+" : "+columnValue);
			}
			LogUtil.e(tag, "====================");
		}
	}
}
