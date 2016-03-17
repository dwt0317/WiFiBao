package com.chinamobile.wifibao.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by dwt on 2016/3/16.
 */
public class ShareRecord extends BmobObject {
    private BmobRelation WiFiId;
    private Double income;
    private BmobDate startTime;
    private BmobDate endTime;
    private Double flowShared;
    private BmobRelation userId;


    public BmobRelation getWiFiId() {
        return WiFiId;
    }

    public void setWiFiId(BmobRelation wiFiId) {
        WiFiId = wiFiId;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
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

    public Double getFlowShared() {
        return flowShared;
    }

    public void setFlowShared(Double flowShared) {
        this.flowShared = flowShared;
    }

    public BmobRelation getUserId() {
        return userId;
    }

    public void setUserId(BmobRelation userId) {
        this.userId = userId;
    }
}
