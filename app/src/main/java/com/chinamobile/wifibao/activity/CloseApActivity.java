package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.utils.ConnectedIP;
import com.chinamobile.wifibao.utils.WifiApAdmin;
import com.chinamobile.wifibao.utils.traffic.TrafficMonitorService;

import java.lang.reflect.Method;

/**
 * Created by cdd on 2016/3/16.
 */
public class CloseApActivity extends Activity{
    private Context mContext = null;
    private boolean stop = false;


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        mContext = this;
        setContentView(R.layout.flow_share);

        Toast.makeText(mContext,"热点已开启！",Toast.LENGTH_SHORT).show();
        //流量监测
        final TextView showFlow = (TextView) findViewById(R.id.tv11);
        final Handler flowHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String sR = (String) msg.obj;
                showFlow.setText(sR);
                if (msg.arg1 == 0) {
                    WifiApAdmin.closeWifiAp(mContext);
                    Toast.makeText(mContext, "超出上限，正在关闭热点！", Toast.LENGTH_LONG).show();
                }
            }
        };
        TrafficMonitorService monitorThread = TrafficMonitorService.getInstance();
        monitorThread.setHandler(flowHandle);
        monitorThread.setContext(mContext);
        monitorThread.setMaxShare(getIntent().getDoubleExtra("maxshare",0.0));
        monitorThread.start();
        //接入监测
        final TextView accessCount = (TextView)findViewById(R.id.tv21);
        final Handler accessHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String count = String.valueOf(msg.arg1);
                accessCount.setText(count);
            }
        };
        accessListenerThread listenerThread=new accessListenerThread();
        listenerThread.setHandler(accessHandle);
        listenerThread.setContext(mContext);
        listenerThread.start();

        //返回HomeActivity
        ImageView home = (ImageView)findViewById(R.id.imageView7);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CloseApActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        //refresh
        ImageView refresh = (ImageView)findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //线程会自动刷新的
                Toast.makeText(mContext,"正在刷新",Toast.LENGTH_SHORT).show();
            }
        });
        //close wifi ap
        Button stopBt = (Button)findViewById(R.id.share_stop);
        stopBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiApAdmin.closeWifiAp(mContext);
                Toast.makeText(mContext, "宝宝这就去睡觉", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CloseApActivity.this, BalanceShareActivity.class);
                intent.putExtra("flow", showFlow.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.scale_in, R.anim.alpha_out);
                CloseApActivity.this.finish();
            }
        });
    }//end onCreate

    private class accessListenerThread extends Thread{
        private Handler handler;
        private Context context;
        private static final int WIFI_AP_STATE_ENABLING = 12;
        private static final int WIFI_AP_STATE_ENABLED = 13;
        private static final String METHOD_GET_WIFI_AP_STATE = "getWifiApState";

        public void setHandler(Handler handler) {
            this.handler = handler;
        }
        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            ConnectedIP cp = ConnectedIP.getInstance();
            while(true){
                if(stop) break;
                Message mess = new Message();
                if(getWiFiApState()!=WIFI_AP_STATE_ENABLING && getWiFiApState()!=WIFI_AP_STATE_ENABLED){
                    mess.arg1 = 0;
                    handler.sendMessage(mess);
                    break;
                }else {
                    mess.arg1 = cp.getConnectedIpCount();
                    Log.i("ip:", String.valueOf(mess.arg1));
                    handler.sendMessage(mess);
                }
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    Log.e("cdd:", "Thread ex", e);
                }
            }
        }

        private int getWiFiApState() {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int apState = WIFI_AP_STATE_ENABLED;
            try {
                String name = METHOD_GET_WIFI_AP_STATE;
                Method method = WifiManager.class.getMethod(name);
                apState =  (int)method.invoke(wifiManager);
            } catch (Exception e) {
                Log.e("cdd:", "SecurityException", e);
            }
            return apState;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}
