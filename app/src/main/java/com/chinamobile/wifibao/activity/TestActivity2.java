package com.chinamobile.wifibao.activity;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.chinamobile.wifibao.bean.ConnectionPool;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.LoginManager;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class TestActivity2 extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test2);

        Bmob.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");
//        writeToDatabase();
//        Toast.makeText(TestActivity2.this,"1111111",Toast.LENGTH_LONG);
//        uh.testConnect();
//        scanNearbyWiFi();
//        updateWiFi();
//        login();
        writeConnectionPool();
    }




    public void scanNearbyWiFi(){
        String wserviceName = Context.WIFI_SERVICE;
        WifiManager wm = (WifiManager) this.getSystemService(wserviceName);
        List<ScanResult> results = wm.getScanResults();
        ArrayList<String> scanIDList= new ArrayList<String>();
        for(ScanResult result:results){
            Log.i("wifi:",result.SSID+" "+result.BSSID);
        }
    }


    public void updateWiFi(){

        final Handler uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    WiFi wifi = new WiFi();
                    wifi.setUser(user);
                    wifi.update(TestActivity2.this, "cTgRKKKO", new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i("bmob","更新成功：");
                        }
                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.i("bmob","更新失败："+msg);
                        }
                    });
                }else{
//                    TrafficMonitor.getInstance(FlowUsingActivity.this).disableTrafficMonitor();
                }
            }
        };


        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(this, "ec9b0e6af9", new GetListener<User>() {

            @Override
            public void onSuccess(User object) {
                // TODO Auto-generated method stub
                user = (User) object;
                int i = 0;

                Message msg = new Message();
                msg.what = 1;
                uiHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int code, String arg0) {
                // TODO Auto-generated method stub
            }
        });




    }

    public  void writeToDatabase(){
        WiFi wifi = new WiFi();
        wifi.setSSID("12312");
        User user = new User();
        user.setBalance(10.0);
        user.setUsername("1111111");
        user.setPassword("12312");
        BmobGeoPoint loc = new BmobGeoPoint(10.0,20.0);
        user.setLocation(loc);
        user.setMobilePhoneNumber("15221111111");
        user.setPortrait("/ddd");
        user.setRemainedFlow(12.0);
        user.setFlowShared(12.0);
        user.setFlowUsed(12.0);
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
//                toast("添加数据成功，返回objectId为：" + ", 数据在服务端的创建时间为：");
                System.out.println("sfsdfsdfsd!!!!!");
                Log.i("DB", "first activity--------onStart()");
            }

            @Override
            public void onFailure(int code, String arg0) {
                System.out.println("shibai!!!!!");
                Log.e("DB", "shibai");

                // 添加失败
            }
        });
    }

    public void login(){
        final Handler uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    Log.i("login","登陆成功");
                    Log.i("login", BmobUser.getCurrentUser(TestActivity2.this).getUsername());
                }else{
                   Log.e("login", "登陆失败");
                }
            }
        };

        LoginManager.getInstance(this).setUiHandler(uiHandler);
        LoginManager.getInstance(this).loginByAccount("ddd","123");
    }


    public void writeConnectionPool(){
        ConnectionPool conn = new ConnectionPool();
        WiFi wifi = new WiFi();
        wifi.setObjectId("cTgRKKKO");
        User user = new User();
        user.setObjectId("2im5888A");
        conn.setUser(user);
        conn.setWiFi(wifi);
        conn.update(TestActivity2.this, "59TP2223", new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.i("bmob", "更新成功：");
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Log.i("bmob", "更新失败：" + msg);
            }
        });
    }
}
