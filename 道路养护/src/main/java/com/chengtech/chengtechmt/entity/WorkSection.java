package com.chengtech.chengtechmt.entity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.chengtech.chengtechmt.entity.gson.SectionG;
import com.chengtech.chengtechmt.entity.gson.WorkSectionG;
import com.chengtech.chengtechmt.presenter.Presenter;
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
 * 作者: LiuFuYingWang on 2016/5/17 15:42.
 */
public class WorkSection extends BaseModel{

    private String sort = "mgDeptId,mtDeptId,sortOrderCode,startStake";
    public List<String> propertyValue;
    public WorkSectionPresenter presenter;
    public String url = MyConstants.PRE_URL+"mt/dbm/road/worksection/listWorkSectionJson.action";
    public int totalRows;

    public String techGrade; 			// 技术等级
    public String startStake; 			// 里程起点
    public String endStake; 			// 里程终点
    public Dept mgDept; 				// 管理单位
    public Dept mtDept; 				// 养护单位
    public String length; 				// 长度
    public String driveType; 			// 车道类型
    public String discountedMileage; 	// 折算里程
    public String rampMileage; 		// 匝道连接线里程
    public String roadWide; 			// 路面均宽
    public String regionCode; 			// 政区编码
    public String roadType; 			// 路面类型
    public String direction; 			// 方向
    public String ahighSpeed; 		 	// 是否一幅高速
    public String designSpeed; 	 	// 设计时速
    public String constructYear;	 	// 修建年度
    public String reconstructYear;  	// 改建年度
    public String lastMajorRepairYear; // 最近一次大中修年度
    public String landformType;        // 地貌类型
    public String subgradeLength;      // 路基长度
    public String startStakeName; 	// 起点名称
    public String endStakeName; 	// 终点名称

    @Override
    public List<String> getContent() {
        if (propertyValue==null) {
            propertyValue = new ArrayList<>();
            propertyValue.add(TextUtils.isEmpty(code)?"":code);
            propertyValue.add(TextUtils.isEmpty(name)?"":name);
            propertyValue.add(TextUtils.isEmpty(techGrade)?"":techGrade);
            propertyValue.add(TextUtils.isEmpty(startStake)?"":startStake);
            propertyValue.add(TextUtils.isEmpty(endStake)?"":endStake);
            propertyValue.add(TextUtils.isEmpty(mgDept.name)?"":mgDept.name);
            propertyValue.add(TextUtils.isEmpty(mtDept.name)?"":mtDept.name);
            propertyValue.add(TextUtils.isEmpty(length)?"":length);
            propertyValue.add(TextUtils.isEmpty(driveType)?"":driveType);
            propertyValue.add(TextUtils.isEmpty(discountedMileage)?"":discountedMileage);
            propertyValue.add(TextUtils.isEmpty(rampMileage)?"":rampMileage);
            propertyValue.add(TextUtils.isEmpty(roadWide)?"":roadWide);
            propertyValue.add(TextUtils.isEmpty(regionCode)?"":regionCode);
            propertyValue.add(TextUtils.isEmpty(roadType)?"":roadType);
            propertyValue.add(TextUtils.isEmpty(direction)?"":direction);
            propertyValue.add(TextUtils.isEmpty(ahighSpeed)?"":ahighSpeed);
            propertyValue.add(TextUtils.isEmpty(designSpeed)?"":designSpeed);
            propertyValue.add(TextUtils.isEmpty(constructYear)?"":constructYear);
            propertyValue.add(TextUtils.isEmpty(reconstructYear)?"":reconstructYear);
            propertyValue.add(TextUtils.isEmpty(lastMajorRepairYear)?"":lastMajorRepairYear);
            propertyValue.add(TextUtils.isEmpty(landformType)?"":landformType);
            propertyValue.add(TextUtils.isEmpty(subgradeLength)?"":subgradeLength);
        }
        return propertyValue;
    }

    public WorkSection(WorkSectionPresenter presenter) {
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
                    WorkSectionG workSectionG = gson.fromJson(data, WorkSectionG.class);
                    totalRows = workSectionG.totalRows;
                    presenter.loadDataSuccess(workSectionG.rows,type);
                } catch (Exception e) {
                    presenter.hasError();
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
        client.get(url+"?pager.pageNo="+pageNo+"&pager.pageSize="+pageSize+"&sort="+sort+
                        "&direction=asc"+"&deptIds="+arg,
                responseHandler);
    }
}
