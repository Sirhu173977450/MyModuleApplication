package me.wcy.express.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.wcy.express.R;
import me.wcy.express.database.History;
import me.wcy.express.utils.DataManager;
import me.wcy.express.utils.binding.Bind;
import me.wcy.express.viewholder.HistoryViewHolder;
import me.wcy.express.widget.radapter.RAdapter;
import me.wcy.express.widget.radapter.RSingleDelegate;

public class HistoryActivity extends BaseActivity {
    @Bind(R.id.rv_history_list)
    private RecyclerView rvHistoryList;
    @Bind(R.id.tv_empty)
    private TextView tvEmpty;

    private List<History> historyList = new ArrayList<>();
    private RAdapter<History> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        adapter = new RAdapter<>(historyList, new RSingleDelegate<>(HistoryViewHolder.class));
        rvHistoryList.setLayoutManager(new LinearLayoutManager(this));
        rvHistoryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvHistoryList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<History> historyList = DataManager.getInstance().getHistoryList();
        this.historyList.clear();
        this.historyList.addAll(historyList);
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(this.historyList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
