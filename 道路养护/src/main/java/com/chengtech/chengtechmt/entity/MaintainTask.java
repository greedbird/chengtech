package com.chengtech.chengtechmt.entity;

import android.content.Context;

import com.chengtech.chengtechmt.presenter.Presenter;
import com.chengtech.chengtechmt.util.HttpclientUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.List;

/**
 * 作者: LiuFuYingWang on 2016/10/11 15:16.
 */

public class MaintainTask  {

    public String id;
    private Presenter presenter;

    public String workYear;                 //年份
    public String workMonth;				 //月份
    public String secondDeptName;           //养护所名称
    public String thirdDeptName;            //养护单位名称
    public String taskMark;				 //作业状态 0：保洁作业，1：小修作业

    //不存数据库，只用来显示
    public int planInsideTaskCount;         //计划内任务量
    public int planInsideNotImpleCount;     //计划内未实施量
    public int planInsideImpleCount;        //计划内已实施量
    public int planInsideAcceptCount;       //计划内验收量
    public int planOutsideTaskCount;        //计划外任务量
    public int planOutsideNotImpleCount;    //计划外未实施量
    public int planOutsideImpleCount;       //计划外已实施量
    public int planOutsideAcceptCount;      //计划外验收量

    public MaintainTask (Presenter presenter) {
        this.presenter = presenter;
    }

    public void getData(Context context, String url, final String type, int pageNo) {
        AsyncHttpClient client = HttpclientUtil.getInstance(context);

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                try {
                    String data = new String(arg2, "utf-8");
                    Gson gson  = new Gson();
                    MaintainTaskG m = gson.fromJson(data,MaintainTaskG.class);
                    presenter.loadDataSuccess(m.rows,type);
                } catch (Exception e) {
                    presenter.loadDataFailed();
                    e.printStackTrace();
                }
                super.onSuccess(arg0, arg1, arg2);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                presenter.loadDataFailed();
                super.onFailure(arg0, arg1, arg2, arg3);
            }
        };
        client.get(url+"&pager.pageNo="+pageNo+"&taskMark="+(type.contains("Task")?"0":"1"),
                responseHandler);
    }

    public class MaintainTaskG{
        public List<MaintainTask> rows;
    }
}
