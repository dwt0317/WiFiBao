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
        recordListView= (ListView) findViewById(R.id.wifiListView);

        //返回HomeActivity
        ImageView home = (ImageView) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UseRecordActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


        Handler uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    recordList = UseRecordManager.getInstance(UseRecordActivity.this).getUseRecordList();
                    recordsSepByMonth =  UseRecordManager.getInstance(UseRecordActivity.this).getRecordsSepByMonth();
                    updateRecordListView();
                }else{
                    Toast.makeText(UseRecordActivity.this, "Please wait..", Toast.LENGTH_LONG).show();
                }
            }
        };

        UseRecordManager.getInstance(this).setUiHandler(uiHandler);
        UseRecordManager.getInstance(this).queryUseRecord(BmobUser.getCurrentUser(UseRecordActivity.this,User.class));
    }

    private void updateRecordListView(){

    }
}
