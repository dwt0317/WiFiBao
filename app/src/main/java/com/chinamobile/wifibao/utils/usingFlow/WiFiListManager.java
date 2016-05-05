package com.chinamobile.wifibao.utils.usingFlow;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.chinamobile.wifibao.bean.ConnectionPool;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by dwt on 2016/3/31.
 */
public class WiFiListManager {
    private static WiFiListManager instance;
    private ArrayList<WiFi> wifiList = new ArrayList<WiFi>();      //可用的热点宝wifi
    private ArrayList<User> ownerList = new ArrayList<User>();    //可用的热点宝wifi的拥有者
    private ArrayList<WiFi> dbNearbyWiFi = new ArrayList<WiFi>();    //在数据库查询到的附近的wifi
    private Context mContext;
    private Handler uiHandler;


    public static synchronized WiFiListManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new WiFiListManager(context);
        }
        return instance;
    }

    private WiFiListManager(Context context)
    {
        this.mContext = context;
    }

    public void getAvailableWiFi(){
        double[] userLoc = {12.0,33.0};
        getWifiList().clear();
        ownerList.clear();
        dbNearbyWiFi.clear();
        enableWiFi();
        readDBNearbyWiFi(userLoc);
    }


//    private void readDBNearbyWiFi(double[] userLoc){
//        BmobQuery<WiFi> bmobQuery = new BmobQuery<WiFi>();
////        bmobQuery.addWhereNear("location", userPoint);
//        bmobQuery.include("user");
//        bmobQuery.setLimit(20);    //获取最接近用户地点的20条数据
//        bmobQuery.findObjects(mContext, new FindListener<WiFi>() {
//            @Override
//            public void onSuccess(List<WiFi> object) {
//                dbNearbyWiFi = new ArrayList<WiFi>(object);
//                compareWiFiList();
//                Message msg = new Message();
//                msg.what = 1;
//                getUiHandler().sendMessage(msg);
//            }
//
//            @Override
//            public void onError(int code, String msg) {
//                Log.e("wifi", "read wifi fail");
//                Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
//                toast.show();
//            }
//        });
//    }

    private void readDBNearbyWiFi(double[] userLoc){
        BmobQuery<ConnectionPool> bmobQuery = new BmobQuery<ConnectionPool>();
//        bmobQuery.addWhereNear("location", userPoint);
//        bmobQuery.include("user");
        bmobQuery.include("WiFi");
        bmobQuery.setLimit(20);    //获取最接近用户地点的20条数据
        bmobQuery.findObjects(mContext, new FindListener<ConnectionPool>() {
            @Override
            public void onSuccess(List<ConnectionPool> object) {
                for(int i=0;i<object.size();i++){
                    dbNearbyWiFi.add(object.get(i).getWiFi());
                }
                compareWiFiList();
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e("wifi", "read wifi fail");
                Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
            }
        });
    }



    private void compareWiFiList(){
        String wserviceName = Context.WIFI_SERVICE;
        WifiManager wm = (WifiManager) mContext.getSystemService(wserviceName);
        wm.startScan();
        List<ScanResult> results = wm.getScanResults();
        ArrayList<String> scanIDList= new ArrayList<String>();
        for(ScanResult result:results){
            scanIDList.add(result.BSSID.toLowerCase());
            Log.i("wifi", result.SSID);
            Log.i("wifi", result.BSSID);
        }

        for(WiFi wifi: dbNearbyWiFi){
            if(scanIDList.contains(wifi.getBSSID().toLowerCase())&&wifi.getState()==true){
                getWifiList().add(wifi);
            }
        }
    }

    //检测wifi是否开启并开启

    private void enableWiFi(){
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
            Toast toast=Toast.makeText(mContext, "正在打开WLAN...", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
            toast.show();
            while(!wifiManager.isWifiEnabled()){
                try {
                    Thread.currentThread();
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private boolean isWiFiActive() {
        Context context = mContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    public ArrayList<WiFi> getWifiList() {
        return wifiList;
    }

    public void setWifiList(ArrayList<WiFi> wifiList) {
        this.wifiList = wifiList;
    }

    public Handler getUiHandler() {
        return uiHandler;
    }

    public void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }
}
