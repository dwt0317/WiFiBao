package com.chinamobile.wifibao.handler;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.bean._User;

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
    private ArrayList<_User> ownerList = new ArrayList<_User>();    //可用的热点宝wifi的拥有者
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


    public ArrayList<WiFi> getAvailableWiFi(double[] userLoc){
        readDBNearbyWiFi(userLoc);
        compareWiFiList();
        return wifiList;
    }

    public ArrayList<_User> getOwnerList(){
        for(WiFi wifi:wifiList){
            BmobUser user = new BmobUser();
            BmobQuery<_User> query = new BmobQuery<_User>();
            query.addWhereRelatedTo("userId", new BmobPointer(user) );    // 查询当前wifi的用户
            query.findObjects(mContext, new FindListener<_User>() {
                @Override
                public void onSuccess(List<_User> object) {
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

    public void readDBNearbyWiFi(double[] userLoc){
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

    public void compareWiFiList(){
        String wserviceName = Context.WIFI_SERVICE;
        WifiManager wm = (WifiManager) mContext.getSystemService(wserviceName);
        List<ScanResult> results = wm.getScanResults();
        ArrayList<String> scanIDList= new ArrayList<String>();
        for(ScanResult result:results){
            scanIDList.add(result.SSID);
        }

        for(WiFi wifi: dbNearbyWiFi){
            if(scanIDList.contains(wifi.getSSID())){
                wifiList.add(wifi);
            }
        }
    }

}
