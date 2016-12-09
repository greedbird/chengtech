package com.chengtech.chengtechmt.activity.integratequery.routequery;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.activity.business.MediumReportActivity;
import com.chengtech.chengtechmt.adapter.RecycleViewAdapter2;
import com.chengtech.chengtechmt.entity.MediumPlanprogress;
import com.chengtech.chengtechmt.entity.routequery.PatrolRecord;
import com.chengtech.chengtechmt.util.HttpclientUtil;
import com.chengtech.chengtechmt.util.MyConstants;
import com.chengtech.chengtechmt.util.MyDialogUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者: LiuFuYingWang on 2016/7/26 16:16.
 */
public class MediumPlanprogressFragment extends Fragment {

    public RecyclerView recyclerView;
    public Dialog loadDialog;
    public String routeId;
    public String url = MyConstants.PRE_URL + "mt/integratequery/basicdataquery/routedataquery/listMediumPlanprogressJson.action?mobile=phone&sectionId=&routeId=";
    public RecycleViewAdapter2 adapter;
    public List<String[]> data;
    public  List<MediumPlanprogress> mediumPlanprogressList;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String json = (String) msg.obj;
            Gson gson = new Gson();
            MediumPlanprogress.MediumPlanprogressG mediumPlanprogressG = gson.fromJson(json,MediumPlanprogress.MediumPlanprogressG.class);
            mediumPlanprogressList = mediumPlanprogressG.data;
            DecimalFormat df = new DecimalFormat("#.###");
            if (mediumPlanprogressList != null && mediumPlanprogressList.size() > 0) {
                for (MediumPlanprogress m : mediumPlanprogressList) {
                    String[] titleArray = new String[7];
                    titleArray[0] = "填报年份:" + m.fillYear;
                    titleArray[1] = "总实施里程:" + df.format(m.maintainLengths);
                    titleArray[2] = "总预算资金(万元):" +df.format(m.budgetFunds);
                    titleArray[3] = "总批复预算金额(万元):" +df.format(m.replyFunds);
                    titleArray[4] = "总支付资金(万元):" + df.format(m.paidFunds);
                    titleArray[5] = "未支付金额(万元):" + df.format(m.notPaidFunds);
                    titleArray[6] = "总路面工程数量(m2):" + df.format(m.projectNums);
                    data.add(titleArray);
                }
                recyclerView.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_medium_progress,container,false);
        loadDialog = MyDialogUtil.createDialog(getActivity(), "正在加载中...");
        loadDialog.show();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        routeId = bundle.getString("routeId");

        //网络请求
        getData();


        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setVisibility(View.GONE);

        data = new ArrayList<>();
        adapter = new RecycleViewAdapter2(getActivity(),data,R.layout.item_recycle3);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setOnItemClickListener(new RecycleViewAdapter2.OnItemClickListener() {
            @Override
            public void setOnItemClickListener(View view, int position) {
                Intent intent = new Intent(getActivity(), MediumReportActivity.class);
                intent.putExtra("data", (ArrayList) (mediumPlanprogressList.get(position).mediumPlanprogressItem));
                startActivity(intent);
            }

            @Override
            public void setOnItemLongClickListener(View view, int position) {

            }
        });

    }

    private void getData() {
        AsyncHttpClient client = HttpclientUtil.getInstance(getActivity());

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                loadDialog.dismiss();
                try {
                    String data = new String(arg2, "utf-8");
                    Message message = Message.obtain();
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
                loadDialog.dismiss();
                super.onFailure(arg0, arg1, arg2, arg3);
            }
        };
        client.get(url + routeId,
                responseHandler);

    }
}
