package com.chengtech.chengtechmt.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.adapter.HorizotalScrollAdapter;
import com.chengtech.chengtechmt.adapter.RecycleViewAdapter;
import com.chengtech.chengtechmt.divider.RecycleViewDivider;
import com.chengtech.chengtechmt.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者: LiuFuYingWang on 2016/11/15 9:05.
 */

public class MyHorizontalScrollView2 extends LinearLayout {
    private Context mContext;
    public RecyclerView nameRV, contentRV;
    private MyHorizontalScrollView titleSV, contentSV;
    private LinearLayout titleLayout;
    private int DEFAULT_WIDTH = 60;
    private int DEFAULT_HEIGHT = 40;
    private List<MyHorizontalScrollView> scrollViewList;
    private boolean isLeftListEnable,isRightListEnable;
    private SparseIntArray sparseIntArray = new SparseIntArray();
    private onItemClickListener onItemClickListener;

    public interface onItemClickListener{
        public void onClick(View view,int position);
    }


    public MyHorizontalScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_horizontal_scroll, this);
        nameRV = (RecyclerView) view.findViewById(R.id.nameRv);
        contentRV = (RecyclerView) view.findViewById(R.id.contentRv);
        titleSV = (MyHorizontalScrollView) view.findViewById(R.id.titleScrollView);
        contentSV = (MyHorizontalScrollView) view.findViewById(R.id.contentSV);
        titleLayout = (LinearLayout) view.findViewById(R.id.titleLayout);
        scrollViewList = new ArrayList<>();
        scrollViewList.add(titleSV);
        scrollViewList.add(contentSV);
        combination();
    }

    private void combination() {
        titleSV.SetOnMyScrollViewListener(new MyHorizontalScrollView.OnMyScrollViewListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                for (MyHorizontalScrollView scrollView : scrollViewList) {
                    scrollView.smoothScrollTo(l, t);
                }
            }
        });
        contentSV.SetOnMyScrollViewListener(new MyHorizontalScrollView.OnMyScrollViewListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                for (MyHorizontalScrollView scrollView : scrollViewList) {
                    scrollView.smoothScrollTo(l, t);
                }
            }
        });


        nameRV.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return true;
                    default:
                        break;
                }
                return true;
            }
        });
        nameRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            //防止死循环，因为双方都设置了对方进行滑动
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isRightListEnable = false;
                    isLeftListEnable = true;
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isRightListEnable = true;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLeftListEnable)
                    contentRV.scrollBy(dx,dy);
            }
        });

        contentRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCREEN_STATE_ON){
                    isLeftListEnable = false;
                    isRightListEnable = true;
                }else if (newState==RecyclerView.SCROLL_STATE_IDLE) {
                    isLeftListEnable = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isRightListEnable)
                    nameRV.scrollBy(dx,dy);

                int visiableChildCount = recyclerView.getChildCount();
                for (int i=0;i<visiableChildCount;i++) {
                    View view = recyclerView.getChildAt(i);
                    int measuredHeight = view.getMeasuredHeight();
                    View childAt = nameRV.getChildAt(i);
                    if (childAt==null) {
                        Log.i("tag",i+"");
                        break;
                    }
                    ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
//                    if (layoutParams.height !=measuredHeight) {
                        layoutParams.height = measuredHeight;
                        childAt.setLayoutParams(layoutParams);
//                    }

                }
            }
        });

    }

    /**
     * 设置标题
     *
     * @param titles
     */
    public void setTitle(List<String> titles) {
        LinearLayout layout = (LinearLayout) titleSV.getChildAt(0);
        if (titles != null && titles.size() > 0) {
            for (int i = 0; i < titles.size(); i++) {
                if (i == 0) {
                    TextView tv1 = (TextView) titleLayout.getChildAt(0);
                    tv1.setText(titles.get(i));
                } else {
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                            CommonUtils.dp2px(mContext, sparseIntArray.get(i)==0?DEFAULT_WIDTH:sparseIntArray.get(i)), ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    TextView textView = new TextView(mContext);
                    textView.setLayoutParams(lp);
                    textView.setGravity(Gravity.CENTER);
                    textView.setText(titles.get(i));
                    textView.setTextColor(Color.WHITE);
//                    int padding = CommonUtils.dp2px(mContext,5);
//                    textView.setPadding(padding,padding,padding,padding);
                    layout.addView(textView);
                }
            }
        }
    }

    public void setData(List<List<String>> data) {
        if (data != null && data.size() > 0)
            setTitle(data.get(0));
        if (data != null && data.size() > 1) {
            //去掉标题栏的list
            data.remove(0);
            setTitleAdapter(data);
            setContentAdapter(data);
        }
    }

    private void setContentAdapter(final List<List<String>> data) {
        contentRV.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setOrientation(HORIZONTAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
//                        CommonUtils.dp2px(mContext, DEFAULT_HEIGHT)
                );
                linearLayout.setLayoutParams(lp);
                linearLayout.setMinimumHeight(CommonUtils.dp2px(mContext,40));
                RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(linearLayout) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                List<String> content = data.get(position);
                LinearLayout container = (LinearLayout) holder.itemView;
                if (position % 2==0) {
                    container.setBackgroundColor(Color.WHITE);
                }else {
                    container.setBackgroundColor(Color.parseColor("#F0F0F0"));
                }
                for (int i = 1; i < content.size(); i++) {
                    if (container.getChildAt(i) != null) {
                        TextView title = (TextView) container.getChildAt(i - 1);
                        title.setText(content.get(i));
                    } else {
                        final TextView title = new TextView(mContext);
                        ViewGroup.LayoutParams lp = new RecyclerView.LayoutParams(
                                CommonUtils.dp2px(mContext, sparseIntArray.get(i)==0?DEFAULT_WIDTH:sparseIntArray.get(i))
                                , ViewGroup.LayoutParams.MATCH_PARENT);
                        title.setGravity(Gravity.CENTER);
                        title.setLayoutParams(lp);
//                        int padding = CommonUtils.dp2px(mContext,5);
//                        title.setPadding(padding,padding,padding,padding);
                        title.setText(content.get(i));
//                        if (i==2 || i==3) {
//                            title.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Toast.makeText(mContext, title.getText().toString(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
                        container.addView(title);

                    }
                }

                container.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener!=null)
                            onItemClickListener.onClick(v,position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return data.size();
            }
        });
        contentRV.setLayoutManager(new LinearLayoutManager(mContext));
    }


    /**
     * 设置固定列的recycleview
     * @param data
     */
    private void setTitleAdapter(final List<List<String>> data) {
        nameRV.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextView textView = new TextView(mContext);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        CommonUtils.dp2px(mContext, DEFAULT_WIDTH), CommonUtils.dp2px(mContext, DEFAULT_HEIGHT)
                );
                textView.setLayoutParams(lp);
//                int padding = CommonUtils.dp2px(mContext,5);
//                textView.setPadding(padding,padding,padding,padding);
                textView.setGravity(Gravity.CENTER);
                RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(textView) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText(data.get(position).get(0));
                if (position % 2==0) {
                    ((TextView) holder.itemView).setBackgroundColor(Color.WHITE);
                }else {
                    ((TextView) holder.itemView).setBackgroundColor(Color.parseColor("#F0F0F0"));
                }
            }

            @Override
            public int getItemCount() {
                return data.size();
            }
        });
        nameRV.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public void setRectWidthAndHeight(SparseIntArray sparseIntArray ){
        this.sparseIntArray = sparseIntArray;
    }

    public void setOnItemClickListener(MyHorizontalScrollView2.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
