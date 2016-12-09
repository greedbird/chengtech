package com.chengtech.chengtechmt.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.entity.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Line;

import static com.chengtech.chengtechmt.R.id.add;
import static com.chengtech.chengtechmt.R.id.hideLayout;

/**
 * 作者: LiuFuYingWang on 2016/6/22 14:50.
 */
public class RecycleViewAdapter2 extends RecyclerView.Adapter<RecycleViewAdapter2.MyViewHolder> {
    private Context mContext;
    private List<String[]> data;
    private LayoutInflater inflater;
    public OnItemClickListener onItemClickListener;
    public int item_res;
    public boolean isShow = false; //是否显示下来箭头

    public int hideLayoutHeight;
    public Map<MyViewHolder,List<TextView>> holderMap;
    public List<String []> planData;


    public RecycleViewAdapter2(Context context, List<String[]> data, int item_res) {
        this.mContext = context;
        this.data = data;
        this.item_res = item_res;
        inflater = LayoutInflater.from(context);
        holderMap = new HashMap<>();
        float denisty = context.getResources().getDisplayMetrics().density;
        hideLayoutHeight = (int) (denisty*80+0.5);

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(item_res, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String[] titleArray = data.get(position);
        List<TextView> tvs = holderMap.get(holder);
        for (int i=0;i<titleArray.length;i++) {
            tvs.get(i).setText(titleArray[i]);
            tvs.get(i).setVisibility(View.VISIBLE);
        }

        if (isShow && planData!=null) {
            //添加下拉后的内容的布局文件
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View contentView = inflater.inflate(R.layout.item_planinside,null,false);
            LinearLayout planLayout = (LinearLayout) contentView.findViewById(R.id.plan_layout);
            TextView plan_tv = (TextView) contentView.findViewById(R.id.plan_tv);
            plan_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.setOnItemClickListener(v, position);
                    }
                }
            });
            TextView add_tv = (TextView) contentView.findViewById(R.id.add_tv);
            add_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.setOnItemClickListener(v, position);
                    }
                }
            });
            LinearLayout addLayout = (LinearLayout) contentView.findViewById(R.id.add_layout);
            for (int i =0;i<6;i++) {
                TextView textView = new TextView(mContext);
                textView.setTextSize(11);
                textView.setTextColor(Color.parseColor("#ffffff"));
                textView.setText(planData.get(position)[i]);
                if (i<3) {
                    planLayout.addView(textView);
                }else {
                    addLayout.addView(textView);
                }
            }

            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            contentView.setLayoutParams(lp);
            holder.hideLayout.addView(contentView);



            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.hideLayout.getVisibility() == View.GONE) {
                        holder.hideLayout.setVisibility(View.VISIBLE);
                        ValueAnimator anima = createDropAnimator(holder.hideLayout,0,hideLayoutHeight);
                        anima.start();
                        RotateAnimation animation = new RotateAnimation(0, 180,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                0.5f);
                        animation.setFillAfter(true);
                        animation.setDuration(100);
                        v.startAnimation(animation);
                    }else {
                        int height = holder.hideLayout.getHeight();
                        ValueAnimator anima = createDropAnimator(holder.hideLayout,height,0);
                        anima.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.hideLayout.setVisibility(View.GONE);
                            }
                        });
                        anima.start();

                        RotateAnimation animation = new RotateAnimation(180, 0,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                0.5f);
                        animation.setFillAfter(true);
                        animation.setDuration(100);
                        v.startAnimation(animation);
                    }
                }
            });
        }

        holder.viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.setOnItemClickListener(v, position);
                }
            }
        });
    }

    public void setPlanData(List<String []> data) {
        planData = data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        public void setOnItemClickListener(View view, int position);

        public void setOnItemLongClickListener(View view, int position);
    }

    public void showMoreFlag(boolean isShow) {
        this.isShow = isShow;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv1, tv2, tv3, tv4, tv5, tv6,tv7;
        public View viewGroup;
        public ImageView imageView;
        public LinearLayout hideLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.viewGroup = itemView;
            tv1 = (TextView) itemView.findViewById(R.id.title);
            tv2 = (TextView) itemView.findViewById(R.id.subtitle);
            tv3 = (TextView) itemView.findViewById(R.id.thirdtitle);
            tv4 = (TextView) itemView.findViewById(R.id.fourtitle);
            tv5 = (TextView) itemView.findViewById(R.id.fivetitle);
            tv6 = (TextView) itemView.findViewById(R.id.sixtitle);
            tv7 = (TextView) itemView.findViewById(R.id.seventitle);
            imageView = (ImageView) itemView.findViewById(R.id.down_arraow);
            hideLayout = (LinearLayout) itemView.findViewById(R.id.hideLayout);
            //对list<TextView> 进行添加
            List<TextView> tvs = new ArrayList<>();
            tvs.add(tv1);
            tvs.add(tv2);
            tvs.add(tv3);
            tvs.add(tv4);
            tvs.add(tv5);
            tvs.add(tv6);
            tvs.add(tv7);

            holderMap.put(this,tvs);
        }
    }

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);

            }
        });
        return animator;
    }

}



