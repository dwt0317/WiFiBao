package com.chinamobile.wifibao.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by dwt on 2016/3/16.
 */
public class UseRecord extends BmobObject{
    private WiFi WiFi;
    private Double cost;
    private BmobDate startTime;
    private BmobDate endTime;
    private Double flowUsed;
    private User user;


    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public BmobDate getStartTime() {
        return startTime;
    }

    public void setStartTime(BmobDate startTime) {
        this.startTime = startTime;
    }

    public BmobDate getEndTime() {
        return endTime;
    }

    public void setEndTime(BmobDate endTime) {
        this.endTime = endTime;
    }

    public Double getFlowUsed() {
        return flowUsed;
    }

    public void setFlowUsed(Double flowUsed) {
        this.flowUsed = flowUsed;
    }


    public com.chinamobile.wifibao.bean.WiFi getWiFi() {
        return WiFi;
    }

    public void setWiFi(com.chinamobile.wifibao.bean.WiFi wiFi) {
        WiFi = wiFi;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
