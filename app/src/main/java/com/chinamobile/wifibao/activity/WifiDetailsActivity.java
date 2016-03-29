package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;

import java.util.ArrayList;

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
        TextView textView = (TextView) findViewById(R.id.wifiname);
        String ssid = bundle.getString("SSID");
        textView.setText(ssid);
//        textView.setText("1111");

        Button button = (Button)findViewById(R.id.use_start);//获取按钮资源
        button.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
                Intent intent = new Intent(WifiDetailsActivity.this, FlowUsingActivity.class);
                Bundle bundle=new Bundle();
                //传递参数
                bundle.putString("flowUsing","99" );
                intent.putExtras(bundle);
                startActivity(intent);

                startActivity(intent);
            }
        });


    }
}