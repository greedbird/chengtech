package com.chengtech.chengtechmt.entity;

import java.io.Serializable;

/**
 * 作者: LiuFuYingWang on 2016/11/30 10:18.
 * 地图参数实体类
 */

public class MapEntity implements Serializable{

    public String layer;
    public String code;
    public String draw;
    public String poi;
    public String lineData;

    public MapEntity(String layer, String code, String draw, String poi,String lineData) {
        this.layer = layer;
        this.code = code;
        this.draw = draw;
        this.poi = poi;
        this.lineData = lineData;
    }
    public MapEntity() {
    }


    public MapEntity(String layer, String code, String draw, String poi) {
        this.layer = layer;
        this.code = code;
        this.draw = draw;
        this.poi = poi;
    }
}
