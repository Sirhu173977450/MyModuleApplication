package com.cock.cityquickindex.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.cock.cityquickindex.bean.CityNameInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Created by Luhailiang on 2016/10/30 16:08.
 * Email : 18336094752@163.com
 */

public class CityNameDao {
    public static final String DB_NAME = "city_name.db"; //保存的数据库文件名
    public static final String PACKAGE_NAME = "com.cock.cityquickindex";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME + "/files/" + DB_NAME;  //在手机里存放数据库的位置

    /**
     * 获取省市城市的Id
     *
     * @param id
     * @return
     */
    public static List<CityNameInfo> getProvencesOrCityOnId(int id) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
        List<CityNameInfo> cityNameInfos = new ArrayList<CityNameInfo>();//String.valueOf(type)
        Cursor cursor = db.rawQuery("select * from REGIONS where _id=" + id, null);
        while (cursor.moveToNext()) {
            CityNameInfo cityNameInfo = new CityNameInfo();
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            int parent = cursor.getInt(cursor.getColumnIndex("parent"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int type1 = cursor.getInt(cursor.getColumnIndex("type"));
            cityNameInfo.setId(_id);
            cityNameInfo.setParent(parent);
            cityNameInfo.setName(name);
            cityNameInfo.setType(type1);
            cityNameInfos.add(cityNameInfo);
        }
        cursor.close();
        db.close();
        return cityNameInfos;
    }

    /**
     * 获取省市的类型Type
     *
     * @param type
     * @return
     */
    public static List<CityNameInfo> getProvencesOrCity(int type) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
        List<CityNameInfo> cityNameInfos = new ArrayList<CityNameInfo>();//String.valueOf(type)
        Cursor cursor = db.rawQuery("select * from REGIONS where type=" + type, null);
        while (cursor.moveToNext()) {
            CityNameInfo cityNameInfo = new CityNameInfo();
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            int parent = cursor.getInt(cursor.getColumnIndex("parent"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int type1 = cursor.getInt(cursor.getColumnIndex("type"));
            cityNameInfo.setId(_id);
            cityNameInfo.setParent(parent);
            cityNameInfo.setName(name);
            cityNameInfo.setType(type1);
            cityNameInfos.add(cityNameInfo);
        }
        cursor.close();
        db.close();
        return cityNameInfos;
    }

    /**
     * 获取省市的Parent
     *
     * @param parent
     * @return
     */
    public static List<CityNameInfo> getProvencesOrCityOnParent(int parent) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
        List<CityNameInfo> cityNameInfos = new ArrayList<CityNameInfo>();//String.valueOf(type)
        Cursor cursor = db.rawQuery("select * from REGIONS where parent=" + parent, null);
        while (cursor.moveToNext()) {
            CityNameInfo cityNameInfo = new CityNameInfo();
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            int parent1 = cursor.getInt(cursor.getColumnIndex("parent"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            cityNameInfo.setId(_id);
            cityNameInfo.setParent(parent1);
            cityNameInfo.setName(name);
            cityNameInfo.setType(type);
            cityNameInfos.add(cityNameInfo);
        }
        cursor.close();
        db.close();
        return cityNameInfos;
    }


    /**
     * 插入数据库，不用
     */

    public static void insertRegion(SQLiteDatabase db, CityNameInfo ri) {
        ContentValues values = new ContentValues();
        values.put("parent", ri.getParent());
        values.put("name", ri.getName());
        values.put("type", ri.getType());
        db.insert("REGIONS", null, values);
    }

    /**
     * 返回所有的省市信息
     */

    public static List<CityNameInfo> queryAllInfo() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
        List<CityNameInfo> cityNameInfos = new ArrayList<CityNameInfo>();
        Cursor cursor = db.rawQuery("select * from REGIONS", null);
        while (cursor.moveToNext()) {
            CityNameInfo cityNameInfo = new CityNameInfo();
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            int parent = cursor.getInt(cursor.getColumnIndex("parent"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            cityNameInfo.setId(_id);
            cityNameInfo.setParent(parent);
            cityNameInfo.setName(name);
            cityNameInfo.setType(type);
            cityNameInfos.add(cityNameInfo);
        }
        cursor.close();
        db.close();
        return cityNameInfos;
    }

    public static CityNameInfo querySingleRemind(SQLiteDatabase db, int _id) {
        String sql = "select * from remindtable where _id =" + _id;
        Cursor cursor = db.rawQuery(sql, null);
        CityNameInfo remind = null;
        if (cursor.moveToNext()) {
        }
        cursor.close();
        return remind;
    }

    /**
     * 删除CityName
     *
     * @param _id
     * @param db
     */
    public static void deleteCityName(int _id, SQLiteDatabase db) {
        db.execSQL("delete from remindtable where _id = ?",
                new Object[]{_id});
    }
}
