package com.chengtech.chengtechmt.activity.dbm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.chengtech.chengtechmt.BaseActivity;
import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.activity.IView;
import com.chengtech.chengtechmt.entity.MapEntity;
import com.chengtech.chengtechmt.entity.bridge.Bridge;
import com.chengtech.chengtechmt.entity.Culvert;
import com.chengtech.chengtechmt.entity.Greening;
import com.chengtech.chengtechmt.entity.Observatory;
import com.chengtech.chengtechmt.entity.SafetyFacilities;
import com.chengtech.chengtechmt.entity.Section;
import com.chengtech.chengtechmt.entity.Slope;
import com.chengtech.chengtechmt.entity.Tree;
import com.chengtech.chengtechmt.entity.tunnel.Tunnel;
import com.chengtech.chengtechmt.entity.WorkSection;
import com.chengtech.chengtechmt.presenter.WorkSectionPresenter;
import com.chengtech.chengtechmt.util.CommonUtils;
import com.chengtech.chengtechmt.util.UserUtils;
import com.chengtech.nicespinner.NiceSpinner;
import com.shelwee.uilistview.ui.UiListView;
import com.shelwee.uilistview.ui.UiListView.ClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DbmListActivity extends BaseActivity implements OnClickListener, IView {
    public int type;
    public int[] subTitleResId;
    public int[] subTitlePropertyResId;
    public List<Section> sectionList;
    public List<WorkSection> workSectionList;
    public List<SafetyFacilities> safetyFacilitiesList;
    public List<Greening> greeningList;
    public List<Observatory> observatoryList;
    public List<Slope> slopeList;
    public List<Bridge> bridgeList;
    public List<Tunnel> tunnelList;
    public List<Culvert> culvertList;
    protected static final String BRIDGE = "bridge";
    protected static final String TUNNEL = "tunnel";
    protected static final String CARD_DATA = "data";
    private UiListView uiListView;
    private List<String> firstDept;
    private Map<String, List<String>> secondDept;
    private int firstPosition;
    //    private Spinner firstSpinner;
//    private Spinner secondSpinner;
    private ArrayAdapter<String> firstAdapter;
    private ArrayAdapter<String> secondAdapter;

    private String url;
    private List<Tree> trees;

    private int flag = 1;//该标记是用来区别是通过方式一：点击上下页请求，还是方式二：通过选择部门来更改的
    //如果为0，就是方式一，1就是方式二，

    /*private List<Tree> itemTree;
    private ExpandableListView expandableListView;
    private List<String> groupName;
    private Map<String, List<String>> childName;
    private MyExpandableAdapter2 adapter2;
    private View popup_layout;
    private ListView popup_listview;
    private TextView popup_title;
    private PopupWindowUtils popupWindowUtils;*/

    private String deptId = null;
    private String listJson;

    private WorkSectionPresenter presenter;
    public NestedScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_work_section);

        initView();

        initData();
        Intent intent = getIntent();

        type = intent.getIntExtra("type", 1);
        url = intent.getStringExtra("url");
        presenter = new WorkSectionPresenter(this, type);

        toolbar.setNavigationIcon(R.drawable.arrow_back2);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setAppBarLayoutScroll(false);
        initEvent();


        presenter.getData(this, 0, 0, 0, url);

    }

    private void inflateSpnnier() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dept_spinner_selector, null);
        LinearLayout parent = (LinearLayout) contentView.findViewById(R.id.parent);
        parent.getChildAt(4).setVisibility(View.VISIBLE);
        parent.getChildAt(5).setVisibility(View.VISIBLE);
        parent.getChildAt(0).setVisibility(View.GONE);
        parent.getChildAt(1).setVisibility(View.GONE);
        NiceSpinner firstS = (NiceSpinner) contentView.findViewById(R.id.secondDept);
        final NiceSpinner secondS = (NiceSpinner) contentView.findViewById(R.id.thirdDept);
        firstS.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondS.attachDataSource(secondDept.get(firstDept.get(position)));
                firstPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        secondS.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDept = secondDept.get(firstDept.get(firstPosition)).get(position);
                if (selectedDept.equals("全选")) {
                    for (Tree t : trees) {
                        if (t.text.equals(firstDept.get(firstPosition))) {
                            deptId = t.id;
                            for (Tree t2 : trees) {
                                if (t.id.equals(t2.parentId))
                                        deptId = deptId + "\',\'" + t2.id;

                            }
                            break;
                        }
                    }
                } else {
                    for (Tree t : trees) {
                        if (t.text.equals(selectedDept)) {
                            deptId = t.id;
                            break;
                        }
                    }
                }

                //重新开始计算页码
                pageNo = 1;
                maxPage = 1;
                pageNo_tv.setText(pageNo + "");
                flag = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        firstS.attachDataSource(firstDept);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(contentView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.getData(DbmListActivity.this, pageNo, pageSize, type, deptId);
            }
        });

        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();

        dialog.show();

        //设置layout的大小
//        LinearLayout parent = (LinearLayout) firstS.getParent();
//        ViewGroup.LayoutParams params = parent.getLayoutParams();
//        params.height = CommonUtils.dp2px(this,300);
//        params.width = (int) (mScreenWidth*0.7);
//        parent.setLayoutParams(params);

        //设置dialog大小与位置
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
//        dialogWindow.setGravity(Gravity.CENTER);
//        attributes.height = CommonUtils.dp2px(this,300);
//        attributes.width = (int) (mScreenWidth*0.7);
//        dialogWindow.setAttributes(attributes);
//        contentView.invalidate();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_work_section, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter) {
            inflateSpnnier();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        subTitleResId = new int[]{0, R.array.section_detail, R.array.worksection_detail,
                R.array.safetyFacilities_detail, R.array.greening_detail,
                R.array.observatory_detail, 0, R.array.slope_detail,
                0, 0, R.array.culvert_detail,
        };
        subTitlePropertyResId = new int[]{0, R.array.section_detail_property, R.array.worksection_detail_property,
                R.array.safetyFacilities_detail_property, R.array.greening_detail_property,
                R.array.observatory_detail_property, 0, R.array.safetyFacilities_detail_property,
                0, 0, R.array.culvert_detail_property,
        };
    }


    private void initEvent() {

        uiListView.setClickListener(new ClickListener() {

            @Override
            public void onClick(int index) {

                if (!toolbar.getTitle().toString().contains("桥梁卡片") && !toolbar.getTitle().toString().contains("隧道卡片")) {
                    Intent intent = new Intent(DbmListActivity.this, DetailActivity.class);
                    List<String> content = new ArrayList<String>();
                    String titleName = null;
                    String sessionId = null;
                    MapEntity mapEntity = null;
                    String mapData = null;
                    String poi = null;
                    switch (type) {
                        case 1:
                            Section section = sectionList.get(index);
                            sessionId = section.sessionId;
                            content = section.getContent();
                            titleName = section.name;
                            mapData = "[{\"code\":\""+section.code+"\",\"from\":"+
                                    CommonUtils.convertStake(section.startStake)+",\"to\":"+
                                    CommonUtils.convertStake(section.endStake)+",\"color\":\"0xC81623\"" +
                                    ",\"width\":6" +
                                    ",\"里程起点\":"+section.startStake+
                                    ",\"里程终点\":"+section.endStake+",\"管理单位\":"+section.mgDept.name+
                                    ",\"养护单位\":"+section.mtDept.name+",\"长度\":"+section.length+
                                    ",\"路面均宽\":"+section.roadWide+",\"路面类型\":"+section.roadType+
                                    ",\"技术等级\":"+section.techGrade+"}" +
//                                    ",{\"code\":\"S111\",\"from\":10.574,\"to\":20.363,\"color\":\"0xc669988\",\"width\":10}" +
                                    "]";
                            mapEntity = new MapEntity(null,null,null,null,mapData);
                            break;
                        case 2:
                            WorkSection workSection = workSectionList.get(index);
                            sessionId = workSection.sessionId;
                            content = workSection.getContent();
                            titleName = workSection.name;
                            mapData = "[{\"code\":\""+workSection.code+"\",\"from\":"+
                                    CommonUtils.convertStake(workSection.startStake)+",\"to\":"+
                                    CommonUtils.convertStake(workSection.endStake)+",\"color\":\"0xC81623\",\"width\":6" +
                                    ",\"里程起点\":"+workSection.startStake+",\"里程终点\":"+workSection.endStake+"" +
                                    ",\"起点名称\":"+workSection.startStakeName+",\"终点名称\":"+workSection.endStakeName+
                                    ",\"管理单位\":"+workSection.mgDept.name+"" + ",\"养护单位\":"+workSection.mtDept.name+"" +
                                    ",\"长度\":"+workSection.length+",\"路面均宽\":"+workSection.roadWide+"" +
                                    ",\"路面类型\":"+workSection.roadType+",\"技术等级\":"+workSection.techGrade+"}]";
                            mapEntity = new MapEntity(null,null,null,null,mapData);
                            break;
                        case 3:
                            SafetyFacilities safetyFacilities = safetyFacilitiesList.get(index);
                            sessionId = safetyFacilities.sessionId;
                            content = safetyFacilities.getContent();
                            titleName = safetyFacilities.name;
                            mapData = "[{\"name\":\"交安设施卡片信息\",\"x\":0,\"y\":0}]";
                            mapEntity = new MapEntity(null,null,null,null,mapData);
                            String[] photoUrls = safetyFacilities.picture.split(",,,");
                            intent.putExtra("photo", photoUrls);
                            break;
                        case 4:
                            Greening greening = greeningList.get(index);
                            sessionId = greening.sessionId;
                            content = greening.getContent();
                            titleName = greening.code;
                            poi = "{\"x\":"+greening.longitude+",\"y\":"+greening.latitude+"}";
                            mapData = "[{\"code\":\""+greening.code+"\",\"from\":"+
                                    CommonUtils.convertStake(greening.startStake)+",\"to\":"+
                                    CommonUtils.convertStake(greening.endStake)+",\"color\":\"0xC81623\",\"width\":4}]";
                            mapEntity = new MapEntity(null,null,null,poi,mapData);
                            break;
                        case 5:
                            Observatory observatory = observatoryList.get(index);
                            sessionId = observatory.sessionId;
                            content = observatory.getContent();
                            titleName = observatory.name;
                            poi = "{\"x\":"+observatory.longitude+",\"y\":"+observatory.latitude+"}";
                            mapData = "[{\"name\":\"交通观测站卡片信息\",\"x\":"+observatory.longitude+",\"y\":"+observatory.latitude+
                                    ",\"交通观测站编号\":"+observatory.observatoryNumber+",\"交通观测站名称\":"+observatory.name+
                                    ",\"所属路线\":"+observatory.routeName+",\"所属养护段\":"+observatory.sectionCode+"}]";

                            mapEntity = new MapEntity(null,null,null,poi,mapData);
                            break;
                        case 7:
                            Slope slope = slopeList.get(index);
                            sessionId = slope.sessionId;
                            content = slope.getContent();
                            titleName = slope.code;
                            poi = "{\"x\":"+slope.longitude+",\"y\":"+slope.latitude+"}";
                            mapData = "[{\"code\":\""+slope.code+"\",\"from\":"+
                                    CommonUtils.convertStake(slope.startStake)+",\"to\":"+
                                    CommonUtils.convertStake(slope.endStake)+",\"color\":\"0xC81623\",\"width\":4}]";
                            mapEntity = new MapEntity(null,null,null,poi,mapData);
                            break;
                        case 10:
                            Culvert culvert = culvertList.get(index);
                            sessionId = culvert.sessionId;
                            content = culvert.getContent();
                            titleName = culvert.name;
                            poi = "{\"x\":"+culvert.longitude+",\"y\":"+culvert.latitude+"}";
                            mapData = "[{\"name\":\"涵洞卡片信息\",\"x\":"+culvert.longitude+",\"y\":"+culvert.latitude+",\"涵洞编号\":\"G325H0090440113\",\"涵洞名称\":\"\",\"路线编号\":\"G325\"}]";
                            mapEntity = new MapEntity(null,null,null,poi,mapData);
                            break;

                    }
                    intent.putExtra("title", titleName);
                    intent.putExtra("content", (Serializable) content);
                    intent.putExtra("sessionId", sessionId);
                    intent.putExtra("map", mapEntity);
                    intent.putExtra("subtitle", (Serializable) Arrays.asList(DbmListActivity.this.getResources().getStringArray(subTitleResId[type])));
                    intent.putExtra("subtitleProperty", (Serializable) Arrays.asList(DbmListActivity.this.getResources().getStringArray(subTitlePropertyResId[type])));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DbmListActivity.this, CardActivity.class);
                    String mapData = null;
                    if ("桥梁卡片".equals(toolbar.getTitle().toString())) {
                        intent.putExtra("title", bridgeList.get(index).name);
                        intent.putExtra("className", BRIDGE);
                        intent.putExtra("map",new MapEntity(null,null,null,null,mapData));
                        intent.putExtra(CARD_DATA, (Serializable) (bridgeList.get(index)));
                    } else if ("隧道卡片".equals(toolbar.getTitle().toString())) {
                        intent.putExtra("title", tunnelList.get(index).name);
                        intent.putExtra("className", TUNNEL);
                        intent.putExtra("map",new MapEntity(null,null,null,null,mapData));
                        intent.putExtra(CARD_DATA, (Serializable) (tunnelList.get(index)));

                    }

                    startActivity(intent);
                }

            }
        });

    }


    private void initView() {
        firstDept = new ArrayList<String>();
        secondDept = new HashMap<String, List<String>>();
        uiListView = (UiListView) findViewById(R.id.uilist);
        //expandableListView = (ExpandableListView) findViewById(R.id.worksection_expandlistview);
        trees = new ArrayList<Tree>();


        up_action.setOnClickListener(this);
        down_action.setOnClickListener(this);

//		DisplayMetrics metrics = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		mScreenWidth = metrics.widthPixels;
//		mScreenHeight = metrics.heightPixels;


        sv = (NestedScrollView) findViewById(R.id.scrollView);

    }

    protected void setSpinnerData() {
        for (Tree t : trees) {
            if (t.id.equals(t.secondDeptId)) {
                firstDept.add(t.text);
                List<String> temp = new ArrayList<String>();
                for (Tree t2 : trees) {
                    if (t.id.equals(t2.parentId)) {
                        temp.add(t2.text);
                    }
                }
                temp.add("全选");
                secondDept.put(t.text, temp);
            }
        }

    }

    @Override
    public void onClick(View v) {
        flag = 0;
        switch (v.getId()) {
            case R.id.perPage:
                if (pageNo != 1) {
                    pageNo--;
                    pageNo_tv.setText(pageNo + "");
                    presenter.getData(DbmListActivity.this, pageNo, pageSize, type, deptId);
                } else {
                    Toast.makeText(this, "当前是最新页", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nextPage:
                if (pageNo < maxPage) {
                    pageNo++;
                    pageNo_tv.setText(pageNo + "");
                    presenter.getData(DbmListActivity.this, pageNo, pageSize, type, deptId);
                } else {
                    Toast.makeText(DbmListActivity.this, "当前已经是最后一页", Toast.LENGTH_SHORT).show();
                }
                break;
        }

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
        switch (type) {
            case 0:
                trees = (List<Tree>) o;
                setSpinnerData();
                break;
            case 1:
                List<Section> sections = (List<Section>) o;

                if (sections != null && sections.size() == pageSize)
                    maxPage++;
                if (sections != null && sections.size() > 0) {
                    //先清除之前的缓存
                    uiListView.clear();
                    for (Section d : sections) {
                        uiListView.addBasicItem(TextUtils.isEmpty(d.name) ? d.code : d.name);
                    }
                    uiListView.commit();
                } else {
                    Toast.makeText(DbmListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    if (flag == 0) {
                        pageNo--;
                        maxPage--;
                    } else {
                        uiListView.clear();
                    }
                    pageNo_tv.setText(String.valueOf(pageNo));
                    break;
                }

                sectionList = sections;
                //释放对象，减少内存
                sections = null;
                break;
            case 2:
                List<WorkSection> workSections = (List<WorkSection>) o;
                if (workSections != null && workSections.size() == pageSize)
                    maxPage++;
                if (workSections != null && workSections.size() > 0) {
                    //先清除之前的缓存
                    uiListView.clear();
                    for (WorkSection d : workSections) {
                        uiListView.addBasicItem(TextUtils.isEmpty(d.name) ? d.code : d.code+"("+d.name+")");
                    }
                    uiListView.commit();
                } else {
                    Toast.makeText(DbmListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    if (flag == 0) {
                        pageNo--;
                        maxPage--;
                    } else {
                        uiListView.clear();
                    }
                    pageNo_tv.setText(String.valueOf(pageNo));
                    break;
                }

                workSectionList = workSections;
                //释放对象，减少内存
                workSections = null;
                break;
            case 3:
                List<SafetyFacilities> safetyFacilities = (List<SafetyFacilities>) o;

                if (safetyFacilities != null && safetyFacilities.size() == pageSize)
                    maxPage++;
                if (safetyFacilities != null && safetyFacilities.size() > 0) {
                    //先清除之前的缓存
                    uiListView.clear();
                    for (SafetyFacilities d : safetyFacilities) {
                        uiListView.addBasicItem(TextUtils.isEmpty(d.name) ? d.code : d.routeCode + "-" + d.name);
                    }
                    uiListView.commit();
                } else {
                    Toast.makeText(DbmListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    if (flag == 0) {
                        pageNo--;
                        maxPage--;
                    } else {
                        uiListView.clear();
                    }
                    pageNo_tv.setText(String.valueOf(pageNo));
                    break;
                }
                safetyFacilitiesList = safetyFacilities;
                //释放对象，减少内存
                safetyFacilities = null;
                break;
            case 4:
                List<Greening> greenings = (List<Greening>) o;

                if (greenings != null && greenings.size() == pageSize)
                    maxPage++;
                if (greenings != null && greenings.size() > 0) {
                    //先清除之前的缓存
                    uiListView.clear();
                    for (Greening d : greenings) {
                        uiListView.addBasicItem(TextUtils.isEmpty(d.name) ? d.routeCode+"("+d.code+")" : d.name);
                    }
                    uiListView.commit();

                } else {
                    Toast.makeText(DbmListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    if (flag == 0) {
                        pageNo--;
                        maxPage--;
                    } else {
                        uiListView.clear();
                    }
                    pageNo_tv.setText(String.valueOf(pageNo));
                    break;
                }

                greeningList = greenings;
                //释放对象，减少内存
                greenings = null;
                break;

            case 5:
                List<Observatory> observatories = (List<Observatory>) o;
                if (observatories != null && observatories.size() == pageSize)
                    maxPage++;
                if (observatories != null && observatories.size() > 0) {
                    //先清除之前的缓存
                    uiListView.clear();
                    for (Observatory d : observatories) {
                        uiListView.addBasicItem(TextUtils.isEmpty(d.name) ? d.code : d.routeCode+"("+d.name+")");
                    }
                    uiListView.commit();

                } else {
                    Toast.makeText(DbmListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    if (flag == 0) {
                        pageNo--;
                        maxPage--;
                    } else {
                        uiListView.clear();
                    }
                    pageNo_tv.setText(String.valueOf(pageNo));
                    break;
                }

                observatoryList = observatories;
                //释放对象，减少内存
                observatories = null;
                break;
            case 7:
                List<Slope> slopes = (List<Slope>) o;
                if (slopes != null && slopes.size() == pageSize)
                    maxPage++;
                if (slopes != null && slopes.size() > 0) {
                    //先清除之前的缓存
                    uiListView.clear();
                    for (Slope s : slopes) {
                        uiListView.addBasicItem(TextUtils.isEmpty(s.name) ? s.routeCode+"("+s.code+")" : s.name);
                    }
                    uiListView.commit();
                } else {
                    Toast.makeText(DbmListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    if (flag == 0) {
                        pageNo--;
                        maxPage--;
                    } else {
                        uiListView.clear();
                    }
                    pageNo_tv.setText(String.valueOf(pageNo));
                    break;
                }

                slopeList = slopes;
                //释放对象，减少内存
                observatories = null;

                break;

            case 8:
                List<Bridge> bridges = (List<Bridge>) o;
                if (bridges != null && bridges.size() == pageSize)
                    maxPage++;
                if (bridges != null && bridges.size() > 0) {
                    //先清除之前的缓存
                    uiListView.clear();
                    for (Bridge d : bridges) {
                        uiListView.addBasicItem(TextUtils.isEmpty(d.name) ? d.code : d.routeCode + "("
                                + d.name + ")");
                    }
                    uiListView.commit();

                } else {
                    Toast.makeText(DbmListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    if (flag == 0) {
                        pageNo--;
                        maxPage--;
                    } else {
                        uiListView.clear();
                    }
                    pageNo_tv.setText(String.valueOf(pageNo));
                    break;
                }

                bridgeList = bridges;
                //释放对象，减少内存
                bridges = null;
                break;
            case 9:
                List<Tunnel> tunnels = (List<Tunnel>) o;
                if (tunnels != null && tunnels.size() == pageSize)
                    maxPage++;
                if (tunnels != null && tunnels.size() > 0) {
                    //先清除之前的缓存
                    uiListView.clear();
                    for (Tunnel d : tunnels) {
                        uiListView.addBasicItem(TextUtils.isEmpty(d.name) ? d.code : d.name);
                    }
                    uiListView.commit();

                } else {
                    Toast.makeText(DbmListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    if (flag == 0) {
                        pageNo--;
                        maxPage--;
                    } else {
                        uiListView.clear();
                    }
                    pageNo_tv.setText(String.valueOf(pageNo));
                    break;
                }

                tunnelList = tunnels;
                //释放对象，减少内存
                tunnels = null;
                break;
            case 10:
                List<Culvert> culverts = (List<Culvert>) o;
                if (culverts != null && culverts.size() == pageSize)
                    maxPage++;
                if (culverts != null && culverts.size() > 0) {
                    //先清除之前的缓存
                    uiListView.clear();
                    for (Culvert d : culverts) {
                        uiListView.addBasicItem(TextUtils.isEmpty(d.name) ? d.code : d.name);
                    }
                    uiListView.commit();
                } else {
                    Toast.makeText(DbmListActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    if (flag == 0) {
                        pageNo--;
                        maxPage--;
                    } else {
                        uiListView.clear();
                    }
                    pageNo_tv.setText(String.valueOf(pageNo));
                    break;
                }

                culvertList = culverts;
                //释放对象，减少内存
                culverts = null;
                break;

        }
//        //srcollview滑动到顶端，必须使用post方法，放到消息队列。
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_UP);

            }
        });
    }

    @Override
    public void loadDataSuccess(Object o, String clasName) {

    }

    @Override
    public void loadDataFailure() {
        Toast.makeText(this, "连接服务器出错", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hasError() {
        Toast.makeText(this, "数据结构不正确！", Toast.LENGTH_SHORT).show();
        UserUtils.reLogin(DbmListActivity.this, loadDialog);
    }
}
