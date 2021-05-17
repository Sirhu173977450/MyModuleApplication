package cc.duduhuo.timelinerecyclerview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.timelinerecyclerview.adapter.ProjectProgressAdapter;
import cc.duduhuo.timelinerecyclerview.model.Trace;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2016/12/7 20:25
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class TraceActivity extends AppCompatActivity {

    private RecyclerView rvTrace;
    private List<Trace> traceList = new ArrayList<>(10);
    private List<String> images2 = new ArrayList<String>(){{
        add("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg");
        add("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg");
        add("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg");
        add("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg");
        add("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg");
        add("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg");
        add("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg");}};
    private ProjectProgressAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);
        rvTrace = (RecyclerView) findViewById(R.id.rvTrace);
        initData();
    }

    private void initData() {
        // 模拟一些假的数据
        traceList.add(new Trace("0", "[沈阳市] [沈阳和平五部]的派件已签收 感谢使用中通快递,期待再次为您服务!",images2));
        traceList.add(new Trace("0", "[沈阳市] [沈阳和平五部]的东北大学代理点正在派件 电话:18040xxxxxx 请保持电话畅通、耐心等待",images2));
        traceList.add(new Trace("4", "[沈阳市] 快件到达 [沈阳和平五部]",images2));
        traceList.add(new Trace("7", "[沈阳市] 快件离开 [沈阳中转]已发往[沈阳和平五部]",images2));
        traceList.add(new Trace("4", "[沈阳市] 快件到达 [沈阳中转]",images2));
        traceList.add(new Trace("2", "[嘉兴市] 快件离开 [杭州中转部]已发往[沈阳中转]",images2));
        traceList.add(new Trace("6", "[杭州市] 快件到达 [杭州汽运部]",images2));
        traceList.add(new Trace("1", "[杭州市] 快件离开 [杭州乔司区]已发往[沈阳]",images2));
        traceList.add(new Trace("2", "[杭州市] [杭州乔司区]的市场部已收件 电话:18358xxxxxx",images2));
        adapter = new ProjectProgressAdapter(this, traceList,R.layout.item_product_progress);
        rvTrace.setLayoutManager(new LinearLayoutManager(this));
        rvTrace.setAdapter(adapter);
    }
}
