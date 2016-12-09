package com.chengtech.chengtechmt.activity.standard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListView;

import com.chengtech.chengtechmt.BaseActivity;
import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.activity.dbm.DbmActivity;
import com.chengtech.chengtechmt.adapter.MyExpandableAdapter;
import com.chengtech.chengtechmt.util.MyConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardActivity extends BaseActivity {

    private ExpandableListView expandableListView;
    //    private String[] groupName = {"技术状况评定标准", "病学字典", "辅助决策模型", "其他规范标准"};
    private String[] groupName = {"病害字典"};
    private ArrayList<String> childList1;
    private ArrayList<String> childTreeUrl1;
    private ArrayList<String> childListUrl1;
    private Map<String, List<String>> groupList;
    private MyExpandableAdapter expandableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_standard);

        initView();
        setNavigationIcon(true);
        hidePagerNavigation(true);
        setAppBarLayoutScroll(false);
        initData();
        expandableAdapter = new MyExpandableAdapter(this, groupName, groupList);
        expandableListView.setAdapter(expandableAdapter);
        expandableListView.expandGroup(0);
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        initEvent();
    }

    private void initEvent() {
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                try {
                    Intent intent = new Intent(StandardActivity.this, StandardListActivity.class);
                    intent.putExtra("url", childTreeUrl1.get(childPosition));
                    intent.putExtra("urlList",childListUrl1.get(childPosition));
                    intent.putExtra("title", childList1.get(childPosition));
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                }
                return false;
            }
        });
    }

    private void initData() {
        childList1.add("评定病害类型");
        childList1.add("评定病害位置");
        childList1.add("病害维修方案");
        groupList.put(groupName[0], childList1);
        childTreeUrl1.add(MyConstants.PRE_URL+"mt/standard/diseasedictionary/facilities/dtree/getTree.action");
        childTreeUrl1.add(MyConstants.PRE_URL+"mt/standard/diseasedictionary/location/ddtree/getTree.action");
        childTreeUrl1.add(MyConstants.PRE_URL+"mt/standard/diseasedictionary/diseasemaintenanceplan/dmptree/getTree.action");
        childListUrl1.add(MyConstants.PRE_URL+"mt/standard/diseasedictionary/facilities/facilitiescategory/listFacilitiesCategoryJson.action?dtreeId=");
        childListUrl1.add(MyConstants.PRE_URL+"mt/standard/diseasedictionary/location/diseaselocation/listDiseaseLocationJson.action?ddtreeId=");
        childListUrl1.add(MyConstants.PRE_URL+"mt/standard/diseasedictionary/diseasemaintenanceplan/diseasemaintenance/addEditDiseaseMaintenance.action?mobile=phone&dmpTreeId=");
    }

    private void initView() {
        expandableListView = (ExpandableListView) findViewById(R.id.expandlist);
        groupList = new HashMap<String, List<String>>();
        childList1 = new ArrayList<String>();
        childListUrl1 = new ArrayList<String>();
        childTreeUrl1 = new ArrayList<String>();
    }
}
