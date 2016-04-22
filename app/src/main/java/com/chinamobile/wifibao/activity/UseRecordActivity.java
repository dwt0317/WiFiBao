package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.utils.UseRecordManager;
import com.chinamobile.wifibao.utils.usingFlow.WiFiListManager;

import java.util.ArrayList;
import java.util.HashMap;

import cn.bmob.v3.BmobUser;

/**
 * Created by dwt on 2016/4/21.
 */
public class UseRecordActivity extends Activity {

    private int[] icon = {R.mipmap.potrait};//图标
    private ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();

    private ListView recordListView;
    private ArrayList<UseRecord> recordList;
    private HashMap<String,Integer> recordsSepByMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.use_record);
        setViewComponent();
    }

    private void setViewComponent() {
        recordListView= (ListView) findViewById(R.id.useListView);

        //返回HomeActivity
        ImageView home = (ImageView) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UseRecordActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


//        Handler uiHandler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if(msg.what == 1){
//                    recordList = UseRecordManager.getInstance(UseRecordActivity.this).getUseRecordList();
//                    recordsSepByMonth =  UseRecordManager.getInstance(UseRecordActivity.this).getRecordsSepByMonth();
//                    updateRecordListView();
//                }else{
//                    Toast.makeText(UseRecordActivity.this, "Please wait..", Toast.LENGTH_LONG).show();
//                }
//            }
//        };
//
//        UseRecordManager.getInstance(this).setUiHandler(uiHandler);
//        UseRecordManager.getInstance(this).queryUseRecord(BmobUser.getCurrentUser(UseRecordActivity.this,User.class));
    }

    private void updateRecordListView(){

    }


    public void updateWiFiListView(){
        //int size=recordList.size();
        int size=3;
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("week", "周 一");
            map.put("date", "04-22");
            map.put("Image", icon[0]);
            //map.put("money", recordList.get(i).getMoney());
            map.put("money","+6.8 流量币");
            map.put("wifi", "Wifi 2");
            Item.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(this, Item, R.layout.ues_item, new String[]{"week", "date","Image","money","wifi"},
                new int[]{R.id.weekView,R.id.dateView,R.id.portraitView, R.id.moneyView,R.id.wifiView});
        recordListView.setAdapter(saImageItems);
        recordListView.setTextFilterEnabled(true);

    }
}