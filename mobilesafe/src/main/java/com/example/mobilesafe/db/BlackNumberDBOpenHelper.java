package com.example.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	public BlackNumberDBOpenHelper(Context context) {
		super(context, "itheima.db", null, 1);
	}
	//数据库第一次被创建的时候调用,适合初始化数据库的表结构
	@Override
	public void onCreate(SQLiteDatabase db) {
		//id 主键自增长, phone 电话号码 mode 拦截模式
		db.execSQL("create table blacknumber (_id integer primary key autoincrement, phone varchar(20),mode varchar(2))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
