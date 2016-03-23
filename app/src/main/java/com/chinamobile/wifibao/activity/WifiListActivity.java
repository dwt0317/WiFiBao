package com.chinamobile.wifibao.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import cn.bmob.v3.Bmob;
import android.app.Activity;
import com.chinamobile.wifibao.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    private int[] icon = {R.drawable.potrait};//图标
//    private String[] title = {"WiFi 1", "WiFi 2"};//wifi名字
//    private String[] subtitle = {"已使用流量:2054 MB", "已使用流量:478 MB"};//wifi具体信息
//    private String[] score = {"9.3", "8.1"};//wifi评分

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");
        setContentView(R.layout.activity_main);
        setViewComponent();
    }
    private void setViewComponent() {
        // mTxtResult = (TextView) findViewById(R.id.txtResult);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        final ListView listview = (ListView) findViewById(R.id.listView);
        ImageView imageView = (ImageView) findViewById(R.id.setting);

        //2016/3/23
        //wifiList是一个 ArrayList<WiFi>的实例
        ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();
        Iterator iter = wifList.iterator();
        int size=wifiList.size();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Image", icon[0]);
            map.put("Title", wifiList.get(i).getSSID());
            map.put("Subtitle", "当前接入人数：" + (String)wifiList.get(i).getcurConnect());
            map.put("Score", wifiList.get(i).getScore());
            Item.add(map);
        }

        SimpleAdapter saImageItems = new SimpleAdapter(this, Item, R.layout.item, new String[]{"Image", "Title","Subtitle","Score"},
                new int[]{R.id.imageView, R.id.title,R.id.subtitle,R.id.score});
        listview.setAdapter(saImageItems);

        listview.setTextFilterEnabled(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Object listItem = listview.getItemAtPosition(position);  把下一个activity写到这里就好了
            }
        });

//        imageView.setOnClickListener((View.OnClickListener) imageView);
//        //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Object listItem = listview.getItemAtPosition(position);  把下一个activity写到这里就好了

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
