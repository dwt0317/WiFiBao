package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.chinamobile.wifibao.utils.DatabaseUtil;
import com.chinamobile.wifibao.utils.wifiap.WifiApAdmin;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * 正在分享热点页面
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
        final String apId = getWifiAp().getObjectId();
        final TextView showFlow = (TextView) findViewById(R.id.tv11);
        final Handler flowHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                double be = Double.parseDouble((String)msg.obj);
                double beOld = Double.parseDouble(showFlow.getText().toString());
                DecimalFormat df  = new DecimalFormat("######0.00");
                showFlow.setText(df.format(be + beOld));
            }
        };
        pullFlowUsed(flowHandle,apId);
        //接入监测
        final TextView accessCount = (TextView) findViewById(R.id.tv21);
        final Handler accessHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int be = Integer.parseInt((String) msg.obj);
                accessCount.setText(String.valueOf(be));
            }
        };

        pullCurConnect(accessHandle,apId);

        //收入监测,获取收益
        final TextView benefit = (TextView) findViewById(R.id.tv31);
        final Handler benefitHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                double be = Double.parseDouble((String)msg.obj);
                double beOld = Double.parseDouble(benefit.getText().toString());
                DecimalFormat df  = new DecimalFormat("######0.00");
                benefit.setText(df.format(be+beOld));
            }
        };

        pullBenefit(benefitHandle,apId);

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
//                monitorThread.stopService();
                DatabaseUtil.getInstance().deletefromConnetionPool(mContext);
                Toast.makeText(mContext, "宝宝这就去睡觉", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CloseApActivity.this, BalanceShareActivity.class);
                intent.putExtra("flow", showFlow.getText().toString());
                intent.putExtra("bene", benefit.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.scale_in, R.anim.alpha_out);
                CloseApActivity.this.finish();
            }
        });
    }//end onCreate


    /**
     * 从本地缓存中读取热点信息
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
     * 获取热点当前接入人数
     */
    private int pullCurConnect(final Handler handler,final String apId) {
        final String tableName = "ConnectionPool";
        final BmobRealTimeData rtd = new BmobRealTimeData();

        rtd.start(CloseApActivity.this, new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject arg0) {
                if (BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))) {
                    JSONObject data = arg0.optJSONObject("data");
                    try {
                        String curConnect = data.getString("curConnect");
                        String id = data.getJSONObject("WiFi").getString("objectId");
                        if (apId.equalsIgnoreCase(id)) {
                            Log.i("bomb:", curConnect);
                            Message mess = new Message();
                            mess.obj = curConnect;
                            handler.sendMessage(mess);
                        }

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

    /**
     * 获取当前已分享的流量
     */
    private int pullFlowUsed(final Handler handler,final String apId) {
        final String tableName = "ConnectionPool";
        final BmobRealTimeData rtd = new BmobRealTimeData();
        rtd.start(CloseApActivity.this, new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject arg0) {
                if (BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))) {
                    JSONObject data = arg0.optJSONObject("data");
                    try {
                        String flow = data.getString("flowUsed");
                        String id = data.getJSONObject("WiFi").getString("objectId");
                        if (apId.equalsIgnoreCase(id)) {
                            Log.i("bomb:", flow);
                            Message mess = new Message();
                            mess.obj = flow;
                            handler.sendMessage(mess);
                        }
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


    /**
     * 获取当前收益
     */
    private int pullBenefit(final Handler handler,final String apId) {
        final String tableName = "ConnectionPool";
//        Bmob.initialize(CloseApActivity.this, "81c22e29e8d2f6204f9d1e58dee89f8c");
        final BmobRealTimeData rtd = new BmobRealTimeData();

        rtd.start(CloseApActivity.this, new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject arg0) {
                if (BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))) {
                    JSONObject data = arg0.optJSONObject("data");
                    try {
                        String flow = data.getString("cost");
                        String id = data.getJSONObject("WiFi").getString("objectId");
                        if(apId.equalsIgnoreCase(id)){
                            Log.i("cost:", flow);
                            Message mess = new Message();
                            mess.obj = flow;
                            handler.sendMessage(mess);
                        }

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
//            moveTaskToBack(true);
//            return true;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
