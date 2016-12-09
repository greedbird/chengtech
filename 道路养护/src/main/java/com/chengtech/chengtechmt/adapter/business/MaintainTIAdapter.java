package com.chengtech.chengtechmt.adapter.business;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.entity.MaintainTaskItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: LiuFuYingWang on 2016/10/19 11:10.
 */

public class MaintainTIAdapter extends RecyclerView.Adapter<MaintainTIAdapter.MyViewHolder> {

    private List<MaintainTaskItem> data;
    private List<String> content;
    private Context context;
    private String[][] data2;
    private int column;
    private onClickListener listener;

    public MaintainTIAdapter(List<MaintainTaskItem> data, Context context) {
        this.context = context;
        this.data = data;
        content = new ArrayList<>();
        content.add("状态");
        content.add("实施路线");
        content.add("计划开始日期");
        content.add("计划结束日期");
        content.add("工期（天）");
        content.add("实际开始日期");
        content.add("实际结束日期");
        content.add("主要内容");
        if (data != null && data.size() > 0) {
            for (MaintainTaskItem item : data) {
                content.addAll(item.getContent());
                content.add("清单");
            }
        }
        column = content.size() / (data.size() + 1);
        data2 = new String[data.size() + 1][column];
        for (int i = 0; i < data.size() + 1; i++) {
            for (int j = 0; j < column; j++) {
                data2[i][j] = content.get(i * column + j);
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maintaintaskitem, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //标题栏的字体和颜色不同,经过测算，如果利用二维数据来显示列表，因为横向的recycleview的数据排列方向是从上到下的，
        //所以当前position位置的数据对应着二维数组中的data2[position除以行数的余数][position除以行数的商]
        if (position % data2.length == 0) {
            holder.textView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_bright));
            holder.textView.setTextSize(16);
        } else {
            holder.textView.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.textView.setTextSize(12);
        }
        //最后一列是可点击的
        if (content.size()-data2.length<position && position<content.size()) {
            holder.textView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_bright));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null)
                        listener.onClick(v,position);
                }
            });
        }
        int x = position % data2.length;
        int y = position / data2.length;
        holder.textView.setText(data2[x][y]);
    }

    @Override
    public int getItemCount() {
        return content == null ? 0 : content.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textview);
        }
    }

    public void setOnClickListener(onClickListener listener) {
        this.listener = listener;
    }
    public interface onClickListener{
        public void onClick(View view ,int position);
    }
}
