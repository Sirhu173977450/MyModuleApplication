package com.example.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 号码归属地的数据库data access object
 */
public class AddressDBDao {
	/**
	 * 查询电话号码的归属地信息
	 *
	 * @param number
	 * @return
	 */
	public static String findLocation(String number) {
		String path = "/data/data/com.itheima.mobilesafe/files/address.db";
		String location = "查无此号";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// 判断 number 是不是一个手机号码.
		// 1 [34578] + 9位的数字
		// ^1[34578]\d{9}$
		boolean result = number.matches("^1[34578]\\d{9}$");
		if (result) {//手机号码
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id = (select outkey from data1 where id = ?)",
							new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				location = cursor.getString(0);
			}
			cursor.close();
		}else{//非手机号码
			switch (number.length()) {
				case 3: //110 119 120 999
					location="报警电话";
					break;
				case 4://5556 模拟器
					location = "模拟器";
					break;
				case 5://
					location = "商业客服电话";
					break;
				case 7:
					location = "本地电话";
					break;
				case 8:
					location = "本地电话";
					break;
				//01012345678
				//075512345678
				default:
					if(number.length()>=10&&number.startsWith("0")){
						//长途电话
						Cursor cursor = db.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 3)});
						if(cursor.moveToNext()){
							String temp  = cursor.getString(0);
							location = temp.substring(0, temp.length()-2);//过滤掉电信 移动 或者联通
						}
						cursor.close();
						cursor = db.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 4)});
						if(cursor.moveToNext()){
							String temp  = cursor.getString(0);
							location = temp.substring(0, temp.length()-2);
						}
						cursor.close();
					}
			}
		}
		db.close();
		return location;
	}
}
