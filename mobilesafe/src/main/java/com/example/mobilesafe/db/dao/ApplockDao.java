package com.example.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.mobilesafe.db.ApplockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * 程序锁的dao
 */
public class ApplockDao {
	private ApplockDBOpenHelper helper;
	private Context context;

	/**
	 * dao的构造方法
	 *
	 * @param context
	 *            上下文
	 */
	public ApplockDao(Context context) {
		helper = new ApplockDBOpenHelper(context);
		this.context = context;
	}

	/**
	 * 添加一条要锁定的应用程序信息
	 *
	 * @param packname
	 *            包名
	 */
	public void add(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("lockinfo", null, values);
		db.close();
		// 发送一个通知，通知内容观察者某个路径的数据变化了。
		Uri uri = Uri.parse("content://com.itheima.mobilesafe.applockdb");
		context.getContentResolver().notifyChange(uri, null);
	}

	/**
	 * 删除一条要锁定的应用程序信息
	 *
	 * @param packname
	 *            包名
	 */
	public void delete(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("lockinfo", "packname=?", new String[] { packname });
		db.close();
		// 发送一个通知，通知内容观察者某个路径的数据变化了。
		Uri uri = Uri.parse("content://com.itheima.mobilesafe.applockdb");
		context.getContentResolver().notifyChange(uri, null);
	}

	/**
	 * 查询一个包名是否要被锁定
	 *
	 * @param packname
	 * @return
	 */
	public boolean find(String packname) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from lockinfo where packname=?",
				new String[] { packname });
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 获取所有的锁定的包名
	 *
	 * @return
	 */
	public List<String> findAll() {
		List<String> lockedPacknames = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select packname from lockinfo", null);
		while (cursor.moveToNext()) {
			lockedPacknames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return lockedPacknames;
	}

}
