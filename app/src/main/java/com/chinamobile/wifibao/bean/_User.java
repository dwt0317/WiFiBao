package com.chinamobile.wifibao.bean;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by dwt on 2016/3/16.
 */
public class _User extends BmobUser{
    private String mobilePhoneNumber;
    private Double remainedFlow;
    private Double useFlow;
    private Double balance;
    private String portrait;
    private BmobGeoPoint location;


    @Override
    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    @Override
    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public Double getRemainedFlow() {
        return remainedFlow;
    }

    public void setRemainedFlow(Double remainedFlow) {
        this.remainedFlow = remainedFlow;
    }

    public Double getUseFlow() {
        return useFlow;
    }

    public void setUseFlow(Double useFlow) {
        this.useFlow = useFlow;
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

}
