package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apple on 2016/3/25.
 */


public class WifiDetailsActivity extends Activity {

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setViewComponent();
    }
    private void setViewComponent() {
        setContentView(R.layout.wifi_details);
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收ssid值
        TextView tV1 = (TextView) findViewById(R.id.wifiname);
        String ssid = bundle.getString("SSID");
        tV1.setText(ssid);
        //接收userid值
        TextView tV2 = (TextView) findViewById(R.id.shareuserText);
        String userid = bundle.getString("userID");
        tV2.setText(userid);
        //接收网络类型
        TextView tV3 = (TextView) findViewById(R.id.tyepText);
        String wifitype = bundle.getString("WiFitype");
        tV3.setText(wifitype);
        //接收信号强度
        TextView tV4 = (TextView) findViewById(R.id.sigstrText);
        Integer strength = bundle.getInt("strength");
        tV4.setText(strength);
        //接收流量上限
        TextView tV5 = (TextView) findViewById(R.id.maxflowText);
        String maxflow =String.valueOf(bundle.getDouble("upperLimit"));
        tV5.setText(maxflow);
        //接收接入人数
        TextView tV6 = (TextView) findViewById(R.id.curconnectText);
        int curcon = bundle.getInt("CurCon");
        tV6.setText(curcon);

        Button button = (Button)findViewById(R.id.use_start);//获取按钮资源
        button.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {

                final Intent intent = new Intent(WifiDetailsActivity.this, FlowUsingActivity.class);
                Bundle bundle=new Bundle();
                //传递参数
                bundle.putString("flowUsing","99" );
                intent.putExtras(bundle);

                Timer timer = new Timer();
                TimerTask tast = new TimerTask() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                };
                timer.schedule(tast,2000);

                Toast toast=Toast.makeText(getApplicationContext(), "正在接入wifi...", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();

            }
        });


    }
}