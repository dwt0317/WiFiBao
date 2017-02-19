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

import com.chinamobile.wifibao.activity.FlowUsingActivity;
import com.chinamobile.wifibao.bean.ConnectionPool;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 热点列表后台
 */
public class WiFiListManager {
    private static WiFiListManager instance;
    private ArrayList<WiFi> wifiList = new ArrayList<WiFi>();      //可用的热点宝wifi
    private ArrayList<User> ownerList = new ArrayList<User>();    //可用的热点宝wifi的拥有者
    private ArrayList<WiFi> dbNearbyWiFi = new ArrayList<WiFi>();    //在数据库查询到的附近的wifi
    private Context mContext;
    private Handler uiHandler;
    private Handler enableWiFiHandler;


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


    /**
     * 获取附近可用wifi
     */
    public void getAvailableWiFi(){
        final double[] userLoc = {12.0,33.0};
        getWifiList().clear();
        ownerList.clear();
        dbNearbyWiFi.clear();
        enableWiFiHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    readDBNearbyWiFi(userLoc);
                }
            }
        };
        enableWiFiHandler.post(enableWiFiRunnable);

    }


    /**
     * 获取数据库中离用户最近的20个可用热点
     */
    private void readDBNearbyWiFi(double[] userLoc){
        BmobQuery<ConnectionPool> bmobQuery = new BmobQuery<ConnectionPool>();
        bmobQuery.include("WiFi,WiFi.user");
        bmobQuery.setLimit(20);    //获取最接近用户地点的20条数据
        bmobQuery.findObjects(mContext, new FindListener<ConnectionPool>() {
            @Override
            public void onSuccess(List<ConnectionPool> connectionList) {
                for(int i=0;i<connectionList.size();i++){
                    WiFi wifi = connectionList.get(i).getWiFi();
                    wifi.setCurConnect(connectionList.get(i).getCurConnect());
                    dbNearbyWiFi.add(wifi);
                }
                compareWiFiList();
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e("bomb", "read wifi fail");
                Log.e("bomb",String.valueOf(code));
                Toast toast = Toast.makeText(mContext, "网络异常，请检查您的网络设置", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
            }
        });
    }


    /**
     * 将数据库热点与用户手机搜索到的热点进行比对
     */
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

    /**
     * 检测wifi是否开启并开启
     */
    private Runnable enableWiFiRunnable= new Runnable() {
        @Override
        public void run() {
            WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
            Message msg = new Message();
            msg.what=1;
            Toast toast = new Toast(mContext);
            if (!wifiManager.isWifiEnabled())
            {
                wifiManager.setWifiEnabled(true);
                toast.makeText(mContext, "正在打开WLAN..", Toast.LENGTH_SHORT).show();
                while(!wifiManager.isWifiEnabled()){
                    try {
                        Thread.currentThread();
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                toast.cancel();
                enableWiFiHandler.sendMessage(msg);
            }else
                enableWiFiHandler.sendMessage(msg);
        }
    };


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
