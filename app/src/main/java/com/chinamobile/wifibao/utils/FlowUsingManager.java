package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.WiFi;

import java.util.List;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by dwt on 2016/3/31.
 */
public class FlowUsingManager {
    private static FlowUsingManager instance;
    private Context mContext;
    private Handler uiHandler;


    public static synchronized FlowUsingManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new FlowUsingManager(context);
        }
        return instance;
    }

    private FlowUsingManager(Context context)
    {
        this.setmContext(context);
    }


    public void disconnectWiFi(WiFi wifi){
        String SSID = wifi.getSSID();
        WifiManager wifiManager = (WifiManager) getmContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.disconnect();
        //删除之前添加的conf
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\"")){
                wifiManager.removeNetwork(existingConfig.networkId);
            }
        }
    }

    public void updateUseRecord(UseRecord useRecord){
        useRecord.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i("bmob", "add use record done!");
            }
            @Override
            public void onFailure(int code, String arg0) {
                Log.e("bmob", "add use record fail!");
            }
        });
    }

    public void updateOwnerIncome(){

    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public Handler getUiHandler() {
        return uiHandler;
    }

    public void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }
}
