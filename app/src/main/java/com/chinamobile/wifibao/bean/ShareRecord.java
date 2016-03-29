package com.chinamobile.wifibao.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by dwt on 2016/3/16.
 */
public class ShareRecord extends BmobObject {
    private BmobObject WiFi;
    private Double income;
    private BmobDate startTime;
    private BmobDate endTime;
    private Double flowShared;
    private BmobUser user;


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

    public BmobObject getWiFi() {
        return WiFi;
    }

    public void setWiFi(BmobObject wiFi) {
        WiFi = wiFi;
    }

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }
}
