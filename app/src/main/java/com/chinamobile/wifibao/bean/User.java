package com.chinamobile.wifibao.bean;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by dwt on 2016/3/16.
 */
public class User extends BmobUser{
    private Double remainedFlow;
    private Double flowShared;
    private Double flowUsed;
    private Double balance;
    private String portrait;
    private BmobGeoPoint location;


    public Double getRemainedFlow() {
        return remainedFlow;
    }

    public void setRemainedFlow(Double remainedFlow) {
        this.remainedFlow = remainedFlow;
    }


    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public Double getFlowShared() {
        return flowShared;
    }

    public void setFlowShared(Double flowShared) {
        this.flowShared = flowShared;
    }

    public Double getFlowUsed() {
        return flowUsed;
    }

    public void setFlowUsed(Double flowUsed) {
        this.flowUsed = flowUsed;
    }
}
