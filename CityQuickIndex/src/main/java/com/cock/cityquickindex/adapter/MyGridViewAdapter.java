package com.cock.cityquickindex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cock.cityquickindex.R;
import com.cock.cityquickindex.bean.CityNameInfo;

import java.util.List;

/**
 * Author : Created by Luhailiang on 2016/10/30 16:57.
 * Email : 18336094752@163.com
 * 热门城市适配器
 */

public class MyGridViewAdapter extends MyBaseAdapter {

    private LayoutInflater inflater;
    private List<CityNameInfo> list;

    public MyGridViewAdapter(Context ct, List<CityNameInfo> list) {
        super(ct, list);
        this.list = list;
        inflater = LayoutInflater.from(ct);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_hotcity_city, null);
            holder.id_tv_cityname = (TextView) convertView.findViewById(R.id.id_tv_cityname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CityNameInfo info = list.get(position);
        holder.id_tv_cityname.setText(info.getName());
        return convertView;
    }

    class ViewHolder {
        TextView id_tv_cityname;
    }
}
