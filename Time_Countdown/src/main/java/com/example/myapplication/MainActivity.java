package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        List<TimerItem> timerItems = TimerItemUtil.getTimerItemList();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mAdapter = new MyAdapter(MainActivity.this, timerItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    //适配器
    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<TimerItem> mDatas;
        //用于退出activity,避免countdown，造成资源浪费。
        private SparseArray<CountDownTimer> countDownMap;

        public MyAdapter(Context context, List<TimerItem> datas) {
            mDatas = datas;
            countDownMap = new SparseArray<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_common, parent, false);
            return new ViewHolder(view);
        }

        /**
         * 清空资源
         */
        public void cancelAllTimers() {
            if (countDownMap == null) {
                return;
            }
            Log.e("TAG",  "size :  " + countDownMap.size());
            for (int i = 0,length = countDownMap.size(); i < length; i++) {
                CountDownTimer cdt = countDownMap.get(countDownMap.keyAt(i));
                if (cdt != null) {
                    cdt.cancel();
                }
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final TimerItem data = mDatas.get(position);
            holder.statusTv.setText(data.name);
            //第一步：将前一个缓存清除
            if (holder.countDownTimer != null) {
                holder.countDownTimer.cancel();
            }
            //第二步：格式化时间
            long time = data.expirationTime;
            time = time - System.currentTimeMillis();
            //第三步：设置时间
            if (time > 0) {
                holder.countDownTimer = new CountDownTimer(time, 1000) {
                    public void onTick(long millisUntilFinished) {
                        holder.timeTv.setText(TimeTools.getCountTimeByLong(millisUntilFinished));
                        Log.e("TAG", data.name + " :  " + millisUntilFinished);
                    }
                    public void onFinish() {
                        holder.timeTv.setText("00:00:00");
                        holder.statusTv.setText(data.name + ":结束");
                    }
                }.start();
                //加入到时间集合中保存
                countDownMap.put(holder.timeTv.hashCode(), holder.countDownTimer);
            } else {
                holder.timeTv.setText("00:00:00");
                holder.statusTv.setText(data.name + ":结束");
            }

        }

        @Override
        public int getItemCount() {
            if (mDatas != null && !mDatas.isEmpty()) {
                return mDatas.size();
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView statusTv;
            public TextView timeTv;
            public CountDownTimer countDownTimer;

            public ViewHolder(View itemView) {
                super(itemView);
                statusTv = (TextView) itemView.findViewById(R.id.tv_status);
                timeTv = (TextView) itemView.findViewById(R.id.tv_time);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cancelAllTimers();
        }
    }
}
