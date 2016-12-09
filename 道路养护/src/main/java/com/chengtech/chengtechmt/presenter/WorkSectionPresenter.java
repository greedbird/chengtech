package com.chengtech.chengtechmt.presenter;

import android.content.Context;

import com.chengtech.chengtechmt.activity.IView;
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

/**
 * 作者: LiuFuYingWang on 2016/5/17 9:13.
 */
public class WorkSectionPresenter implements Presenter<IView,Object> {

    private Section section;
    private WorkSection workSection;
    private SafetyFacilities safetyFacilities;
    private Greening greening;
    private Observatory observatory;
    private Slope slope;
    private Bridge bridge;
    private Tunnel tunnel;
    private Culvert culvert;

    private Tree tree;
    private IView workSectionView;

    public WorkSectionPresenter(IView view,int type) {
        this.attachView(view);
        switch (type) {
            case 1:
                section = new Section(this);
                break;
            case 2:
                workSection= new WorkSection(this);
                break;
            case 3:
                safetyFacilities = new SafetyFacilities(this);
                break;
            case 4:
                greening = new Greening(this);
                break;
            case 5:
                observatory = new Observatory(this);
                break;
            case 7:
                slope = new Slope(this);
                break;
            case 8:
                bridge = new Bridge(this);
                break;
            case 9:
                tunnel = new Tunnel(this);
                break;
            case 10:
                culvert = new Culvert(this);
                break;
        }
        tree = new Tree(this);


    }

    @Override
    public void attachView(IView view) {
        workSectionView = view;
    }

    @Override
    public void detchView(IView view) {
        workSectionView = null;
    }

    @Override
    public void loadDataSuccess(Object t) {

    }

    @Override
    public void loadDataSuccess(Object s, int type) {
        workSectionView.dismssDialog();
        workSectionView.loadDataSuccess(s,type);
    }

    @Override
    public void loadDataSuccess(Object o, String className) {

    }

    @Override
    public void loadDataFailed() {
        workSectionView.dismssDialog();
        workSectionView.loadDataFailure();
    }

    @Override
    public void hasError() {
        workSectionView.hasError();
    }

    public void getData(Context context,int pageNo,int pageSize,int type,String arg) {
        workSectionView.showDialog();
        switch (type) {
            case 0:
                tree.getData(context,arg);
                break;
            case 1:
                section.getData(context,pageNo,pageSize,type,arg);
                break;
            case 2:
                workSection.getData(context,pageNo,pageSize,type,arg);
                break;
            case 3:
                safetyFacilities.getData(context,pageNo,pageSize,type,arg);
                break;
            case 4:
                greening.getData(context,pageNo,pageSize,type,arg);
                break;
            case 5:
                observatory.getData(context,pageNo,pageSize,type,arg);
                break;
            case 7:
                slope.getData(context,pageNo,pageSize,type,arg);
                break;
            case 8:
                bridge.getData(context,pageNo,pageSize,type,arg);
                break;
            case 9:
                tunnel.getData(context,pageNo,pageSize,type,arg);
                break;
            case 10:
                culvert.getData(context,pageNo,pageSize,type,arg);
                break;
        }
    }

    public int getTotalRows(int type) {
        int totalRows=0;
        switch (type) {
            case 1:
                totalRows = this.section.totalRows;
                break;
            case 2:
                totalRows = this.workSection.totalRows;
                break;
            case 3:
                totalRows = this.safetyFacilities.totalRows;
                break;
            case 4:
                totalRows = this.greening.totalRows;
                break;
            case 5:
                totalRows = this.observatory.totalRows;
                break;
            case 7:
                totalRows = this.slope.totalRows;
                break;
            case 8:
                totalRows = this.bridge.totalRows;
                break;
            case 9:
                totalRows = this.tunnel.totalRows;
                break;
            case 10:
                totalRows = this.culvert.totalRows;
                break;
        }

        return totalRows;
    }
}
