package com.chengtech.chengtechmt.entity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.chengtech.chengtechmt.entity.gson.RouteG;
import com.chengtech.chengtechmt.entity.gson.SectionG;
import com.chengtech.chengtechmt.presenter.WorkSectionPresenter;
import com.chengtech.chengtechmt.util.HttpclientUtil;
import com.chengtech.chengtechmt.util.MyConstants;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者: LiuFuYingWang on 2016/5/16 14:25.
 */
public class Section extends BaseModel {

    public WorkSectionPresenter presenter;
    public List<String> propetryValues;
    public int totalRows;
    public String url = MyConstants.PRE_URL+"mt/dbm/road/section/listSectionJson.action";


    public String  techGrade;         //技术等级	
    public String  startStake;        //里程起点	
    public String  endStake;          //里程终点	
    public Dept    mgDept;            //管理单位
    public Dept    mtDept;            //养护单位
    public String	length;            //长度
    public String	driveType;         //车道类型	
    public String	discountedMileage; //折算里程
    public String	rampMileage	;      //匝道连接线里程
    public String	roadWide;          //路面均宽
    public String	regionCode;        //政区编码	
    public String	roadType;          //路面类型

    
    @Override
    public List<String> getContent() {
        if (propetryValues==null) {
            propetryValues = new ArrayList<>();
            propetryValues.add(TextUtils.isEmpty(code)?"":code);
            propetryValues.add(TextUtils.isEmpty(name)?"":name);
            propetryValues.add(TextUtils.isEmpty(techGrade)?"":techGrade);
            propetryValues.add(TextUtils.isEmpty(startStake)?"":startStake);
            propetryValues.add(TextUtils.isEmpty(endStake)?"":endStake);
            propetryValues.add(TextUtils.isEmpty(mgDept.name)?"":mgDept.name);
            propetryValues.add(TextUtils.isEmpty(mtDept.name)?"":mtDept.name);
            propetryValues.add(TextUtils.isEmpty(length)?"":length);
            propetryValues.add(TextUtils.isEmpty(driveType)?"":driveType);
            propetryValues.add(TextUtils.isEmpty(discountedMileage)?"":discountedMileage);
            propetryValues.add(TextUtils.isEmpty(rampMileage)?"":rampMileage);
            propetryValues.add(TextUtils.isEmpty(roadWide)?"":roadWide);
            propetryValues.add(TextUtils.isEmpty(regionCode)?"":regionCode);
            propetryValues.add(TextUtils.isEmpty(roadType)?"":roadType);
        }
        return propetryValues;
    }

    public Section(WorkSectionPresenter presenter) {
        this.presenter = presenter;
    }

    public void getData(Context context,int pageNo,int pageSize, final int type,String arg) {
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
                    data = data.replace("pager.","");
                    SectionG sectionG = gson.fromJson(data, SectionG.class);
                    totalRows = sectionG.totalRows;
                    presenter.loadDataSuccess(sectionG.rows,type);
//
                } catch (Exception e) {
                    presenter.hasError();
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
        client.get(url+"?pager.pageNo="+pageNo+"&pager.pageSize="+pageSize+"&sort=mgDeptId,mtDeptId,sortOrderCode" +
                        "&direction=asc"+"&deptIds="+arg,
                responseHandler);

    }
}
