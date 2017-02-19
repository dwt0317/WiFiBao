package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.utils.UseRecordManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.bmob.v3.BmobUser;

/**
 * 查看用户历史记录
 */
public class UseRecordActivity extends Activity {

    private int[] icon = {R.mipmap.potrait};//图标
    private ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();

    private ListView recordListView;
    private ArrayList<UseRecord> useRecordList;
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
                Intent intent = new Intent(UseRecordActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        Handler uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    useRecordList = UseRecordManager.getInstance(UseRecordActivity.this).getUseRecordList();
                    if(useRecordList.size()!=0)
                        updateRecordListView();
                }else{
                    Toast.makeText(UseRecordActivity.this, "Please wait..", Toast.LENGTH_LONG).show();
                }
            }
        };

        UseRecordManager.getInstance(this).setUiHandler(uiHandler);
        UseRecordManager.getInstance(this).queryUseRecord(BmobUser.getCurrentUser(UseRecordActivity.this, User.class));
    }

    private void updateRecordListView(){
        int size=useRecordList.size();
        for (int i = 0; i < size; i++) {
            UseRecord item = useRecordList.get(i);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("week", getWeek(item));
            map.put("date", formatDate(item.getStartTime().getDate()));
            map.put("Image", icon[0]);
            map.put("money","-"+item.getCost().toString()+" 流量币");
            map.put("wifi", item.getWiFi().getSSID());
            Item.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(this, Item, R.layout.ues_item, new String[]{"week", "date","Image","money","wifi"},
                new int[]{R.id.weekView,R.id.dateView,R.id.portraitView, R.id.moneyView,R.id.wifiView});
        recordListView.setAdapter(saImageItems);
        recordListView.setTextFilterEnabled(true);

    }

    private String getWeek(UseRecord useRecord){
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            date = sdf.parse(useRecord.getStartTime().getDate());
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