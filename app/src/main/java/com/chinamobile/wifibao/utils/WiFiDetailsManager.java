package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.chinamobile.wifibao.activity.WifiDetailsActivity;
import com.chinamobile.wifibao.bean.ConnectionPool;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by dwt on 2016/3/31.
 */
public class WiFiDetailsManager {

    private static WiFiDetailsManager instance;
    private Context mContext;
    private Handler uiHandler;
    private User selectedUser;
    private Handler wifiDetectHandler= new Handler();

    public static synchronized WiFiDetailsManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new WiFiDetailsManager(context);
        }
        return instance;
    }

    private WiFiDetailsManager(Context context)
    {
        this.setmContext(context);
    }


    public void queryUser(WiFi wifi){
        User user = wifi.getUser();
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(mContext, user.getObjectId(), new GetListener<User>() {
            public void onSuccess(User object) {
                selectedUser = object;
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
                // TODO Auto-generated method stub
                Log.i("bmob", "query user successfully");
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

    private void isExceedingConnLimit(WiFi wifi, final Handler handler){
        BmobQuery<ConnectionPool> query = new BmobQuery<ConnectionPool>();
        query.addWhereEqualTo("WiFi", wifi);
        query.findObjects(mContext, new FindListener<ConnectionPool>() {
            @Override
            public void onSuccess(List<ConnectionPool> object) {
                // TODO Auto-generated method stub
                ConnectionPool conn = object.get(0);
                Integer curConnect = conn.getCurConnect();
                Integer maxConnect = conn.getMaxConnect();
                Message msg = new Message();

//
                if (curConnect >= maxConnect)
                    msg.what = 0;
                else
                    msg.what = 1;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
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



    public void connectWiFi(final WiFi wifi){
        Handler connLimitHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    doConnect(wifi);
                }else{
                    Message errorMsg = new Message();
                    errorMsg.what = 0;
                    getUiHandler().sendMessage(errorMsg);
                }
            }
        };
        isExceedingConnLimit(wifi,connLimitHandler);
    }

    private boolean doConnect(WiFi wifi){
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
            wifiDetectHandler.postDelayed(wifiDetectRunnable,100);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检测wifi是否连接runnable
     */
    private Runnable wifiDetectRunnable  = new Runnable() {
        @Override
        public void run() {
            Toast toast=Toast.makeText(mContext, "正在连接...", Toast.LENGTH_SHORT);
            if(isWiFiActive()){
                toast.cancel();
                wifiDetectHandler.removeCallbacks(wifiDetectRunnable);
                wifiDetectHandler.removeMessages(0);
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
            }else{
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
                wifiDetectHandler.postDelayed(wifiDetectRunnable,2000);
            }
        }
    };



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

    private boolean isWiFiActive() {
        ConnectivityManager connManager = (ConnectivityManager)mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected())
            return true;
        else
            return false;
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

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }
}
