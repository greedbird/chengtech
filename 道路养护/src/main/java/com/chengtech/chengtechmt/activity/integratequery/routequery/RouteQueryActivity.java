package com.chengtech.chengtechmt.activity.integratequery.routequery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.chengtech.chengtechmt.BaseActivity;
import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.entity.Route;
import com.chengtech.chengtechmt.view.TitleLayout;

import java.util.ArrayList;
import java.util.List;

public class RouteQueryActivity extends BaseActivity {

    public TabLayout tabLayout;
    public ViewPager viewPager;
    public Route route;
    public List<Fragment> fragmentList;
    public String[] mTitle = new String[]{"路线卡片", "历史病害","历史小维","历史小额"
    ,"历史项目","历史评定"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_route_query);

        Intent intent = getIntent();
        route = (Route) intent.getSerializableExtra("data");

        tabLayout = (TabLayout) findViewById(R.id.id_tabLayout);

        initData();

        initView();

        setNavigationIcon(true);
        hidePagerNavigation(true);
        toolbar.setTitle(route.name);

    }

    private void initData() {
        fragmentList = new ArrayList<>();
        Bundle bundle = new Bundle();
        //路线信息卡片
        RouteFragment routeFragment = new RouteFragment();
        if (route!=null) {
            bundle.putString("routeId",route.id);
            bundle.putSerializable("data",route);
            routeFragment.setArguments(bundle);
        }

        //历史病害
        HistoryDiseaseFragment historyDiseaseFragment = new HistoryDiseaseFragment();
        historyDiseaseFragment.setArguments(bundle);

        TaskFragment taskFragment = new TaskFragment();
        taskFragment.setArguments(bundle);

        //历史小额专项维修
        MediumPlanprogressFragment mediumPlanprogressFragment = new MediumPlanprogressFragment();
        mediumPlanprogressFragment.setArguments(bundle);

        //历史项目
        ProjectManagementFragment projectManagementFragment = new ProjectManagementFragment();
        projectManagementFragment.setArguments(bundle);

        SixFragment frag6 = new SixFragment();
        fragmentList.add(routeFragment);
        fragmentList.add(historyDiseaseFragment);
        fragmentList.add(taskFragment);
        fragmentList.add(mediumPlanprogressFragment);
        fragmentList.add(projectManagementFragment);
        fragmentList.add(frag6);


    }

    private void initView() {

        viewPager = (ViewPager) findViewById(R.id.id_viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
//                Toast.makeText(RouteQueryActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mTitle.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle[position];
            }

        });

        tabLayout.setupWithViewPager(viewPager);

    }
}
