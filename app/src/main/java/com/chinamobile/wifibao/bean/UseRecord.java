package com.chinamobile.wifibao.bean;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by dwt on 2016/3/16.
 */
public class UseRecord {
    private BmobRelation WiFiId;
    private Double cost;
    private BmobDate startTime;
    private BmobDate endTime;
    private Double flowUsed;
    private BmobRelation userId;

    public BmobRelation getWiFiId() {
        return WiFiId;
    }

    public void setWiFiId(BmobRelation wiFiId) {
        WiFiId = wiFiId;
    }

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

    public BmobRelation getUserId() {
        return userId;
    }

    public void setUserId(BmobRelation userId) {
        this.userId = userId;
    }
}
