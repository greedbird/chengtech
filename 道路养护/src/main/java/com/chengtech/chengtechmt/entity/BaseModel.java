package com.chengtech.chengtechmt.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者: LiuFuYingWang on 2016/5/16 14:22.
 */
public abstract class BaseModel implements Serializable{

    public String id;
    public String name ;
    public String code;
    public String sessionName;
    public String memo;
    public String sessionId;




    public abstract List<String> getContent();
}
