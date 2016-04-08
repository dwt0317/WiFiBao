package com.chinamobile.wifibao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import cn.bmob.v3.Bmob;
import android.app.Activity;
import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.WiFiListManager;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class WifiListActivity extends Activity {

    private int[] icon = {R.mipmap.potrait};//图标

    private ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();
    private ArrayList<WiFi> wifiList;
    private ArrayList<WiFi>userList;
    private ListView wifiListView;
    private ImageView settingView;
    private Handler updateListHandler = new Handler();
    public  final static String wifiListSER_KEY = "com.chinamobile.wifibao.ser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_list);
        setViewComponent();
    }
    private void setViewComponent() {
        // mTxtResult = (TextView) findViewById(R.id.txtResult);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        wifiListView= (ListView) findViewById(R.id.wifiListView);
        settingView = (ImageView) findViewById(R.id.setting);

        //2016/3/23
        //wifiList是一个 ArrayList<WiFi>的实例


        Handler uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    wifiList = WiFiListManager.getInstance(WifiListActivity.this).getWifiList();
                    updateWiFiListView();
                }else{
                    Toast.makeText(WifiListActivity.this,  "Please wait..", Toast.LENGTH_LONG).show();
                }
            }
        };

        WiFiListManager.getInstance(this).setUiHandler(uiHandler);
        WiFiListManager.getInstance(this).getAvailableWiFi();

        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(WifiListActivity.this, WifiDetailsActivity.class);
                Bundle bundle = new Bundle();
                //传递参数
                bundle.putSerializable(wifiListSER_KEY, wifiList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }


    private Runnable updateListRunnable = new Runnable() {
        @Override
        public void run() {
            WiFiListManager.getInstance(WifiListActivity.this).getAvailableWiFi();
            updateWiFiListView();
            updateListHandler.postDelayed(updateListRunnable,5000);
        }
    };


    public void updateWiFiListView(){
        int size=wifiList.size();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Image", icon[0]);
            map.put("SSID", wifiList.get(i).getSSID());
            map.put("CurCon", "当前接入人数：" + wifiList.get(i).getCurConnect());
            map.put("Score", wifiList.get(i).getScore());
            Item.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(this, Item, R.layout.item, new String[]{"Image", "SSID","CurCon","Score"},
                new int[]{R.id.portraitView, R.id.ssidView,R.id.curConnectView,R.id.score});
        wifiListView.setAdapter(saImageItems);
        wifiListView.setTextFilterEnabled(true);

    }
}
