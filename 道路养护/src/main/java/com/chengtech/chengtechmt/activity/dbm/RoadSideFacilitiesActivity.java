package com.chengtech.chengtechmt.activity.dbm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.chengtech.chengtechmt.BaseActivity;
import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.entity.MapEntity;
import com.chengtech.chengtechmt.entity.Tree;
import com.chengtech.chengtechmt.entity.dbm.Cover;
import com.chengtech.chengtechmt.entity.dbm.Video;
import com.chengtech.chengtechmt.entity.dbm.WeatherStation;
import com.chengtech.chengtechmt.entity.gson.TreeG;
import com.chengtech.chengtechmt.util.HttpclientUtil;
import com.chengtech.chengtechmt.util.MyConstants;
import com.chengtech.chengtechmt.util.UserUtils;

import com.google.gson.Gson;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shelwee.uilistview.ui.UiListView;
import com.shelwee.uilistview.ui.UiListView.ClickListener;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class RoadSideFacilitiesActivity extends BaseActivity implements OnClickListener {
    private static final int TYPE1 = 1;
    private static final int TYPE2 = 2;
    private static final int TYPE3 = 3;
    private UiListView uiListView;
    private Spinner roadSpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private String url;
    private List<Tree> trees;
    private String actionName;
    private ArrayList<Video> videos;
    private ArrayList<Cover> covers;
    private ArrayList<WeatherStation> weatherStations;
    private String videoListUrl = "mt/dbm/road/roadsidefacilities/video/listVideoJson.action?pager.pageSize=";
    private String coverListUrl = "mt/dbm/road/roadsidefacilities/cover/listCoverJson.action?pager.pageSize=";
    private String weatherListUrl = "mt/dbm/road/roadsidefacilities/weatherstation/listWeatherStationJson.action?pager.pageSize=";
    private String appendUrl;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            String json = (String) msg.obj;
            Gson gson = new Gson();
            switch (msg.what) {
                case TYPE1:
                    if (!TextUtils.isEmpty(json)) {
                        try {
                            json = "{\"success\":true,\"data\" : " + json + "}";
                            TreeG treeG = gson.fromJson(json, TreeG.class);
                            trees = treeG.data;
                            //将获得的数据进行整理，添加到两个spinner内
                            setSpinnerData();
                            loadDialog.dismiss();
                        } catch (Exception e) {
                            UserUtils.reLogin(RoadSideFacilitiesActivity.this, loadDialog);
                        }

                    } else {
                        loadDialog.dismiss();
                    }
                    break;

                case TYPE2:
                    if (!TextUtils.isEmpty(json)) {
                        try {
                            switch (actionName) {
                                case "Video":
                                    Video.VideoG videoG = gson.fromJson(json, Video.VideoG.class);
                                    videos = videoG.rows;
                                    if (videos != null && videos.size() == pageSize)
                                        maxPage++;
                                    uiListView.clear();
                                    if (videos != null && videos.size() > 0) {
                                        for (int i = 0; i < videos.size(); i++) {
                                            uiListView.addBasicItem(!TextUtils.isEmpty(videos.get(i).name) ? videos.get(i).name : " 编号: " + videos.get(i).code);
                                        }
                                        uiListView.commit();
                                    } else {
                                        Toast.makeText(RoadSideFacilitiesActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                                        pageNo--;
                                        maxPage--;
                                        pageNo_tv.setText(String.valueOf(pageNo));

                                    }
                                    break;
                                case "Cover":
                                    Cover.CoverG coverG = gson.fromJson(json, Cover.CoverG.class);
                                    covers = coverG.rows;
                                    if (covers != null && covers.size() == pageSize)
                                        maxPage++;
                                    uiListView.clear();
                                    if (covers != null && covers.size() > 0) {
                                        for (int i = 0; i < covers.size(); i++) {
                                            uiListView.addBasicItem(TextUtils.isEmpty(covers.get(i).code) ?  covers.get(i).name :  " 编号: " +covers.get(i).code);
                                        }
                                        uiListView.commit();
                                    } else {
                                        Toast.makeText(RoadSideFacilitiesActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                                        pageNo--;
                                        maxPage--;
                                        pageNo_tv.setText(String.valueOf(pageNo));

                                    }
                                    break;
                                case "Weatherstation":
                                    WeatherStation.WeatherStationG weatherStationG = gson.fromJson(json, WeatherStation.WeatherStationG.class);
                                    weatherStations = weatherStationG.rows;
                                    if (weatherStations != null && weatherStations.size() == pageSize)
                                        maxPage++;
                                    uiListView.clear();
                                    if (weatherStations != null && weatherStations.size() > 0) {
                                        for (int i = 0; i < weatherStations.size(); i++) {
                                            uiListView.addBasicItem(TextUtils.isEmpty(weatherStations.get(i).name) ? " 编号: " +weatherStations.get(i).code :  weatherStations.get(i).name);
                                        }
                                        uiListView.commit();
                                    } else {
                                        Toast.makeText(RoadSideFacilitiesActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                                        pageNo--;
                                        maxPage--;
                                        pageNo_tv.setText(String.valueOf(pageNo));

                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(RoadSideFacilitiesActivity.this, "数据解析出错。", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_road_side_facilities);

        setNavigationIcon(true);
        setAppBarLayoutScroll(false);

        initView();
        initEvent();
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        getData(url, TYPE1);

    }


    private void initEvent() {
        roadSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long arg3) {
                String msg = (String) parent.getAdapter().getItem(position);
                if (msg.contains("视频")) {
                    actionName = "Video";
                    appendUrl = MyConstants.PRE_URL + videoListUrl;
                } else if (msg.contains("井盖")) {
                    actionName = "Cover";
                    appendUrl = MyConstants.PRE_URL + coverListUrl;
                }else if (msg.contains("气象")) {
                    actionName = "Weatherstation";
                    appendUrl = MyConstants.PRE_URL+weatherListUrl;
                }
                //重新开始计算页码
                pageNo = 1;
                maxPage = 1;
                pageNo_tv.setText(pageNo + "");
                getData(appendUrl + pageSize + "&pager.pageNo=" + pageNo, TYPE2);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        uiListView.setClickListener(new ClickListener() {

            @Override
            public void onClick(int index) {
//                splicUrl(index);
                Intent intent = new Intent(RoadSideFacilitiesActivity.this, DetailActivity.class);
                MapEntity mapEntity = new MapEntity();
                switch (actionName) {
                    case "Video":
                        mapEntity.poi = "{\"x\":"+videos.get(index).longitude+",\"y\":"+videos.get(index).latitude+"}";
                        mapEntity.lineData = "[{\"name\":\"视屏设备卡片信息\",\"x\":113.319896,\"y\":23.12772,\"url\":\"http://localhost:8080/chengtechmt/resources/include/video.jsp\"," +
                                "\"状态\":\"开\",\"视屏设备编号\":"+videos.get(index).num+",\"监控点名称\":"+videos.get(index).name+"," +
                                "\"维护人\":"+videos.get(index).maintenancePeople+",\"维护电话\":"+videos.get(index).maintenanceMoblie+"}]";
                        intent.putExtra("map",mapEntity);
                        intent.putExtra("title", videos.get(index).name);
                        intent.putExtra("content", videos.get(index).getContent());
                        intent.putExtra("sessionId", videos.get(index).sessionId);
                        intent.putExtra("subtitle", videos.get(index).getTitles());
                        intent.putExtra("subtitleProperty", videos.get(index).getFieldNames());
                        break;
                    case "Cover":
                        mapEntity.poi = "{\"x\":"+covers.get(index).longitude+",\"y\":"+covers.get(index).latitude+"}";
                        mapEntity.lineData = "[{\"name\":\"井盖设备卡片信息\",\"x\":122,\"y\":23," +
                                "\"井盖设备编号\":"+covers.get(index).code+",\"安装时间\":"+covers.get(index).installTime+"," +
                                "\"安装地点\":"+covers.get(index).installLocation+",\"规格尺寸\":"+covers.get(index).deviceSize+"," +
                                "\"电话\":"+covers.get(index).telephone+"}]";
                        intent.putExtra("map",mapEntity);
                        intent.putExtra("title", covers.get(index).code);
                        intent.putExtra("content", covers.get(index).getContent());
                        intent.putExtra("sessionId", covers.get(index).sessionId);
                        intent.putExtra("subtitle", covers.get(index).getTitles());
                        intent.putExtra("subtitleProperty", covers.get(index).getFieldNames());
                        break;
                    case "Weatherstation":
                        mapEntity.poi = "{\"x\":"+weatherStations.get(index).longitude+",\"y\":"+weatherStations.get(index).latitude+"}";
                        mapEntity.lineData = "[{\"name\":\"气象站卡片信息\",\"x\":113.123253,\"y\":23.563214," +
                                "\"编号\":"+weatherStations.get(index).code+",\"名称\":"+weatherStations.get(index).name+"," +
                                "\"管理人\":"+weatherStations.get(index).manager+",\"位置\":"+weatherStations.get(index).location+"}]";
                        intent.putExtra("map",mapEntity);
                        intent.putExtra("title", weatherStations.get(index).name);
                        intent.putExtra("content", weatherStations.get(index).getContent());
                        intent.putExtra("sessionId", weatherStations.get(index).sessionId);
                        intent.putExtra("subtitle", weatherStations.get(index).getTitles());
                        intent.putExtra("subtitleProperty", weatherStations.get(index).getFieldNames());
                        break;
                }

                startActivity(intent);
            }

        });
    }


    private void getData(String url, final int type) {
        AsyncHttpClient client = HttpclientUtil.getInstance(this);
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                if (type == 1) {
                    loadDialog.show();
                }
                super.onStart();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                try {
                    String data = new String(arg2, "utf-8");
                    Message message = new Message();
                    message.what = type;
                    message.obj = data;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSuccess(arg0, arg1, arg2);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                if (type == 1) {
                    loadDialog.dismiss();
                }
                Toast.makeText(RoadSideFacilitiesActivity.this, "服务器断开连接",
                        Toast.LENGTH_SHORT).show();
                super.onFailure(arg0, arg1, arg2, arg3);
            }
        };
        client.get(url, responseHandler);

    }

    private void initView() {
        uiListView = (UiListView) findViewById(R.id.uilist);
        roadSpinner = (Spinner) findViewById(R.id.road_type);
        up_action.setOnClickListener(this);
        down_action.setOnClickListener(this);

    }

    /**
     * 设置spinner数据
     *
     * @author liufuyingwang
     * 2016-1-6 下午3:07:53
     */
    protected void setSpinnerData() {
        List<String> spinnerList = new ArrayList<String>();
        for (int i = 0; i < trees.size(); i++) {
            if (!"root".equals(trees.get(i).type)) {
                spinnerList.add(trees.get(i).text);
            }
        }

        spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerList);
        roadSpinner.setAdapter(spinnerAdapter);
    }

    /**
     * 拼接url，并且利用webview加载
     *
     * @author liufuyingwang
     * 2016-1-7 下午3:12:17
     */
    public void splicUrl(int index) {
//        String editUrl = trees.get(spinnerPosition + 1).url;
//        String[] urlSplit = editUrl.split("/");
//        String actionName = urlSplit[urlSplit.length - 1];
//        actionName = "/" + actionName;
//        actionName = actionName.replace("/list", "addEdit");
//        int lastPos = editUrl.lastIndexOf("/");
//        editUrl = editUrl.substring(0, lastPos + 1);
//        editUrl = MyConstants.PRE_URL + "mt/" + editUrl;
//        editUrl = editUrl + actionName + "?mobile=phone&pageNo=1&isRead=true&id=" + itemTree.get(index).id;
//        Intent intent = new Intent(RoadSideFacilitiesActivity.this, WebViewActivity.class);
//        intent.putExtra("url", editUrl);
//        intent.putExtra("title", TextUtils.isEmpty(itemTree.get(index).name) ? "" : itemTree.get(index).name);
//        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.perPage:
                if (pageNo != 1) {
                    pageNo--;
                    pageNo_tv.setText(pageNo + "");
                    getData(appendUrl + pageSize + "&pager.pageNo=" +
                            pageNo, TYPE2);
                } else {
                    Toast.makeText(this, "当前是最新页", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nextPage:
                if (pageNo < maxPage) {
                    pageNo++;
                    pageNo_tv.setText(pageNo + "");
                    getData(appendUrl + pageSize + "&pager.pageNo=" +
                            pageNo, TYPE2);
                } else {
                    Toast.makeText(this, "当前已经是最后一页", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }


}
