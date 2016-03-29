package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.net.TrafficStats;

import android.app.AlertDialog;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by dwt on 2016/3/22.
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
        double mStartRX = TrafficStats.getTotalRxBytes();
        double mStartTX = TrafficStats.getTotalTxBytes();
        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 1000);
        }
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            rxBytes = TrafficStats.getTotalRxBytes()- mStartRX;
            txBytes = TrafficStats.getTotalTxBytes()- mStartTX;
            totalTraffic =(rxBytes+txBytes);
            formatRst();
            Message msg = new Message();
            msg.what = 1;
            getUiHandler().sendMessage(msg);
            Log.i("traffic",mStartRX+" "+mStartTX);
        }
    };

    private void formatRst(){
        totalTraffic = totalTraffic/1024/1024;
        DecimalFormat df  = new DecimalFormat("######0.00");
        totalTrafficStr=df.format(totalTraffic);
    }

    public void refreshTraffic(){
        uiHandler.postDelayed(mRunnable,2000);
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
}
