package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.bean.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by dwt on 2016/3/17.
 */
public class UseManager {
    private static UseManager instance;
    private ArrayList<WiFi> wifiList = new ArrayList<WiFi>();      //可用的热点宝wifi
    private ArrayList<User> ownerList = new ArrayList<User>();    //可用的热点宝wifi的拥有者
    private ArrayList<WiFi> dbNearbyWiFi = new ArrayList<WiFi>();    //在数据库查询到的附近的wifi
    private Context mContext;
    private Handler uiHandler;

    private UseRecord useRecord = new UseRecord();




    public static synchronized UseManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new UseManager(context);
        }
        return instance;
    }

    private UseManager(Context context)
    {
        this.mContext = context;
    }



    public void getAvailableWiFi(){
        double[] userLoc = {12.0,33.0};
        wifiList.clear();
        ownerList.clear();
        dbNearbyWiFi.clear();
        enableWiFi();
        readDBNearbyWiFi(userLoc);
    }

    public void getOwnerList(){
        for(WiFi wifi: getWifiList()){
            BmobUser user = new BmobUser();
            BmobQuery<User> query = new BmobQuery<User>();
            query.addWhereRelatedTo("userId", new BmobPointer(user) );    // 查询当前wifi的用户
            query.findObjects(mContext, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> object) {
                    ownerList.add(object.get(0));
                    Message msg = new Message();
                    msg.what = 1;
                    getUiHandler().sendMessage(msg);
                }

                @Override
                public void onError(int code, String msg) {
//                    toast("查询失败:"+msg);
                }
            });
        }
    }

    private void readDBNearbyWiFi(double[] userLoc){
        BmobGeoPoint userPoint = new BmobGeoPoint(userLoc[0], userLoc[1]);
        BmobQuery<WiFi> bmobQuery = new BmobQuery<WiFi>();
//        bmobQuery.addWhereNear("location", userPoint);
        bmobQuery.setLimit(20);    //获取最接近用户地点的20条数据
        bmobQuery.findObjects(mContext, new FindListener<WiFi>() {
            @Override
            public void onSuccess(List<WiFi> object) {
                dbNearbyWiFi = new ArrayList<WiFi>(object);
                for (WiFi wifi : object) {
                    //获得playerName的信息
                    Log.i("wifi", wifi.getSSID());
                    Log.i("wifi", dbNearbyWiFi.get(0).getBSSID());
                }
                compareWiFiList();
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
            }

            @Override
            public void onError(int code, String msg) {
                Log.i("wifi", "read wifi fail");
            }
        });
    }

    private void compareWiFiList(){
        String wserviceName = Context.WIFI_SERVICE;
        WifiManager wm = (WifiManager) mContext.getSystemService(wserviceName);
        List<ScanResult> results = wm.getScanResults();
        ArrayList<String> scanIDList= new ArrayList<String>();
        for(ScanResult result:results){
            scanIDList.add(result.BSSID);
            Log.i("wifi-mac",result.BSSID);
        }

        for(WiFi wifi: dbNearbyWiFi){
            if(scanIDList.contains(wifi.getBSSID())){
                getWifiList().add(wifi);
            }
        }
    }

    public boolean connectWiFi(WiFi wifi){
        String BSSID = wifi.getBSSID();
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        WiFi connect=null;
        for(WiFi wifiItem: getWifiList()){
            if(wifiItem.getBSSID().equals(BSSID))
                connect = wifiItem;
        }
        if(connect==null){
            Log.e("UseWiFi", "No such WiFi found!");
            return false;
        }

        String networkSSID = connect.getSSID();
        String networkPass = connect.getPassword();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        conf.preSharedKey = "\""+ networkPass +"\"";

        enableWiFi();

        //删除之前添加的conf
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.BSSID.equals("\""+BSSID+"\"")){
                wifiManager.removeNetwork(existingConfig.networkId);
            }
        }
        try{
            int netId = wifiManager.addNetwork(conf);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            BmobDate startTime = new BmobDate(new Date());
            useRecord.setStartTime(startTime);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void disconnectWiFi(WiFi wifi){
        String BSSID = wifi.getBSSID();
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.disconnect();
        updateUseRecord(wifi);
        //删除之前添加的conf
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.BSSID.equals("\""+BSSID+"\"")){
                wifiManager.removeNetwork(existingConfig.networkId);
            }
        }
    }

    //检测wifi是否开启并开启
    private void enableWiFi(){
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
            while(!wifiManager.isWifiEnabled()){
                Toast.makeText(mContext, "正在打开WLAN..", Toast.LENGTH_SHORT).show();
                try {
                    Thread.currentThread();
                    Thread.sleep(1500);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //待测试
    private void updateUseRecord(WiFi wifi){
        BmobDate endTime = new BmobDate(new Date());
        useRecord.setEndTime(endTime);
        useRecord.setWiFi(wifi);
        useRecord.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i("bmob","add use record done!");
            }

            @Override
            public void onFailure(int code, String arg0) {
                Log.e("bmob", "add use record fail!");
            }
        });
    }

    public void testConnect(){
        String networkSSID = "qqqqq";
        String networkPass = "12121212";

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        conf.preSharedKey = "\""+ networkPass +"\"";

        //删除之前添加的conf
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        //检测wifi是否开启
        if (!wifiManager.isWifiEnabled())
        {
            Toast.makeText(mContext, "正在打开WLAN..", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
            while(!wifiManager.isWifiEnabled()){
            }
        }

        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\""+networkSSID+"\"")){
                wifiManager.removeNetwork(existingConfig.networkId);
            }
        }

        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    public ArrayList<WiFi> getWifiList() {
        return wifiList;
    }

    public Handler getUiHandler() {
        return uiHandler;
    }

    public void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }
}
