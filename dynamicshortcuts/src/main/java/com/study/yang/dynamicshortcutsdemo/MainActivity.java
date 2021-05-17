package com.study.yang.dynamicshortcutsdemo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.study.yang.dynamicshortcutsdemo.data.DataResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> data = new ArrayList<>();
    private ListView lv;
    private ArrayAdapter<String> mAdapter;
    //管理器
    private ShortcutManager mShortcutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        data.addAll(Arrays.asList(DataResource.contacts));
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        lv.setAdapter(mAdapter);

        addDynamicShortcuts();

        //给ListView添加长按事件进行删除shortcut
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(MainActivity.this, data.get(position) + "陨落了", Toast.LENGTH_SHORT).show();
                removeShortcut(position);
                return false;
            }
        });

        //给ListView添加单击事件进行修改shortcut
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data.add(0, "刘备、张飞、关羽");
                mAdapter.notifyDataSetChanged();
                updateShortcut(position);
            }
        });
    }

    /**
     * 更新Shortcut
     * @param index
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void updateShortcut(int index) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("message", "我和好汉" + mAdapter.getItem(index) + "的切磋");

        ShortcutInfo info = new ShortcutInfo
                //给指定的id重新赋值内容
                .Builder(this, "id" + index)
                .setShortLabel(mAdapter.getItem(index))
                .setLongLabel("水浒好汉:" + mAdapter.getItem(index))
                .setIcon(Icon.createWithResource(this, R.drawable.ic_first))
                //此处可以添加多个intent
                .setIntents(new Intent[]{intent, new Intent("android.intent.action.MAIN",
                        null, this, MainActivity.class)})
                .setDisabledMessage("好汉失效了")
                .build();
        //更新
        mShortcutManager.updateShortcuts(Arrays.asList(info));
    }

    /**
     * 移除Shortcut
     *
     * @param
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void removeShortcut(int index) {
        //获取已有快捷条目
        List<ShortcutInfo> infos = mShortcutManager.getPinnedShortcuts();
        for (ShortcutInfo info : infos) {
            if (info.getId().equals("id" + index)) {
                //使得标签失效
                mShortcutManager.disableShortcuts(Arrays.asList(info.getId()), "好汉陨落了");
            }
        }
        //通过标签id移除指定标签
        mShortcutManager.removeDynamicShortcuts(Arrays.asList("id" + index));
    }

    /**
     * 动态添加Shortcuts应用
     */
    private void addDynamicShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mShortcutManager = (ShortcutManager) getSystemService(SHORTCUT_SERVICE);
            //返回至是4
            int maxShortcutCountPerActivity = mShortcutManager.getMaxShortcutCountPerActivity();
            List<ShortcutInfo> infos = new ArrayList<>();
            for (int i = 0; i < maxShortcutCountPerActivity; i++) {
                Intent intent = new Intent(this, MessageActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("message", "我和好汉" + mAdapter.getItem(i) + "的切磋");

                ShortcutInfo info = new ShortcutInfo
                        //添加shortcuts条目的唯一id
                        .Builder(this, "id" + i)
                        //给条目添加短标签
                        .setShortLabel(mAdapter.getItem(i))
                        //给条目添加长标签
                        .setLongLabel("水浒好汉:" + mAdapter.getItem(i))
                        //给条目设置图标
                        .setIcon(Icon.createWithResource(this, R.drawable.ic_first))
                        //设置点击条目之后要触发的intent
                        .setIntent(intent)
                        .build();
                infos.add(info);
            }

            mShortcutManager.addDynamicShortcuts(infos);
        }
    }
}
