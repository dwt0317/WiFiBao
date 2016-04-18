package com.chinamobile.wifibao.utils.usingFlow;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.chinamobile.wifibao.bean.ConnectionPool;
import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
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


    public void disconnect(WiFi wifi,UseRecord useRecord,double flowDiff){
//        disconnectWiFi(wifi);
        updateUseInfo(wifi,flowDiff);
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
                updateUserBalance(wifi,useRecord);
            }
            @Override
            public void onFailure(int code, String arg0) {
                Log.e("bmob", "add use record fail!");
            }
        });
    }


    /**
     * @param wifi
     * 更新connectionPool中的分享的cost和flow
     */
    public void updateUseInfo(WiFi wifi, final double trafficDiff){
        BmobQuery<ConnectionPool> query = new BmobQuery<ConnectionPool>();
        query.addWhereEqualTo("WiFi", wifi);    // 查询当前用户的所有帖子
        query.findObjects(mContext, new FindListener<ConnectionPool>() {
            @Override
            public void onSuccess(List<ConnectionPool> object) {
                ConnectionPool conn = object.get(0);
                conn.setFlowUsed(trafficDiff);
                double cost = trafficDiff * 0.00005;
                conn.setCost(cost);
                conn.update(mContext,conn.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.i("bmob", "流量花费更新成功");
                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        Log.e("bmob", "更新失败：" + msg);
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i("bomb", "写入cost和flow成功 "+trafficDiff);
            }
            @Override
            public void onError(int code, String msg) {
                Log.e("bomb","写入cost和flow失败");
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void updateUserBalance(final WiFi wifi, final UseRecord useRecord){
        User user = User.getCurrentUser(mContext,User.class);
        user.setBalance(user.getBalance() + useRecord.getCost());
        user.update(mContext, user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.i("bmob", "balance 更新成功");
                disconnectWiFi(wifi);
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i("bmob", "balance 更新失败：" + msg);
                disconnectWiFi(wifi);
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
