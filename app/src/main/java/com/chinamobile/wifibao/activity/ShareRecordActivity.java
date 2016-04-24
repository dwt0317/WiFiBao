package com.chinamobile.wifibao.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.UseRecord;

import java.util.ArrayList;
import java.util.HashMap;

public class ShareRecordActivity extends AppCompatActivity {

    private int[] icon = {R.mipmap.potrait};//图标
    private ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();

    private ListView recordListView;
    private ArrayList<UseRecord> recordList;
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
        updateRecordListView();
    }



    public void updateRecordListView(){
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
        SimpleAdapter saImageItems = new SimpleAdapter(this, Item, R.layout.share_item, new String[]{"week", "date","Image","money","wifi"},
                new int[]{R.id.week,R.id.date,R.id.portrait, R.id.money,R.id.wifi});
        recordListView.setAdapter(saImageItems);
        recordListView.setTextFilterEnabled(true);

    }
}
