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

                Toast.makeText(getApplicationContext(), "正在接入wifi...", Toast.LENGTH_SHORT).show();
//                Toast t2=Toast.makeText(this, "Toast text with specific position", Toast.LENGTH_LONG);
//                t2.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
//                t2.show();
                Intent intent = new Intent(WifiDetailsActivity.this, FlowUsingActivity.class);
                Bundle bundle=new Bundle();
                //传递参数
                bundle.putString("flowUsing","99" );
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }
}