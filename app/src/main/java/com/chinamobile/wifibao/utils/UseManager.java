package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import android.os.Handler;

import com.chinamobile.wifibao.activity.WifiListActivity;
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
import cn.bmob.v3.listener.GetListener;
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
    private User selectedUser;




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


    private void readDBNearbyWiFi(double[] userLoc){
        BmobGeoPoint userPoint = new BmobGeoPoint(userLoc[0], userLoc[1]);
        BmobQuery<WiFi> bmobQuery = new BmobQuery<WiFi>();
//        bmobQuery.addWhereNear("location", userPoint);
        bmobQuery.setLimit(20);    //获取最接近用户地点的20条数据
        bmobQuery.findObjects(mContext, new FindListener<WiFi>() {
            @Override
            public void onSuccess(List<WiFi> object) {
                dbNearbyWiFi = new ArrayList<WiFi>(object);
                compareWiFiList();
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e("wifi", "read wifi fail");
                Toast toast=Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
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
            Log.i("wifi", result.SSID);
            Log.i("wifi", result.BSSID);
        }

        for(WiFi wifi: dbNearbyWiFi){
            if(scanIDList.contains(wifi.getBSSID())){
                getWifiList().add(wifi);
            }
        }
    }

    public void queryUser(WiFi wifi){
        User user = wifi.getUser();
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(mContext, user.getObjectId(), new GetListener<User>()  {
            public void onSuccess(User object) {
                selectedUser=object;
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
                // TODO Auto-generated method stub
                Log.i("bmob", "query user successfully");
            }

            @Override
            public void onFailure(int code, String arg0) {
                Log.e("bmob", "query user error");
                Toast toast=Toast.makeText(mContext, arg0, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
            }
        });
    }


    public boolean connectWiFi(WiFi wifi){
        String BSSID = wifi.getBSSID();
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);

        String networkSSID = wifi.getSSID();
        String networkPass = wifi.getPassword();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        conf.preSharedKey = "\""+ networkPass +"\"";

        enableWiFi();
        if(wifiManager.getConnectionInfo().getBSSID().equals(BSSID))
        {
            Toast toast=Toast.makeText(mContext, "您已接入此WiFi", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
            toast.show();
            return false;
        }


        //删除之前添加的conf
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+networkSSID+"\"")){
                wifiManager.removeNetwork(existingConfig.networkId);
            }
        }
        try{
            int netId = wifiManager.addNetwork(conf);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            BmobDate startTime = new BmobDate(new Date());

            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void disconnectWiFi(WiFi wifi){
        String SSID = wifi.getSSID();
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
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

    public int getWiFiLevel(WiFi wifi){
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();

        for (ScanResult result : results) {
            if(result.BSSID.equals(wifi.getBSSID()))
                return result.level;
        }
        return 0;
    }


    //检测wifi是否开启并开启
    private void enableWiFi(){
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
            while(!wifiManager.isWifiEnabled()){
                Toast toast=Toast.makeText(mContext, "正在打开WLAN...", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
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
    public void updateUseRecord(UseRecord useRecord){
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


    public ArrayList<WiFi> getWifiList() {
        return wifiList;
    }

    public Handler getUiHandler() {
        return uiHandler;
    }

    public void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public User getSelectedUser() {
        return selectedUser;
    }
}
