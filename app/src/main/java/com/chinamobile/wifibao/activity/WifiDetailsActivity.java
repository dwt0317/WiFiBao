package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.usingFlow.WiFiDetailsManager;

import cn.bmob.v3.BmobUser;

/**
 * 查看热点信息
 */


public class WifiDetailsActivity extends Activity {

    private User user;
    //接收userid值
    TextView shareuserText;
    public final static String wifiDetailSER_KEY = "com.chinamobile.wifibao.wifiDetail";
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setViewComponent();
    }
    private void setViewComponent() {

        setContentView(R.layout.wifi_details);
        //新页面接收数据
        final WiFi wifi = (WiFi)this.getIntent().getSerializableExtra(WifiListActivity.wifiListSER_KEY);
       //接收ssid值
        TextView wifiname = (TextView) findViewById(R.id.wifiname);
        wifiname.setText(wifi.getSSID());

        shareuserText = (TextView) findViewById(R.id.shareuserText);
        shareuserText.setText(wifi.getUser().getUsername());

        //接收网络类型
        TextView tyepText = (TextView) findViewById(R.id.tyepText);
        tyepText.setText(wifi.getWiFitype());
        //获取wifi信号强度
        TextView sigstrText = (TextView) findViewById(R.id.sigstrText);
        sigstrText.setText(String.valueOf(WiFiDetailsManager.getInstance(this).getWiFiLevel(wifi)));
        //接收流量上限
        TextView maxflowText = (TextView) findViewById(R.id.maxflowText);
        maxflowText.setText(String.valueOf(wifi.getUpperLimit()));
        //接收接入人数
        TextView maxconnectText = (TextView) findViewById(R.id.maxconnectText);
        maxconnectText.setText(wifi.getMaxConnect().toString());

        //返回HomeActivity
        ImageView home = (ImageView) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiDetailsActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Button button = (Button)findViewById(R.id.use_start);//获取按钮资源
        button.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
                if(!isLogin()){
                    Intent loginIntent = new Intent(WifiDetailsActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }else{
                    final Intent intent = new Intent(WifiDetailsActivity.this, FlowUsingActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable(wifiDetailSER_KEY,wifi);
                    intent.putExtras(bundle);
                    //传递参数
                    Handler connectHandler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.what == 1){
                                WiFiDetailsManager.getInstance(WifiDetailsActivity.this).writeWiFitoCache(wifi,WifiDetailsActivity.this);
                                startActivity(intent);
                            }else if(msg.what==0){
                                Toast toast=Toast.makeText(getApplicationContext(), "已达到最大接入人数", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                                toast.show();
                            }
                        }
                    };
                    WiFiDetailsManager.getInstance(WifiDetailsActivity.this).setUiHandler(connectHandler);
                    WiFiDetailsManager.getInstance(WifiDetailsActivity.this).connectWiFi(wifi);
                }
            }
        });
    }
    private boolean isLogin(){
        BmobUser bmobUser = BmobUser.getCurrentUser(this);
        if(bmobUser == null)
            return false;
        else return true;
    }
}