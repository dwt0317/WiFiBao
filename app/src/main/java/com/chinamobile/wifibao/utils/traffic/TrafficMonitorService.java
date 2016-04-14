package com.chinamobile.wifibao.utils.traffic;

import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Method;
import java.text.DecimalFormat;


/**
 * Created by cdd on 2016/3/27.
 */
public class TrafficMonitorService extends Thread {
    private static final String METHOD_GET_WIFI_AP_STATE = "getWifiApState";
    private static final int WIFI_AP_STATE_ENABLING = 12;
    private static final int WIFI_AP_STATE_ENABLED = 13;

    private Handler handler;
    private Context context;
    private double maxShare;
    private static TrafficMonitorService tms;

    private TrafficMonitorService(){}
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    public void setContext(Context context){
        this.context = context;
    }
    public void setMaxShare(double maxShare){
        this.maxShare = maxShare;
    }

    public static TrafficMonitorService getInstance(){
        if(tms == null)
            tms = new TrafficMonitorService();
        return tms;
    }

    @Override
    public void run() {
        //避免异常
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            Log.e("cdd:", "exception, ", e);
        }
        TrafficInfoProvider tp = new TrafficInfoProvider(context);
        //线程启动时历史记录,已使用总流量，软件已使用总流量
        tp.initOldTotalTraffic(tp.getTotalTraffic());
        tp.initOldAllAppTraffic(tp.getAllAppTraffic());
        while(true){
            Message message = Message.obtain();
            if(getWiFiApState()!=WIFI_AP_STATE_ENABLING &&
                    getWiFiApState()!=WIFI_AP_STATE_ENABLED)
                break;
            long apTraffic = tp.getWifiApTotalTraffic();
            double apTrafficMBytesDouble = apTraffic*1.0/1048576;
            DecimalFormat df = new DecimalFormat("#0.00");
            String sR = df.format(apTrafficMBytesDouble)+"MB";
            message.obj = sR;
            if(apTrafficMBytesDouble > maxShare){
                //告诉主线程到达流量上限
                message.arg1=0;
                handler.sendMessage(message);
                break;
            }else{
                message.arg1=1;
                handler.sendMessage(message);
            }
            try {
                Thread.currentThread().sleep(1500);
            } catch (InterruptedException e) {
                Log.e("cdd:","exception, ",e);
                message.arg1=0;
                handler.sendMessage(message);
            }
        }
    }

    /***
     * 获取热点状态，默认返回的结果是热点开启
     * @return
     */
    private int getWiFiApState() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int apState = WIFI_AP_STATE_ENABLED;
        try {
            String name = METHOD_GET_WIFI_AP_STATE;
            Method method = WifiManager.class.getMethod(name);
            apState =  (int)method.invoke(wifiManager);
        } catch (Exception e) {
            Log.e("cdd:", "SecurityException", e);
        }
        return apState;
    }

}
