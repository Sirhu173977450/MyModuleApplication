package com.example.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ApplockDBOpenHelper extends SQLiteOpenHelper {

	public ApplockDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}

	//数据库第一次被创建的时候调用,适合初始化数据库的表结构
	@Override
	public void onCreate(SQLiteDatabase db) {
		//id 主键自增长, packname 被加锁应用程序的包名
		db.execSQL("create table lockinfo (_id integer primary key autoincrement, packname varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
