package com.chengtech.chengtechmt;

import android.app.Application;

import com.chengtech.chengtechmt.util.MyConstants;
import com.pgyersdk.crash.PgyCrashManager;


/**
 * 作者: LiuFuYingWang on 2016/5/25 16:26.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Fresco.initialize(this);
        PgyCrashManager.register(this);
        MyConstants.imageDict.put("路线信息", R.mipmap.route);
        MyConstants.imageDict.put("道路数据", R.mipmap.route);
        MyConstants.imageDict.put("区间路段信息", R.mipmap.section);
        MyConstants.imageDict.put("养护路段信息", R.mipmap.worksection);
        MyConstants.imageDict.put("桥梁卡片", R.mipmap.bridge);
        MyConstants.imageDict.put("桥梁数据", R.mipmap.bridge);
        MyConstants.imageDict.put("隧道卡片", R.mipmap.tunnel);
        MyConstants.imageDict.put("隧道数据", R.mipmap.tunnel);
        MyConstants.imageDict.put("涵洞卡片", R.mipmap.culvert);
        MyConstants.imageDict.put("涵洞数据", R.mipmap.culvert);
        MyConstants.imageDict.put("沿线设施", R.mipmap.along);
        MyConstants.imageDict.put("设施量统计", R.mipmap.along);
        MyConstants.imageDict.put("边坡路堤挡墙", R.mipmap.slope);
        MyConstants.imageDict.put("边坡数据", R.mipmap.slope);
        MyConstants.imageDict.put("交安设施", R.mipmap.safetyfacilities);
        MyConstants.imageDict.put("服务交安设施", R.mipmap.safetyfacilities);
        MyConstants.imageDict.put("绿化信息", R.mipmap.greening);
        MyConstants.imageDict.put("绿化数据", R.mipmap.greening);
        MyConstants.imageDict.put("交通量观测站", R.mipmap.observatory);
        MyConstants.imageDict.put("交通量观测点数据", R.mipmap.observatory);
        MyConstants.imageDict.put("规范标准管理", R.mipmap.standard);
        MyConstants.imageDict.put("基础数据管理", R.mipmap.main_img2_b);
        MyConstants.imageDict.put("养护业务管理", R.mipmap.main_img3_b);
        MyConstants.imageDict.put("监测及应急保障", R.mipmap.main_img4_b);
        MyConstants.imageDict.put("专家决策", R.mipmap.main_img5_b);
        MyConstants.imageDict.put("综合查询", R.mipmap.main_img6_b);
        MyConstants.imageDict.put("病害字典", R.mipmap.dictionary);
        MyConstants.imageDict.put("静态数据管理", R.mipmap.database);
        MyConstants.imageDict.put("动态数据管理", R.mipmap.datebase2);
        MyConstants.imageDict.put("评定病害类型", R.mipmap.disease);
        MyConstants.imageDict.put("病害维修方案", R.mipmap.disease);
        MyConstants.imageDict.put("评定病害位置", R.mipmap.disease);
        MyConstants.imageDict.put("大中修、改造（善）及省部补助项目", R.mipmap.business_a_01);
        MyConstants.imageDict.put("小额专项维修", R.mipmap.business_a_02);
        MyConstants.imageDict.put("月度检查", R.mipmap.month_checked);
        MyConstants.imageDict.put("保修作业", R.mipmap.maintaintask);
        MyConstants.imageDict.put("小修作业", R.mipmap.minorrepair);
    }
}
