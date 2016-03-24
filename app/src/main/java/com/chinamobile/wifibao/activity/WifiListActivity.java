package com.chinamobile.wifibao.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import cn.bmob.v3.Bmob;
import android.app.Activity;
import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.UseManager;
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

    ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();
    ArrayList<WiFi> wifiList;
    ListView wifiListView;
    ImageView settingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");
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
                   wifiList = UseManager.getInstance(WifiListActivity.this,this).getWifiList();
                   updateWiFiListView();
                }else{
                    Toast.makeText(WifiListActivity.this,  "Please wait..", Toast.LENGTH_LONG).show();
                }
            }
        };

        UseManager.getInstance(this,uiHandler).getAvailableWiFi();

    }

    public void updateWiFiListView(){
        Iterator iter = wifiList.iterator();
        int size=wifiList.size();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Image", icon[0]);
            map.put("Title", wifiList.get(i).getSSID());
            map.put("Subtitle", "当前接入人数：" + wifiList.get(i).getCurConnect());
            map.put("Score", wifiList.get(i).getScore());
            Item.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(this, Item, R.layout.item, new String[]{"Image", "Title","Subtitle","Score"},
                new int[]{R.id.portraitView, R.id.ssidView,R.id.curConnectView,R.id.score});
        wifiListView.setAdapter(saImageItems);

        wifiListView.setTextFilterEnabled(true);
        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Object listItem = listview.getItemAtPosition(position);  把下一个activity写到这里就好了
            }
        });

    }

}
