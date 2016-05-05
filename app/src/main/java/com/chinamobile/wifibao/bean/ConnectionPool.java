package com.chinamobile.wifibao.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by dwt on 2016/4/6.
 */
public class ConnectionPool extends BmobObject {
    private WiFi WiFi;
    private Double cost;
    private Double flowUsed;
    private User user;
    private Integer maxConnect;
    private Integer curConnect;

    public com.chinamobile.wifibao.bean.WiFi getWiFi() {
        return WiFi;
    }

    public void setWiFi(com.chinamobile.wifibao.bean.WiFi wiFi) {
        WiFi = wiFi;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getFlowUsed() {
        return flowUsed;
    }

    public void setFlowUsed(Double flowUsed) {
        this.flowUsed = flowUsed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getMaxConnect() {
        return maxConnect;
    }

    public void setMaxConnect(Integer maxConnect) {
        this.maxConnect = maxConnect;
    }

    public Integer getCurConnect() {
        return curConnect;
    }

    public void setCurConnect(Integer curConnect) {
        this.curConnect = curConnect;
    }
}
