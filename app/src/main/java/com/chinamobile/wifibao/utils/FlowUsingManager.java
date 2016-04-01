package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.chinamobile.wifibao.activity.TestActivity2;
import com.chinamobile.wifibao.bean.ShareRecord;
import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

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


    public void disconnect(WiFi wifi,UseRecord useRecord){
//        disconnectWiFi(wifi);
        updateUseRecord(wifi, useRecord);
    }

    private void disconnectWiFi(WiFi wifi){
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

    private void updateUseRecord(final WiFi wifi,final UseRecord useRecord){
        useRecord.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i("bmob", "add use record done!");
                disconnectWiFi(wifi);
//                updateOwnerIncome(wifi, useRecord);
            }
            @Override
            public void onFailure(int code, String arg0) {
                Log.e("bmob", "add use record fail!");
            }
        });
    }



    private void updateOwnerIncome(final WiFi wifi, final UseRecord useRecord){
        User user = wifi.getUser();
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(mContext, user.getObjectId(), new GetListener<User>() {
            public void onSuccess(User owner) {
                owner.setBalance(owner.getBalance()+useRecord.getCost());
                owner.update(mContext,owner.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.i("bmob", "balance 更新成功");

                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        Log.i("bmob", "balance 更新失败：" + msg);
                    }
                });
            }

            @Override
            public void onFailure(int code, String arg0) {
                Log.e("bmob", "query user error");
                Toast toast = Toast.makeText(mContext, arg0, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
            }
        });

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
