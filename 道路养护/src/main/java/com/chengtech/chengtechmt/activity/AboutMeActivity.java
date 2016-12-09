package com.chengtech.chengtechmt.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.entity.User;
import com.chengtech.chengtechmt.entity.gson.UserG;
import com.chengtech.chengtechmt.util.ACache;
import com.chengtech.chengtechmt.util.AppAccount;
import com.chengtech.chengtechmt.util.HttpclientUtil;
import com.chengtech.chengtechmt.util.MyConstants;
import com.chengtech.chengtechmt.util.MyDialogUtil;
import com.chengtech.chengtechmt.util.UserUtils;
import com.chengtech.chengtechmt.view.TitleLayout;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shelwee.uilistview.ui.UiListView;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

public class AboutMeActivity extends Activity {

    private TitleLayout layout;
    private UiListView uiListView;
    private Dialog loadDialog;
    private ACache aCache;
    private int refreshCount = 0;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String json = (String) msg.obj;
            Gson gson = new Gson();
            try {
                    UserG userG = gson.fromJson(json, UserG.class);
                    User user = userG.data;
                if (user != null) {
                    uiListView.addBasicItem("\t用户名：" + (user.name == null ? "" : user.name));
                    uiListView.addBasicItem("\t账号：" + (user.userAccount == null ? "" : user.userAccount));
                    uiListView.addBasicItem("\t员工编号：" + (user.userNo == null ? "" : user.userNo));
                    uiListView.addBasicItem("取回密码问题：" + (user.question == null ? "" : user.question));
                    uiListView.addBasicItem("取回密码答案：" + (user.answer == null ? "" : user.answer));
                    uiListView.addBasicItem("性别：" + (user.sex.equals("1") ? "男" : "女"));
                    uiListView.addBasicItem("职位：" + (user.jobPosition == null ? "" : user.jobPosition));
                    uiListView.addBasicItem("出生年月：" + (user.birthday == null ? "" : user.birthday));
                    uiListView.addBasicItem("移动电话：" + (user.mobile == null ? "" : user.mobile));
                    uiListView.addBasicItem("住宅地址：" + (user.homeAddress == null ? "" : user.homeAddress));
                    uiListView.addBasicItem("邮箱：" + (user.email == null ? "" : user.email));
                    uiListView.addBasicItem("最后登陆时间：" + (user.lastLoginTime == null ? "" : user.lastLoginTime));
                    uiListView.commit();
                    loadDialog.dismiss();
                    aCache.put(AppAccount.userId, user, ACache.TIME_DAY);
                }
            } catch (Exception e) {
                UserUtils.reLogin(AboutMeActivity.this, loadDialog);
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about_me);

        initView();
        //User aboutMe = (User) aCache.getAsObject(AppAccount.userId);
        //if (aboutMe==null) {
            getData();
        //}else {
//            Message message = new Message();
//            message.obj = aboutMe;
//            handler.sendMessage(message);
//       }

    }

    private void getData() {
        AsyncHttpClient client = HttpclientUtil.getInstance(this);
        AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler(){
            @Override
            public void onStart() {
                loadDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                loadDialog.dismiss();
                try {
                    String data = new String(arg2,"utf-8");
                    Message message = new Message();
                    message.obj = data;
                    handler.sendMessage(message);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                super.onSuccess(arg0, arg1, arg2);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                loadDialog.dismiss();
                Toast.makeText(AboutMeActivity.this, "服务器断开连接",Toast.LENGTH_SHORT).show();
                super.onFailure(arg0, arg1, arg2, arg3);
            }

        };

        client.get(MyConstants.ABOUT_ME_URL+AppAccount.userId, asyncHttpResponseHandler);

    }

    private void initView() {
        loadDialog = MyDialogUtil.createDialog(this, "正在加载");
        aCache = ACache.get(this);
        layout = (TitleLayout) findViewById(R.id.mytitle);
        layout.setTitle("个人信息");

        uiListView = (UiListView) findViewById(R.id.uilist);

    }


}
