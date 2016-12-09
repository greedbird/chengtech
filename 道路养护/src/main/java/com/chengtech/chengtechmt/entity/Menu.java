package com.chengtech.chengtechmt.entity;

import android.content.Context;
import android.util.Log;

import com.chengtech.chengtechmt.entity.gson.RouteG;
import com.chengtech.chengtechmt.presenter.Presenter;
import com.chengtech.chengtechmt.util.HttpclientUtil;
import com.chengtech.chengtechmt.util.MyConstants;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 作者: LiuFuYingWang on 2016/6/27 9:11.
 */
public class Menu {
    public String itemName;
    public String itemNo;
    public String url;
    public String isEffective;
    public String isModule;
    public String rightCode;
    public String isVirtualModule;
    public String imgName;
    public String groupId;
    public String description;
    public String parentId;
    public String id;
    public String code;
    public String delFlag;
    public String sortOrder;
    public String iconCls;
    public String actionUrl;
    public String state;
    public String isDesktopModelItem;
    public String desktopModelCodeIframe;
    public String isSelected;
    public String isLeaf;
    public String pageNo;

    public List<Menu> data;
    public Presenter presenter;

    public Menu(Presenter integrateQueryPresenter) {
        this.presenter = integrateQueryPresenter;
    }

    public void getData(Context context,String id,String url ) {
        AsyncHttpClient client = HttpclientUtil.getInstance(context);

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    Log.i("tag",arg2.length+"");
                    String data = new String(arg2, "utf-8");
                    Log.i("tag",data);
                    Gson gson  = new Gson();
                    data = "{\"data\" : " + data + "}";
                    Menu m = gson.fromJson(data,Menu.class);
                    presenter.loadDataSuccess(m.data);
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
        Log.i("tag",url+"?onClickMenuId="+id+"&mobile=phone");
        client.get(url+"?onClickMenuId="+id+"&mobile=phone",
                responseHandler);

    }
}
