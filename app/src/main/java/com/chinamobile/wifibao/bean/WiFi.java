package com.chinamobile.wifibao.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by dwt on 2016/3/16.
 */
public class WiFi extends BmobObject{
    private String SSID;
    private String BSSID;
    private String password;
    private Integer maxConnect;
    private Integer curConnect;
    private Double score;
    private Boolean state;
    private String WiFitype;
    private Double upperLimit;
    private BmobRelation userId;
    private BmobGeoPoint location;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getWiFitype() {
        return WiFitype;
    }

    public void setWiFitype(String wiFitype) {
        WiFitype = wiFitype;
    }

    public Double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public BmobRelation getUserId() {
        return userId;
    }

    public void setUserId(BmobRelation userId) {
        this.userId = userId;
    }


    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }
}
