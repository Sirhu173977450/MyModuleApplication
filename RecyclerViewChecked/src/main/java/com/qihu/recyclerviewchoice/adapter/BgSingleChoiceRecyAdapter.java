package com.qihu.recyclerviewchoice.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qihu.recyclerviewchoice.R;
import com.qihu.recyclerviewchoice.bean.Person;
import com.qihu.recyclerviewchoice.view.ChoiceItemLayout;

import java.util.List;

/**
 * Created by cui on 2017/3/4.
 */

public class BgSingleChoiceRecyAdapter extends RecyclerView.Adapter {

    public List<Person> list;

    public BgSingleChoiceRecyAdapter(List<Person> list) {
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bg_choice, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        MyHolder holder = (MyHolder) viewHolder;
        Person person = list.get(position);

        holder.tv1.setText("姓名：" + person.getName());
        holder.tv2.setText("年龄：" + person.getAge());

        ChoiceItemLayout layout = (com.qihu.recyclerviewchoice.view.ChoiceItemLayout) holder.itemView;
        layout.setChecked(person.isChecked());

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv1;
        public TextView tv2;

        public MyHolder(View itemView) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.tv1);
            tv2 = (TextView) itemView.findViewById(R.id.tv2);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
