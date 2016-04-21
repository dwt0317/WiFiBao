package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.ConnectedIP;
import com.chinamobile.wifibao.utils.wifiap.WifiApAdmin;
import com.chinamobile.wifibao.utils.traffic.TrafficMonitorService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by cdd on 2016/3/16.
 */
public class CloseApActivity extends Activity {
    private Context mContext = null;
    private boolean stop = false;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        mContext = this;
        setContentView(R.layout.flow_share);

        Toast.makeText(mContext, "热点已开启！", Toast.LENGTH_SHORT).show();
        //流量监测
        final TextView showFlow = (TextView) findViewById(R.id.tv11);
        final Handler flowHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String sR = (String) msg.obj;
                showFlow.setText(sR);
                if (msg.arg1 == 0) {
                    //WifiApAdmin.closeWifiAp(mContext);
                    Toast.makeText(mContext, "超出上限，请关闭热点！", Toast.LENGTH_LONG).show();
                }
            }
        };
        final TrafficMonitorService monitorThread = new TrafficMonitorService();
        monitorThread.setHandler(flowHandle);
        monitorThread.setContext(mContext);
        monitorThread.setMaxShare(getIntent().getDoubleExtra("maxshare", 0.0));
        monitorThread.start();
        //接入监测
        final TextView accessCount = (TextView) findViewById(R.id.tv21);
        final Handler accessHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String count = String.valueOf(msg.arg1);
                accessCount.setText(count);
            }
        };
        final AccessListenerThread listenerThread = new AccessListenerThread();
        listenerThread.setHandler(accessHandle);
        listenerThread.setContext(mContext);
        listenerThread.start();

        //收入监测
        final TextView benefit = (TextView) findViewById(R.id.tv31);
        final Handler benefitHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                double be = Double.parseDouble((String)msg.obj);
                double beOld = Double.parseDouble(benefit.getText().toString());
                benefit.setText(String.valueOf(be+beOld));
            }
        };
//        final BenefitPullThread pullThread = new BenefitPullThread();
//        pullThread.setHandler(benefitHandle);
//        pullThread.setContext(mContext);
//        pullThread.start();
        pullBenefit(benefitHandle);

        //返回HomeActivity
        ImageView home = (ImageView) findViewById(R.id.gohome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CloseApActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        //refresh
        ImageView refresh = (ImageView) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //线程会自动刷新的
                Toast.makeText(mContext, "正在刷新", Toast.LENGTH_SHORT).show();
            }
        });
        //close wifi ap
        Button stopBt = (Button) findViewById(R.id.share_stop);
        stopBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiApAdmin.closeWifiAp(mContext);
                monitorThread.stopService();
                //pullThread.stopThread();
                Toast.makeText(mContext, "宝宝这就去睡觉", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CloseApActivity.this, BalanceShareActivity.class);
                intent.putExtra("flow", showFlow.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.scale_in, R.anim.alpha_out);
                CloseApActivity.this.finish();
            }
        });
    }//end onCreate

    /**
     * 介入监听线程
     */
    private class AccessListenerThread extends Thread {
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
            while (true) {
                if (stop) break;
                Message mess = new Message();
                if (getWiFiApState() != WIFI_AP_STATE_ENABLING && getWiFiApState() != WIFI_AP_STATE_ENABLED) {
                    mess.arg1 = 0;
                    handler.sendMessage(mess);
                    break;
                } else {
                    mess.arg1 = cp.getConnectedIpCount();
                    //Log.i("ip:", String.valueOf(mess.arg1));
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
                apState = (int) method.invoke(wifiManager);
            } catch (Exception e) {
                Log.e("cdd:", "SecurityException", e);
            }
            return apState;
        }
    }//end accessListenerThread

    /**
     * 数据库获得已得收入,实时更新
     */
    private class BenefitPullThread extends Thread {
        private boolean doStop = false;
        private Context context;
        private Handler handler;

        //private static BenefitThread bt;
        //private BenefitThread(){}

        public void setDoStop(boolean doStop) {
            this.doStop = doStop;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHandler(Handler handler) {
            this.handler = handler;
        }

        //关闭线程
        public void stopThread() {
            setDoStop(true);
        }

        @Override
        public void run() {
            while (!doStop) {
//                int benefit = pullBenefit();
//                Message mess = new Message();
//                mess.arg1 = benefit;
//                handler.sendMessage(mess);//异步不可以这样
//                pullBenefit(handle);
                try {
                    Thread.currentThread().sleep(6000);
                } catch (InterruptedException e) {
                    Log.e("cdd:", "Thread ex", e);
                }
            }
        }

    }//end BenefitPullThread

    /**
     * 从本地缓存中读取热点信息
     *
     * @return
     */
    private WiFi getWifiAp() {
        SharedPreferences sp;
        WiFi ap = new WiFi();
        sp = getApplicationContext().getSharedPreferences("WIFIAPIFNO", MODE_PRIVATE);
        ap.setObjectId(sp.getString("objectId", ""));
        ap.setSSID(sp.getString("SSID", ""));
        ap.setPassword(sp.getString("password", ""));
        ap.setUpperLimit(Double.parseDouble(String.valueOf(sp.getFloat("upperLimit", 0))));
        ap.setMaxConnect(sp.getInt("maxConnect", 0));
        ap.setBSSID(sp.getString("BSSID", ""));
        return ap;
    }

    /**
     * 数据库获得已得收入，已使用者为准，不已本地分享为准
     */
    private int pullBenefit(final Handler handler) {
        final String tableName = "UseRecord";
        Bmob.initialize(CloseApActivity.this, "81c22e29e8d2f6204f9d1e58dee89f8c");
        final BmobRealTimeData rtd = new BmobRealTimeData();
        final String objId = getWifiAp().getObjectId();

        rtd.start(CloseApActivity.this, new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject arg0) {
                if (BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))) {
                    JSONObject data = arg0.optJSONObject("data");
                    try {
                        String flowUsed = data.getString("flowUsed");
                        Log.i("flowUsed:", flowUsed);
                        Message mess = new Message();
                        mess.obj = flowUsed;
                        handler.sendMessage(mess);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onConnectCompleted() {
                if (rtd.isConnected()) {
                    // 监听表更新
                    rtd.subTableUpdate(tableName);
                }
            }
        });

        return 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //应当在这里结束所有监测线程
        stop = true;//接入监测
        //TrafficMonitorService.getInstance().setStop(true);//流量监测
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
