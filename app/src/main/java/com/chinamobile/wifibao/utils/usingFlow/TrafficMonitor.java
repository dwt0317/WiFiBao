package com.chinamobile.wifibao.utils.usingFlow;

import android.content.Context;
import android.net.TrafficStats;

import android.app.AlertDialog;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * 流量监控
 */
public class TrafficMonitor {

    private static TrafficMonitor instance;
    private Handler mHandler = new Handler();
    private Handler uiHandler;
    private Context mContext;
    private double rxBytes;
    private double txBytes;
    private double mStartRX;
    private double mStartTX;
    private double totalTraffic;
    private double lastTraffic;
    private double trafficDiff;    // 单位Bytes
    private String totalTrafficStr;

    public static synchronized TrafficMonitor getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new TrafficMonitor(context);
        }
        return instance;
    }

    private TrafficMonitor(Context context)
    {
        this.mContext = context;
    }

    public void startTrafficMonitor(){
        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();
        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 100);
        }
    }

    //runnable to update traffic
    private Runnable mRunnable = new Runnable() {
        public void run() {
            Message msg = new Message();
            WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
                txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
                totalTraffic = (rxBytes + txBytes);
                trafficDiff=(totalTraffic-lastTraffic);
                lastTraffic=totalTraffic;
                formatRst();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
                Log.i("traffic",mStartRX+" "+mStartTX);
            }else{
                msg.what = 0;
                getUiHandler().sendMessage(msg);
            }
        }
    };

    //转成mb并保留两位小数
    private void formatRst(){
        totalTraffic = totalTraffic/1024/1024;
        DecimalFormat df  = new DecimalFormat("######0.00");
        totalTrafficStr=df.format(totalTraffic);
    }

    public void refreshTraffic(){
        uiHandler.postDelayed(mRunnable, 5000);
    }




    public void disableTrafficMonitor(){
        uiHandler.removeCallbacks(mRunnable);
    }
    public Handler getUiHandler() {
        return uiHandler;
    }

    public void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }


    public String getTotalTrafficStr() {
        return totalTrafficStr;
    }

    public double getTrafficDiff() {
        return trafficDiff;
    }
}
