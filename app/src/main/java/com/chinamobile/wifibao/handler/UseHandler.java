package com.chinamobile.wifibao.handler;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by dwt on 2016/3/17.
 */
public class UseHandler {
    private static UseHandler instance;
    private ArrayList<WiFi> wifiList = new ArrayList<WiFi>();      //可用的热点宝wifi
    private ArrayList<User> ownerList = new ArrayList<User>();    //可用的热点宝wifi的拥有者
    private ArrayList<WiFi> dbNearbyWiFi = new ArrayList<WiFi>();    //在数据库查询到的附近的wifi
    private Context mContext;

    public static synchronized UseHandler getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new UseHandler(context);
        }
        return instance;
    }

    private UseHandler(Context context)
    {
        this.mContext = context;
    }


    public ArrayList<WiFi> getAvailableWiFi(){
        double[] userLoc = {12.0,33.0};
        readDBNearbyWiFi(userLoc);
        compareWiFiList();
        return wifiList;
    }

    public ArrayList<User> getOwnerList(){
        for(WiFi wifi:wifiList){
            BmobUser user = new BmobUser();
            BmobQuery<User> query = new BmobQuery<User>();
            query.addWhereRelatedTo("userId", new BmobPointer(user) );    // 查询当前wifi的用户
            query.findObjects(mContext, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> object) {
                    ownerList.add(object.get(0));
                }

                @Override
                public void onError(int code, String msg) {
//                    toast("查询失败:"+msg);
                }
            });
        }
        return ownerList;
    }

    private void readDBNearbyWiFi(double[] userLoc){
        BmobGeoPoint userPoint = new BmobGeoPoint(userLoc[0], userLoc[1]);
        BmobQuery<WiFi> bmobQuery = new BmobQuery<WiFi>();
        bmobQuery.addWhereNear("location", userPoint);
        bmobQuery.setLimit(20);    //获取最接近用户地点的20条数据
        bmobQuery.findObjects(mContext, new FindListener<WiFi>() {
            @Override
            public void onSuccess(List<WiFi> object) {
                // TODO Auto-generated method stub
                System.out.println("查询成功：共" + object.size() + "条数据。");
                dbNearbyWiFi =new ArrayList<WiFi>(object);
            }
            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                System.out.println("查询失败：" + msg);
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
        }

        for(WiFi wifi: dbNearbyWiFi){
            if(scanIDList.contains(wifi.getBSSID())){
                wifiList.add(wifi);
            }
        }
    }

    public boolean connectWiFi(String BSSID){
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        WiFi connect=null;
        for(WiFi wifi:wifiList){
            if(wifi.getBSSID().equals(BSSID))
                connect = wifi;
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
        Toast.makeText(mContext, "正在打开WLAN..", Toast.LENGTH_SHORT).show();

        //检测wifi是否开启
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

        //删除之前添加的conf
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.BSSID.equals("\""+BSSID+"\"")){
                wifiManager.removeNetwork(existingConfig.networkId);
            }
        }



        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        return true;
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
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+networkSSID+"\"")){
                wifiManager.removeNetwork(existingConfig.networkId);
            }
        }

        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

}
