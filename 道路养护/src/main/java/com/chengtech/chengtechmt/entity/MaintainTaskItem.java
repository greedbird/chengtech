package com.chengtech.chengtechmt.entity;

import android.content.Context;

import com.chengtech.chengtechmt.presenter.Presenter;
import com.chengtech.chengtechmt.util.DateUtils;
import com.chengtech.chengtechmt.util.HttpclientUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 作者: LiuFuYingWang on 2016/10/19 10:35.
 */

public class MaintainTaskItem extends BaseModel {

    public Presenter presenter;

    private String routeName;                     //实施路线名称

    private String routePeg;                      //实施范围（路线桩号段）

    private String workContent;                   //主要作业内容

    private Date planStartDate;				      //计划作业开始日期
    private Date planEndDate;				      //计划作业结束日期
    private String workDay;                       //计划作业工期
    private Date startDate;			          	  //实际开始日期
    private Date finishDate;			          //实际完工日期

    private String inOutPlanType;                 //计划内or计划外；0计划内，1计划外

    private String registOrAcceptanceStatus;      //状态 0/""：表示未实施，1：未验收，2：已完成

    //工程项目类型  只用来显示
    private String projectType;                   //工程项目类型

    private String workTeam;                      //作业班组

    //2016.11.15
    private String floatRoutePeg;				  //实施范围(带小数点),比如k0013+020,浮点数为13.020

    public MaintainTaskItem(Presenter presenter){
        this.presenter = presenter;
    }

    public List<String> getContent(){
        List<String> list = new ArrayList<>();
        list.add(registOrAcceptanceStatus.equals("0")?"未核定":"已核定");
        list.add(workTeam==null?"":workTeam);
        list.add(routeName==null?"":routeName);
        list.add(routePeg==null?"":routePeg);
        list.add(workContent==null?"":workContent);
        list.add(planStartDate==null?"": DateUtils.convertDate2(planStartDate));
        list.add(planEndDate==null?"": DateUtils.convertDate2(planEndDate));
        list.add(workDay==null?"": workDay);
        list.add(startDate==null?"": DateUtils.convertDate2(startDate));
        list.add(finishDate==null?"": DateUtils.convertDate2(finishDate));
        return list;
    }

    public List<String> getTitles(){
        List<String> title = new ArrayList<>();
        title.add("状态");
        title.add("作业班组");
        title.add("实施路线");
        title.add("实施范围");
        title.add("主要作业内容");
        title.add("计划开始日期");
        title.add("计划结束日期");
        title.add("工期（天）");
        title.add("实际开始日期");
        title.add("实际结束日期");
        title.add("主要内容");
        return title;
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
                    MaintainTaskItem.MaintainTaskItemG m = gson.fromJson(data,MaintainTaskItem.MaintainTaskItemG.class);
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
        client.get(url+"&pager.pageNo="+pageNo,
                responseHandler);
    }

    public class MaintainTaskItemG{
        List<MaintainTaskItem> rows;
    }
}
