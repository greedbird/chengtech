package com.chengtech.chengtechmt.activity.business;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengtech.chengtechmt.BaseActivity;
import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.activity.IView;
import com.chengtech.chengtechmt.activity.dbm.DetailActivity;
import com.chengtech.chengtechmt.adapter.RecycleViewAdapter2;
import com.chengtech.chengtechmt.adapter.business.TaskDetailAdapter;
import com.chengtech.chengtechmt.entity.Dept;
import com.chengtech.chengtechmt.entity.MaintainRegister;
import com.chengtech.chengtechmt.entity.MaintainTaskItem;
import com.chengtech.chengtechmt.entity.TaskDetail;
import com.chengtech.chengtechmt.entity.gson.DeptG;
import com.chengtech.chengtechmt.entity.MaintainTask;
import com.chengtech.chengtechmt.presenter.ListPagePre;
import com.chengtech.chengtechmt.util.DateUtils;
import com.chengtech.chengtechmt.util.MyConstants;
import com.chengtech.chengtechmt.view.MyHorizontalScrollView2;
import com.chengtech.nicespinner.NiceSpinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 月度检查，保养作业，小修作业的列表页面
 */
public class MaintenanceListActivity extends BaseActivity
        implements IView<Object>, View.OnClickListener, RecycleViewAdapter2.OnItemClickListener {

    //    public LinearLayout thirdLayout; //“养护站”linearlayout，有的模块需要隐藏
    //    private Spinner firstSpinner;
    //    private Spinner secondSpinner;
    //    private Spinner thirdSpinner;
    //    private Spinner yearSpinner;
    //    private Spinner monthSpinner;
    private String[] years;
    private String[] months;
    private List<String> firstDept;
    private Map<String, List<String>> secondDept;
    private Map<String, List<String>> thirdDept;
    private ArrayAdapter<String> firstAdapter;
    private ArrayAdapter<String> secondAdapter;
    private ArrayAdapter<String> thirdAdapter;
    public String firstDeptId;
    public String secondDeptId;
    public String thirdDeptId;
    public String yearString;
    public String monthString;
    public RecyclerView recyclerView;
    public ListPagePre presenter;
    public String type;
    public String urlList;
    public String appendUrl;
    public DeptG deptG;
    public int firstPosition;
    public int secondPosition;
    //    public Button search_bt;

    public List<MaintainRegister> maintainRegisterList;
    public List<MaintainTask> maintainTaskList;
    public List<MaintainTaskItem> maintainTaskItemList;
    public List<TaskDetail> taskDetailList;

    public AlertDialog horizontalDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_maintenance_list);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        urlList = intent.getStringExtra("urlList");

        setNavigationIcon(true);
        setAppBarLayoutScroll(false);
        initView();

        initData();

        initEvent();

        initAdapter();
        String url = MyConstants.PRE_URL + "mt/common/index.action?mobile=phone";
        presenter.getData(this, url, "DeptG", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_business_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter) {
            if (deptG != null) {
                inflateSpnnier();
            } else {
                Toast.makeText(this, "获取部门数据失败。", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inflateSpnnier() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dept_spinner_selector, null);
        LinearLayout parent = (LinearLayout) contentView.findViewById(R.id.parent);
        if (!type.equals("MaintainRegister")) {
            parent.getChildAt(4).setVisibility(View.VISIBLE);
            parent.getChildAt(5).setVisibility(View.VISIBLE);
        }
        //年份选择器
        parent.getChildAt(6).setVisibility(View.VISIBLE);
        parent.getChildAt(7).setVisibility(View.VISIBLE);
        //月份选择器
        parent.getChildAt(8).setVisibility(View.VISIBLE);
        parent.getChildAt(9).setVisibility(View.VISIBLE);
        NiceSpinner firstS = (NiceSpinner) contentView.findViewById(R.id.firstDept);
        final NiceSpinner secondS = (NiceSpinner) contentView.findViewById(R.id.secondDept);
        final NiceSpinner thirdSpinner = (NiceSpinner) contentView.findViewById(R.id.thirdDept);
        NiceSpinner yearSpinner = (NiceSpinner) contentView.findViewById(R.id.year);
        NiceSpinner monthSpinner = (NiceSpinner) contentView.findViewById(R.id.month);
        yearSpinner.setVisibility(View.VISIBLE);
        firstS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondS.attachDataSource(secondDept.get(firstDept.get(position)));
                firstPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        secondS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstDeptId = deptG.listFirstDept.get(firstPosition).id;

                String selectedDeptName = secondDept.get(firstDept.get(firstPosition)).get(position);
                for (Dept d : deptG.listSecondDept) {
                    if (d.name.equals(selectedDeptName)) {
                        secondDeptId = d.id;
                        break;
                    }
                }
                thirdSpinner.attachDataSource(thirdDept.get(selectedDeptName));
                secondPosition = position;
                //重新置为第一页
                pageNo = 1;
                maxPage = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        thirdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDeptName =
                        thirdDept.get(secondDept.get(firstDept.get(firstPosition)).get(secondPosition))
                                .get(position);
                if (selectedDeptName.equals("请选择")) {
                    thirdDeptId = "";
                } else {
                    for (Dept d : deptG.listThirdDept) {
                        if (d.name.equals(selectedDeptName)) {
                            thirdDeptId = d.id;
                            break;
                        }
                    }
                }
                //重新置为第一页
                pageNo = 1;
                maxPage = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearString = years[position].replace("年", "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthString = months[position].replace("月", "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        firstS.attachDataSource(firstDept);
        yearSpinner.attachDataSource(Arrays.asList(years));
        monthSpinner.attachDataSource(Arrays.asList(months));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(contentView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (yearString.equals("请选择")) yearString = "";
                if (monthString.equals("请选择")) monthString = "";
                appendUrl = urlList
                        + "?firstDeptId="
                        + firstDeptId
                        + "&secondDeptId="
                        + secondDeptId
                        + "&thirdDeptId="
                        + thirdDeptId
                        + "&year="
                        + yearString
                        + "&month="
                        + monthString
                        + "&workYear="
                        + yearString
                        + "&workMonth"
                        + monthString;
                presenter.getData(MaintenanceListActivity.this, appendUrl, type,
                        Integer.parseInt(pageNo_tv.getText().toString()));
            }
        });

        builder.setNegativeButton("取消", null);
        AlertDialog  dialog = builder.create();

        dialog.show();
    }

    private void initAdapter() {
        //        yearSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years));
        //        monthSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months));
    }

    private void initEvent() {
        //        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //            @Override
        //            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //                yearString = years[position].replace("年", "");
        //            }
        //
        //            @Override
        //            public void onNothingSelected(AdapterView<?> parent) {
        //
        //            }
        //        });
        //
        //        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //            @Override
        //            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //                monthString = months[position].replace("月", "");
        //            }
        //
        //            @Override
        //            public void onNothingSelected(AdapterView<?> parent) {
        //
        //            }
        //        });

        //        search_bt.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                if (yearString.equals("请选择"))
        //                    yearString = "";
        //                if (monthString.equals("请选择"))
        //                    monthString = "";
        //                appendUrl = urlList + "?firstDeptId=" + firstDeptId + "&secondDeptId=" + secondDeptId + "&thirdDeptId=" + thirdDeptId + "&year=" + yearString + "&month=" + monthString + "&workYear=" + yearString + "&workMonth" + monthString;
        //                presenter.getData(MaintenanceListActivity.this, appendUrl, type, Integer.parseInt(pageNo_tv.getText().toString()));
        //            }
        //        });

        up_action.setOnClickListener(this);
        down_action.setOnClickListener(this);
    }

    private void initView() {
        //        firstSpinner = (Spinner) findViewById(R.id.firstDept);
        //        secondSpinner = (Spinner) findViewById(R.id.secondDept);
        //        thirdSpinner = (Spinner) findViewById(R.id.thirdDept);
        //        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        //        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        //        thirdLayout = (LinearLayout) findViewById(R.id.thirdLayout);
        //        search_bt = (Button) findViewById(R.id.search);
        //        if (type.equals("MaintainRegister")) {
        //            thirdLayout.setVisibility(View.GONE);
        //        } else {
        //            thirdLayout.setVisibility(View.VISIBLE);
        //        }
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);

        presenter = new ListPagePre(this, type);
    }

    private void initData() {
        firstDept = new ArrayList<String>();
        years = new String[]{
                "请选择", "2014年", "2015年", "2016年", "2017年", "2018年", "2019年", "2020年"
        };
        months = new String[]{
                "请选择", "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"
        };
        secondDept = new HashMap<String, List<String>>();
        thirdDept = new HashMap<String, List<String>>();
    }

    @Override
    public void showDialog() {
        loadDialog.show();
    }

    @Override
    public void dismssDialog() {
        loadDialog.dismiss();
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
            case "DeptG":
                deptG = (DeptG) object;
                if (deptG != null) {
                    setSpinnerData();
                }
                break;
            case "MaintainRegister":
                maintainRegisterList = (List<MaintainRegister>) object;

                if (maintainRegisterList.size() == pageSize) maxPage++;

                List<String[]> titleArrayList2 = new ArrayList<>();
                if (maintainRegisterList != null && maintainRegisterList.size() > 0) {
                    for (MaintainRegister m : maintainRegisterList) {
                        String[] titleArray = new String[2];
                        titleArray[0] =
                                "检查单位:" + secondDept.get(firstDept.get(firstPosition)).get(secondPosition);
                        titleArray[1] = "检查日期:" + DateUtils.convertDate2(m.examineDate);
                        titleArrayList2.add(titleArray);
                    }
                } else {
                    Toast.makeText(MaintenanceListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    maxPage--;
                }
                RecycleViewAdapter2 adapter2 =
                        new RecycleViewAdapter2(this, titleArrayList2, R.layout.item_recycle3);
                recyclerView.setAdapter(adapter2);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter2.setOnItemClickListener(this);
                break;

            case "MaintainTask":
            case "MinorRepair":
                maintainTaskList = (List<MaintainTask>) object;

                if (maintainTaskList.size() == pageSize) maxPage++;

                List<String[]> titleArrayList3 = new ArrayList<>();
                List<String[]> planData = new ArrayList<>(); //计划内任务与新增任务
                if (maintainTaskList != null && maintainTaskList.size() > 0) {
                    for (MaintainTask m : maintainTaskList) {
                        String[] titleArray = new String[2];
                        titleArray[0] = m.secondDeptName + "-" + m.thirdDeptName;
                        titleArray[1] = "日期:" + m.workYear + "-" + m.workMonth;
                        titleArrayList3.add(titleArray);

                        String[] planArray = new String[6];
                        planArray[0] = "已下达任务：" + m.planInsideTaskCount;
                        planArray[1] = "未核定：" + m.planInsideNotImpleCount;
                        planArray[2] = "已核定：" + m.planInsideImpleCount;
                        planArray[3] = "已新增任务：" + m.planOutsideTaskCount;
                        planArray[4] = "未核定：" + m.planOutsideNotImpleCount;
                        planArray[5] = "未核定：" + m.planOutsideImpleCount;
                        planData.add(planArray);
                    }
                } else {
                    Toast.makeText(MaintenanceListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    maxPage--;
                }
                RecycleViewAdapter2 adapter3 =
                        new RecycleViewAdapter2(this, titleArrayList3, R.layout.item_recycle3);
                adapter3.showMoreFlag(true);
                adapter3.setPlanData(planData);
                adapter3.setOnItemClickListener(this);
                recyclerView.setAdapter(adapter3);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                break;

            case "MaintainTaskItem":
                maintainTaskItemList = (List<MaintainTaskItem>) object;
                if (maintainTaskItemList != null && maintainTaskItemList.size() > 0) {
                    showBottomSheetDialog();
                } else {
                    Toast.makeText(this, "无数据", Toast.LENGTH_SHORT).show();
                }
                break;
            case "TaskDetail":
                //// TODO: 2016/10/27
                taskDetailList = (List<TaskDetail>) object;
                if (taskDetailList != null && taskDetailList.size() > 0) {
//                    BottomSheetDialog sheetDialog = new BottomSheetDialog(this);
//                    LayoutInflater inflater = LayoutInflater.from(this);
//                    View parent = inflater.inflate(R.layout.recycleview, null, false);
//                    RecyclerView recyclerView = (RecyclerView) parent.findViewById(R.id.recycleView);
//                    List<String> title = new ArrayList<>();
//                    title.add("类型");
//                    title.add("细项");
//                    title.add("主要作业内容");
//                    title.add("计划工程量");
//                    title.add("单位");
//                    title.add("核定工程量");
//                    TaskDetailAdapter adapter =
//                            new TaskDetailAdapter(MaintenanceListActivity.this, taskDetailList, title);
//
//                    GridLayoutManager manager =
//                            new GridLayoutManager(this, taskDetailList.size() + 1, GridLayoutManager.HORIZONTAL,
//                                    false);
//                    recyclerView.setLayoutManager(manager);
//                    recyclerView.setAdapter(adapter);
//                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
//                    params.height = 400;
//                    recyclerView.setLayoutParams(params);
//
//                    sheetDialog.setContentView(parent);
//
//                    sheetDialog.show();
                    final MyHorizontalScrollView2 parent = new MyHorizontalScrollView2(this, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setView(parent);
                    final List<List<String>> data = new ArrayList<>();
                    data.add(taskDetailList.get(0).getTitles());
                    for (int i = 0; i < taskDetailList.size(); i++) {
                        data.add(taskDetailList.get(i).getContent());
                    }
                    SparseIntArray sparseIntArray = new SparseIntArray();
                    sparseIntArray.put(1,150);
                    sparseIntArray.put(2,150);
                    sparseIntArray.put(3,100);
                    sparseIntArray.put(4,100);
                    sparseIntArray.put(5,100);
                    sparseIntArray.put(6,100);
                    sparseIntArray.put(7,100);
                    sparseIntArray.put(8,100);
                    sparseIntArray.put(9,100);
                    parent.setRectWidthAndHeight(sparseIntArray);
                    parent.setData(data);
                    parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            for (int i=0;i<parent.contentRV.getChildCount();i++) {
                                View view = parent.contentRV.getChildAt(i);
                                int measuredHeight = view.getMeasuredHeight();
                                View childAt = parent.nameRV.getChildAt(i);
                                ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                                layoutParams.height = measuredHeight;
                                childAt.setLayoutParams(layoutParams);
                            }
                            parent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    });
                    AlertDialog dialog = builder.create();

                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                } else {
                    Toast.makeText(this, "无数据", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    //从底部弹出窗口
    private void showBottomSheetDialog() {
        final MyHorizontalScrollView2 parent = new MyHorizontalScrollView2(this, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(parent);
        final List<List<String>> data = new ArrayList<>();
        data.add(maintainTaskItemList.get(0).getTitles());
        for (int i = 0; i < maintainTaskItemList.size(); i++) {
            data.add(maintainTaskItemList.get(i).getContent());
        }
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(1,100);
        sparseIntArray.put(2,100);
        sparseIntArray.put(3,100);
        sparseIntArray.put(4,100);
        sparseIntArray.put(5,100);
        sparseIntArray.put(6,100);
        sparseIntArray.put(7,100);
        sparseIntArray.put(8,100);
        sparseIntArray.put(9,100);
        parent.setRectWidthAndHeight(sparseIntArray);
        parent.setData(data);
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for (int i=0;i<parent.contentRV.getChildCount();i++) {
                    View view = parent.contentRV.getChildAt(i);
                    int measuredHeight = view.getMeasuredHeight();
                    View childAt = parent.nameRV.getChildAt(i);
                    ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                    layoutParams.height = measuredHeight;
                    childAt.setLayoutParams(layoutParams);
                }
                parent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        horizontalDialog = builder.create();
        parent.setOnItemClickListener(new MyHorizontalScrollView2.onItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                String maintainItemId = maintainTaskItemList.get(position ).id;
                String url = MyConstants.PRE_URL
                        + "mt/business/tinkermaintainpatrol/maintaintask/listTaskDetailJson.action?maintainTaskItemId="
                        + maintainItemId;

                presenter.getData(MaintenanceListActivity.this, url, "TaskDetail", 0);
                horizontalDialog.dismiss();
            }
        });

        horizontalDialog.setCanceledOnTouchOutside(true);
        horizontalDialog.show();

//        HorizotalScrollAdapter adapter = new HorizotalScrollAdapter(this,data);
//        RecyclerView recyclerView = (RecyclerView) parent.findViewById(R.id.recycleView);
//        MaintainTIAdapter adapter = new MaintainTIAdapter(maintainTaskItemList, this);
//        adapter.setOnClickListener(new MaintainTIAdapter.onClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                sheetDialog.dismiss();
//                //请求数据,下面的数字8，表示有8列
//                String maintainItemId = maintainTaskItemList.get((position + 1) / 8 - 2).id;
//                String url = MyConstants.PRE_URL
//                        + "mt/business/tinkermaintainpatrol/maintaintask/listTaskDetailJson.action?maintainTaskItemId="
//                        + maintainItemId;
//
//                presenter.getData(MaintenanceListActivity.this, url, "TaskDetail", 0);
//                sheetDialog.dismiss();
//            }
//        });
//        GridLayoutManager manager =
//                new GridLayoutManager(this, maintainTaskItemList.size() + 1, GridLayoutManager.HORIZONTAL,
//                        false);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(manager);
//        recyclerView.setAdapter(adapter);

//        sheetDialog.setContentView(parent);

//        sheetDialog.show();

    }

    @Override
    public void loadDataFailure() {
        Toast.makeText(this, "数据结构出错。", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hasError() {
        Toast.makeText(this, "数据结构出错。", Toast.LENGTH_SHORT).show();
    }

    //设置两个spinner的内容
    protected void setSpinnerData() {
        List<Dept> firstDeptList = deptG.listFirstDept;
        final List<Dept> secondDeptList = deptG.listSecondDept;
        List<Dept> thirdDeptList = deptG.listThirdDept;
        for (Dept d : firstDeptList) {
            firstDept.add(d.name);
            List<String> temp = new ArrayList<String>();
            for (Dept d2 : secondDeptList) {
                if (d2.parentId.equals(d.id)) {
                    temp.add(d2.name);
                }
                List<String> temp2 = new ArrayList<String>();
                temp2.add("请选择");
                for (Dept d3 : thirdDeptList) {
                    if (d3.parentId.equals(d2.id)) {
                        temp2.add(d3.name);
                    }
                }
                thirdDept.put(d2.name, temp2);
            }
            secondDept.put(d.name, temp);
        }
//        inflateSpnnier();

        //        firstAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, firstDept);
        //        firstSpinner.setAdapter(firstAdapter);

        //        firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //
        //            @Override
        //            public void onItemSelected(AdapterView<?> arg0, View arg1,
        //                                       int position, long arg3) {
        //                secondAdapter = new ArrayAdapter<String>(MaintenanceListActivity.this, android.R.layout.simple_spinner_item, secondDept.get(firstDept.get(position)));
        //                secondSpinner.setAdapter(secondAdapter);
        //                firstPosition = position;
        //            }
        //
        //            @Override
        //            public void onNothingSelected(AdapterView<?> arg0) {
        //
        //            }
        //        });

        //        secondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //
        //            @Override
        //            public void onItemSelected(AdapterView<?> arg0, View arg1,
        //                                       int position, long arg3) {
        //
        //                firstDeptId = deptG.listFirstDept.get(firstPosition).id;
        //
        //                String selectedDeptName = secondDept.get(firstDept.get(firstPosition)).get(position);
        //                for (Dept d : deptG.listSecondDept) {
        //                    if (d.name.equals(selectedDeptName)) {
        //                        secondDeptId = d.id;
        //                        break;
        //                    }
        //                }
        //
        //                thirdAdapter = new ArrayAdapter<String>(MaintenanceListActivity.this, android.R.layout.simple_spinner_item, thirdDept.get(selectedDeptName));
        //                thirdSpinner.setAdapter(thirdAdapter);
        //                secondPosition = position;
        //                //重新置为第一页
        //                pageNo = 1;
        //                maxPage = 1;
        //
        //            }
        //
        //            @Override
        //            public void onNothingSelected(AdapterView<?> arg0) {
        //
        //            }
        //        });

        //        thirdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //
        //            @Override
        //            public void onItemSelected(AdapterView<?> arg0, View arg1,
        //                                       int position, long arg3) {
        //
        //                String selectedDeptName = thirdDept.get(secondDept.get(firstDept.get(firstPosition)).get(secondPosition)).get(position);
        //                if (selectedDeptName.equals("请选择")) {
        //                    thirdDeptId = "";
        //                } else {
        //                    for (Dept d : deptG.listThirdDept) {
        //                        if (d.name.equals(selectedDeptName)) {
        //                            thirdDeptId = d.id;
        //                            break;
        //                        }
        //                    }
        //                }
        //                //重新置为第一页
        //                pageNo = 1;
        //                maxPage = 1;
        //
        //            }
        //
        //            @Override
        //            public void onNothingSelected(AdapterView<?> arg0) {
        //
        //            }
        //        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.perPage:
                if (pageNo != 1 && !TextUtils.isEmpty(appendUrl)) {
                    pageNo--;
                    pageNo_tv.setText(pageNo + "");
                    presenter.getData(MaintenanceListActivity.this, appendUrl, type, pageNo);
                } else {
                    Toast.makeText(MaintenanceListActivity.this, "当前是最新页", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nextPage:
                if (pageNo < maxPage && !TextUtils.isEmpty(appendUrl)) {
                    pageNo++;
                    pageNo_tv.setText(pageNo + "");
                    presenter.getData(MaintenanceListActivity.this, appendUrl, type, pageNo);
                } else {
                    Toast.makeText(MaintenanceListActivity.this, "当前已经是最后一页", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        int viewId = view.getId();
        if (view instanceof TextView) {
            String inOutPlanType = null;
            switch (viewId) {
                case R.id.plan_tv:
                    inOutPlanType = "0";
                    break;
                case R.id.add_tv:
                    inOutPlanType = "1";
                    break;
            }
            String url = MyConstants.PRE_URL
                    + "mt/business/tinkermaintainpatrol/maintaintask/listMaintainTaskItemJson.action?maintainTaskId="
                    + maintainTaskList.get(position).id
                    + "&inOutPlanType="
                    + inOutPlanType;
            presenter.getData(MaintenanceListActivity.this, url, "MaintainTaskItem", 1);
            return;
        }
        switch (type) {
            case "MaintainRegister":
                Intent intent = new Intent(MaintenanceListActivity.this, DetailActivity.class);
                intent.putExtra("title", toolbar.getTitle().toString());
                ArrayList<String> content =
                        (ArrayList<String>) maintainRegisterList.get(position).getContent();
                intent.putExtra("content", content);
                intent.putExtra("sessionId", maintainRegisterList.get(position).sessionId);
                intent.putExtra("subtitle", (Serializable) Arrays.asList(new String[]{"检查单位", "检查时间"}));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void setOnItemLongClickListener(View view, int position) {

    }
}
