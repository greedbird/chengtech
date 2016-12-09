package com.chengtech.chengtechmt.activity.business;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chengtech.chengtechmt.BaseActivity;
import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.activity.IView;
import com.chengtech.chengtechmt.adapter.MyExpandableAdapter3;
import com.chengtech.chengtechmt.adapter.RecycleViewAdapter3;
import com.chengtech.chengtechmt.entity.CapitalSource;
import com.chengtech.chengtechmt.entity.OtherExpend;
import com.chengtech.chengtechmt.entity.ProjectManagement;
import com.chengtech.chengtechmt.presenter.Presenter;
import com.chengtech.chengtechmt.presenter.ProjectManagerPre;
import com.chengtech.chengtechmt.util.DateUtils;
import com.chengtech.chengtechmt.util.MyConstants;
import com.chengtech.chengtechmt.util.NestedScrollViewUtil;
import com.chengtech.chengtechmt.view.TitleLayout;
import com.shelwee.uilistview.ui.UiListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 大中修详细显示页面
 */
public class ProjectManagementActivity extends BaseActivity implements IView<Object>{

    public ProjectManagerPre presenter;
    public List<CapitalSource> capitalSourceList;
    public List<OtherExpend> otherExpendList;
    public boolean sourceFlag = true;
    public boolean expendFlag = true;
    public String capitalSoureUrl =
            MyConstants.PRE_URL+"mt/business/maintenance/projectaccount/capitalsource/listCapitalSourceJson.action?isRead=true&projectId=";
    public String expendUrl =
            MyConstants.PRE_URL+"mt/business/maintenance/projectaccount/otherexpend/listOtherExpendJson.action?isRead=true&projectId=";
    public String[] titles = new String[]{
            "一、建设概况", "二、建设规模(公里)", "三、建设方案", "四、施工图设计", "五、工程招投标",
            "六、工程投资", "七、资金管理", "八、工程实施", "九、工程验收"
    };
//    public TitleLayout titleLayout;
    public Spinner spinner;
    public ArrayAdapter<String> adapter;
    public ProjectManagement projectManagement;
    public RecyclerView recyclerView;
    List<String> subTitle = new ArrayList<>();
    List<String> content = new ArrayList<>();
    public List<List<String>> contentList = new ArrayList<>();
    public List<List<String>> subtitleList = new ArrayList<>();
    public RecycleViewAdapter3 adapter3;


    public ExpandableListView expandableListView;
    public String [] groupName = new String[] {"资金来源","资金支出"};
    public Map<String,List<List<String>>> childMap;
    public MyExpandableAdapter3 expandableAdapter3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_project_management);

        Intent intent = getIntent();
        projectManagement = (ProjectManagement) intent.getSerializableExtra("data");

        setNavigationIcon(true);
        hidePagerNavigation(true);
        setAppBarLayoutScroll(false);
        toolbar.setTitle(projectManagement.projectName);
        initView();
        //最后的那部分，包含填表日期，其他，备注
        LinearLayout otherLayout = (LinearLayout) findViewById(R.id.other);
        for (int i=1;i<4;i++) {
            TextView textView = (TextView) otherLayout.getChildAt(i);
            String content = textView.getText().toString().trim();
            if (i==1)
                content = content+projectManagement.fillDate;
            else if(i==2)
                content = content+(projectManagement.other==null?"":projectManagement.other);
            else if (i==3)
                content = content+(projectManagement.memo==null?"":projectManagement.memo);

            textView.setText(content);
        }
//        titleLayout.setTitle(projectManagement.projectName);
//        adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titles);
        adapter = new ArrayAdapter<String>(this, R.layout.item_spinner_popup, titles);
        spinner.setAdapter(adapter);


        initData();
        initEvent();

    }

    private void initData() {
        if (projectManagement != null) {
            contentList.add(projectManagement.getListOne());
            contentList.add(projectManagement.getListTwo());
            contentList.add(projectManagement.getListThree());
            contentList.add(projectManagement.getListFour());
            contentList.add(projectManagement.getListFive());
            contentList.add(projectManagement.getListSix());
            contentList.add(new ArrayList<String>());
            contentList.add(projectManagement.getListEight());
            contentList.add(projectManagement.getListNine());
            subtitleList.add(Arrays.asList(getResources().getStringArray(R.array.projectmanagement_one)));
            subtitleList.add(Arrays.asList(getResources().getStringArray(R.array.projectmanagement_two)));
            subtitleList.add(Arrays.asList(getResources().getStringArray(R.array.projectmanagement_three)));
            subtitleList.add(Arrays.asList(getResources().getStringArray(R.array.projectmanagement_four)));
            subtitleList.add(Arrays.asList(getResources().getStringArray(R.array.projectmanagement_five)));
            subtitleList.add(Arrays.asList(getResources().getStringArray(R.array.projectmanagement_six)));
            subtitleList.add(Arrays.asList(new String[]{}));
            subtitleList.add(Arrays.asList(getResources().getStringArray(R.array.projectmanagement_eight)));
            subtitleList.add(Arrays.asList(getResources().getStringArray(R.array.projectmanagement_nine)));
        }
        subTitle = subtitleList.get(0);
        content = contentList.get(0);
        adapter3 = new RecycleViewAdapter3(this, subTitle, content, R.layout.item_expandblelistview_child_2);
        recyclerView.setAdapter(adapter3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        childMap = new ArrayMap<>();

        expandableAdapter3 = new MyExpandableAdapter3(this,groupName,childMap,R.layout.item_expanlist_group_3,R.layout.item_expandblelistview_child_2);
        expandableListView.setAdapter(expandableAdapter3);
        NestedScrollViewUtil.setExpandableListViewHeightBasedOnChildren(expandableListView);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                NestedScrollViewUtil.setExpandableListViewHeightBasedOnChildren(expandableListView);
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                NestedScrollViewUtil.setExpandableListViewHeightBasedOnChildren(expandableListView);
            }
        });

    }

    private void initEvent() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==6) {
                    if (sourceFlag) {
                        presenter.getData(ProjectManagementActivity.this,capitalSoureUrl+projectManagement.id,"CapitalSource");
                        sourceFlag = !sourceFlag;
                    }

                    if (expendFlag) {
                        presenter.getData(ProjectManagementActivity.this,expendUrl+projectManagement.id,"OtherExpend");
                        expendFlag = !expendFlag;
                    }

                    recyclerView.setVisibility(View.GONE);
                    expandableListView.setVisibility(View.VISIBLE);
                }else {
                   recyclerView.setVisibility(View.VISIBLE);
                    expandableListView.setVisibility(View.GONE);
                }
                adapter3 = new RecycleViewAdapter3(ProjectManagementActivity.this,subtitleList.get(position),
                        contentList.get(position),R.layout.item_expandblelistview_child_2);
                recyclerView.setAdapter(adapter3);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProjectManagementActivity.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        expandableAdapter3.setOnItemClickListenr(new MyExpandableAdapter3.OnItemClickListener() {
            @Override
            public void onClick(View view, int groupPosition, int childPosition) {
                Dialog dialog = new Dialog(ProjectManagementActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                LayoutInflater inflater  = LayoutInflater.from(ProjectManagementActivity.this);
                View contentView = inflater.inflate(R.layout.item_expanlist_child_3,null,false);
                TextView tv1 = (TextView) contentView.findViewById(R.id.tv1);
                TextView tv2 = (TextView) contentView.findViewById(R.id.tv2);
                TextView tv3 = (TextView) contentView.findViewById(R.id.tv3);
                TextView tv4 = (TextView) contentView.findViewById(R.id.tv4);
                TextView tv5 = (TextView) contentView.findViewById(R.id.tv5);
                tv1.setText(childMap.get(groupName[groupPosition]).get(childPosition).get(0));
                tv2.setText(childMap.get(groupName[groupPosition]).get(childPosition).get(1));
                tv3.setText(childMap.get(groupName[groupPosition]).get(childPosition).get(2));
                tv4.setText(childMap.get(groupName[groupPosition]).get(childPosition).get(3));
                tv5.setText(childMap.get(groupName[groupPosition]).get(childPosition).get(4));

                DisplayMetrics metrics = getResources().getDisplayMetrics();

//                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((int)(metrics.widthPixels*0.6),(int)(metrics.heightPixels*0.5));

//                contentView.setLayoutParams(lp);
                dialog.setContentView(contentView);
                dialog.setCanceledOnTouchOutside(true);

                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = (int) (metrics.widthPixels*0.8);
                lp.height = (int) (metrics.heightPixels*0.6);
                dialogWindow.setAttributes(lp);
                dialog.show();

            }
        });
    }

    private void initView() {
        spinner = (Spinner) findViewById(R.id.spinner);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
//        titleLayout = (TitleLayout) findViewById(R.id.title);
        presenter = new ProjectManagerPre(this);
        expandableListView = (ExpandableListView) findViewById(R.id.expandlist);
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void dismssDialog() {

    }

    @Override
    public void loadDataSuccess(Object o) {

    }

    @Override
    public void loadDataSuccess(Object o, int type) {

    }

    @Override
    public void loadDataSuccess(Object object, String clasName) {
        switch (clasName) {
            case "CapitalSource" :
                capitalSourceList = (List<CapitalSource>) object;

                if (capitalSourceList!=null && capitalSourceList.size()>0) {
                    List<List<String>> list = new ArrayList<>();
                    for (CapitalSource c: capitalSourceList) {
                        list.add(c.getList());
                    }

                    childMap.put(groupName[0],list);
                }

                expandableAdapter3.notifyDataSetChanged();
                expandableListView.expandGroup(0);
                expandableListView.expandGroup(1);
                break;
            case "OtherExpend" :
                otherExpendList = (List<OtherExpend>) object;
                if (otherExpendList!=null && otherExpendList.size()>0) {
                    List<List<String>> list = new ArrayList<>();
                    for (OtherExpend o: otherExpendList) {
                        list.add(o.getList());
                    }
                    childMap.put(groupName[1],list);
                }

                expandableAdapter3.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void loadDataFailure() {

    }

    @Override
    public void hasError() {

    }
}
