package com.chinamobile.wifibao.utils.traffic;

/**
 * Created by cdd on 2016/3/27.
 */
public class TrafficInfo {
    private static final String TAG = "TrafficInfo";

    //应用的包名
    private String packName;
    //应用的名称
    private String appName;
    //uid
    private int uid;
    //上传的数据
    private long tx;
    //下载的数据
    private long rx;

    public String getPackName() {
        return packName;
    }
    public void setPackName(String packName) {
        this.packName = packName;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public long getTx() {
        return tx;
    }
    public void setTx(long tx) {
        if(tx == -1)
            this.tx=0L;
        else
            this.tx = tx;
    }
    public long getRx() {
        return rx;
    }
    public void setRx(long rx) {
        if(rx == -1)
            this.rx = 0L;
        else
            this.rx = rx;
    }
}
