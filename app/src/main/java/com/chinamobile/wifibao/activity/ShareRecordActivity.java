package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.bmob.v3.BmobUser;
/**
 * Created by cdd on 2016/4/24.
 */
public class ShareRecordActivity extends Activity {

    private int[] icon = {R.mipmap.potrait};//图标
    private ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();

    private ListView recordListView;
    private ArrayList<ShareRecord> recordList;
    private HashMap<String,Integer> recordsSepByMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_record);

        Toast.makeText(ShareRecordActivity.this, "Please wait..", Toast.LENGTH_SHORT).show();
        setViewComponent();

        //返回HomeActivity
        ImageView home = (ImageView) findViewById(R.id.tohome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareRecordActivity.this, Home2Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        int size=0;
        if(recordList != null){
            size=recordList.size();
        }

        ShareRecord sr;;
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            sr = recordList.get(i);
            map.put("week", getWeek(sr));
            map.put("date", formatDate(sr.getStartTime().getDate()));
            map.put("Image", icon[0]);
            map.put("money",sr.getIncome()+"流量币");
            map.put("wifi", "热点:"+sr.getWiFi().getSSID());
            Item.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(this, Item, R.layout.share_item, new String[]{"week", "date","Image","money","wifi"},
                new int[]{R.id.week,R.id.date,R.id.portrait, R.id.money,R.id.wifi});
        recordListView.setAdapter(saImageItems);
        recordListView.setTextFilterEnabled(true);

    }

    private String getWeek(ShareRecord record){
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            date = sdf.parse(record.getStartTime().getDate());
            SimpleDateFormat weekdf = new SimpleDateFormat("EEEE");
            String week = weekdf.format(date);
            return week;
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
    private String formatDate(String date){
        String fdate="";
        String[] tmp =date.split(" ");
        String[] tmp2=tmp[0].split("-");
        fdate=tmp2[1]+"-"+tmp2[2];
        return fdate;
    }
}
