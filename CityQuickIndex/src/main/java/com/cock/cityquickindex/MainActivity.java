package com.cock.cityquickindex;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cock.cityquickindex.adapter.MyGridViewAdapter;
import com.cock.cityquickindex.adapter.SortAdapter;
import com.cock.cityquickindex.bean.CityNameInfo;
import com.cock.cityquickindex.bean.SortModel;
import com.cock.cityquickindex.db.CityNameDao;
import com.cock.cityquickindex.utils.CharacterParser;
import com.cock.cityquickindex.utils.ClearEditText;
import com.cock.cityquickindex.utils.PinyinComparator;
import com.cock.cityquickindex.widget.SideBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout iv_left;
    private GridView mGrideView;
    private List<CityNameInfo> provinceList;
    private ArrayList<CityNameInfo> citysList;
    private ArrayList<CityNameInfo> mHotCity;
    private ArrayList<String> provinces;
    private MyGridViewAdapter gvAdapter;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private SideBar sideBar;
    private TextView dialog;
    private ListView sortListView;
    private List<SortModel> SourceDateList;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        copyDBToLocal("city_name.db");
        initData();
        initView();
    }

    private void initData() {
        provinceList = CityNameDao.getProvencesOrCity(1);
        provinceList.addAll(CityNameDao.getProvencesOrCity(2));
        citysList = new ArrayList<>();
        mHotCity = new ArrayList<>();
        provinces = new ArrayList<>();
        for (CityNameInfo info : provinceList) {
            provinces.add(info.getName().trim());
        }
        mHotCity.add(new CityNameInfo(2, 1, "北京"));
        mHotCity.add(new CityNameInfo(25, 1, "上海"));
        mHotCity.add(new CityNameInfo(76, 6, "广州"));
        mHotCity.add(new CityNameInfo(77, 6, "深圳"));
        mHotCity.add(new CityNameInfo(383, 31, "杭州"));
        mHotCity.add(new CityNameInfo(180, 13, "武汉"));
        mHotCity.add(new CityNameInfo(32, 1, "重庆"));
        mHotCity.add(new CityNameInfo(197, 14, "长沙"));
        mHotCity.add(new CityNameInfo(343, 1, "天津"));
    }

    private void initView() {
        iv_left = (RelativeLayout) findViewById(R.id.iv_left);
        View view = View.inflate(this, R.layout.head_city_list, null);
        mGrideView = (GridView) view.findViewById(R.id.id_gv_remen);
        gvAdapter = new MyGridViewAdapter(this, mHotCity);
        mGrideView.setAdapter(gvAdapter);
        mGrideView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.addHeaderView(view);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position - 1)).getName(),
                        Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra("cityName", ((SortModel) adapter.getItem(position - 1)).getName());
                setResult(100, data);
                finish();
            }
        });
        SourceDateList = filledData(provinceList);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);

        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });
        mGrideView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String cityName = mHotCity.get(position).getName();
                Toast.makeText(getApplication(), cityName, Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra("cityName", cityName);
                setResult(100, data);
                finish();
            }
        });
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(List<CityNameInfo> date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i).getName());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            if (!provinces.contains(filterStr)) {
                filterDateList.clear();
                for (SortModel sortModel : SourceDateList) {
                    String name = sortModel.getName();
                    if (name.indexOf(filterStr.toString()) != -1 ||
                            characterParser.getSelling(name).startsWith(filterStr.toString())) {
                        filterDateList.add(sortModel);
                    }
                }
            } else {
                filterDateList.clear();
                for (int i = 0; i < provinceList.size(); i++) {
                    String name = provinceList.get(i).getName();
                    if (name.equals(filterStr)) {
                        filterDateList.addAll(filledData(
                                CityNameDao.getProvencesOrCityOnParent(provinceList.get(i).getId())));
                    }
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    /**
     * 拷贝数据库文件到本地
     *
     * @param fileName
     */
    private void copyDBToLocal(final String fileName) {

        // getFilesDir() /data/data/包名/files
        File dbFile = new File(getFilesDir(), fileName);
        if (!dbFile.exists()
                || (dbFile.exists() && dbFile.length() == 0)) {

            AssetManager assets = getAssets();
            try {
                InputStream is = assets.open(fileName);
                FileOutputStream fos = openFileOutput(fileName, 0);
                byte[] b = new byte[1024];
                int len = -1;
                while ((len = is.read(b)) != -1) {
                    fos.write(b, 0, len);
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
