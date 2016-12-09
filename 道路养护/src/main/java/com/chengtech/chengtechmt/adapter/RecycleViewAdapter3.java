package com.chengtech.chengtechmt.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.entity.Menu;

import java.util.List;

/**
 * 作者: LiuFuYingWang on 2016/6/22 14:50.
 */
public class RecycleViewAdapter3 extends RecyclerView.Adapter<RecycleViewAdapter3.MyViewHolder> {
    private List<String> subTitle;
    private List<String> content;
    private LayoutInflater inflater;
    public OnItemClickListener onItemClickListener;
    public int item_res;


    public RecycleViewAdapter3(Context context, List<String>  subtitle, List<String> content, int item_res) {
        this.subTitle = subtitle;
        this.item_res = item_res;
        this.content = content;
        inflater = LayoutInflater.from(context);

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(item_res,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return subTitle==null?0:subTitle.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

            holder.tv.setText(subTitle.get(position));
            holder.et.setText(content.get(position));


        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.setOnItemClickListener(v, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.setOnItemLongClickListener(v,position);
                    return true;
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void setOnItemClickListener(View view, int position);

        public void setOnItemLongClickListener(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        EditText et;
        TextView tv;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            et = (EditText) itemView.findViewById(R.id.et1);
            tv = (TextView) itemView.findViewById(R.id.tv1);
        }
    }

}



