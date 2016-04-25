package com.chinamobile.wifibao.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.ShareRecord;
import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.utils.ShareRecordManager;

import java.util.ArrayList;
import java.util.HashMap;

import cn.bmob.v3.BmobUser;

public class ShareRecordActivity extends AppCompatActivity {

    private int[] icon = {R.mipmap.potrait};//图标
    private ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();

    private ListView recordListView;
    private ArrayList<ShareRecord> recordList;
    private HashMap<String,Integer> recordsSepByMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_record);

        setViewComponent();

        //返回HomeActivity
        ImageView home = (ImageView) findViewById(R.id.tohome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareRecordActivity.this, Home2Activity.class);
                startActivity(intent);
            }
        });

    }

    private void setViewComponent() {
        recordListView = (ListView) findViewById(R.id.shareListView);
        final ShareRecordManager srm = ShareRecordManager.getInstance(ShareRecordActivity.this);
        updateRecordListView();

        Handler uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    recordList = srm.getShareRecordList();
                    recordsSepByMonth =  srm.getRecordsSepByMonth();
                    updateRecordListView();
                }else{
                    //Toast.makeText(ShareRecordActivity.this, "Please wait..", Toast.LENGTH_SHORT).show();
                }
            }
        };

        srm.setUiHandler(uiHandler);
        srm.queryShareRecord(BmobUser.getCurrentUser(ShareRecordActivity.this, User.class));
    }



    public void updateRecordListView(){
        int size;
        if(recordList != null){
            size=recordList.size();
        }else{
            size = 0;
        }

        ShareRecord sr;
        //int size=3;
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            sr = recordList.get(i);
            map.put("week", "周 一");
            map.put("date", "04-22");
            map.put("Image", icon[0]);
            //map.put("money", recordList.get(i).getMoney());
            map.put("money",sr.getIncome());
            map.put("wifi", "Wifi 2");
            Item.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(this, Item, R.layout.share_item, new String[]{"week", "date","Image","money","wifi"},
                new int[]{R.id.week,R.id.date,R.id.portrait, R.id.money,R.id.wifi});
        recordListView.setAdapter(saImageItems);
        recordListView.setTextFilterEnabled(true);

    }
}
