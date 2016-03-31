package com.chinamobile.wifibao.utils.traffic;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Method;
import java.text.DecimalFormat;


/**
 * Created by cdd on 2016/3/27.
 */
public class TrafficMonitor extends Thread {
    private Handler handler;
    private Context context;

    private static final String METHOD_GET_WIFI_AP_STATE = "getWifiApState";
    private static final int WIFI_AP_STATE_ENABLING = 12;
    private static final int WIFI_AP_STATE_ENABLED = 13;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    public void setContext(Context context){
        this.context = context;
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
        //Message message = this.handler.obtainMessage();
        while(true){
            Message message = Message.obtain();
            if(getWiFiApState()!=WIFI_AP_STATE_ENABLING && getWiFiApState()!=WIFI_AP_STATE_ENABLED)break;
            //更新
            long apTraffic = tp.getWifiApTotalTraffic();
            double apTrafficKBytesDouble = apTraffic*1.0/1024;
            DecimalFormat df = new DecimalFormat("#0.00");
            String sR = df.format(apTrafficKBytesDouble)+"KB";
            message.obj = sR;
            if(apTrafficKBytesDouble > 600.00){
                //告诉主线程到达流量上限
                message.arg1=0;
                handler.sendMessage(message);
                break;
            }
            else{
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
