package com.chinamobile.wifibao.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.DatabaseUtil;
import com.chinamobile.wifibao.utils.WifiApAdmin;

import java.lang.reflect.Method;

public class ShareActivity extends Activity {
    private Context mContext = null;
    private static final String METHOD_GET_WIFI_AP_STATE = "getWifiApState";
    private static final String METHOD_IS_WIFI_AP_ENABLED = "isWifiApEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_share);
        mContext = this;

        //如果热点已经打开，跳转下个页面
        isWiFiEnabled();
        //点击打开热点
        Button shareButton = (Button)findViewById(R.id.share_submit);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText) findViewById(R.id.apnametext)).getText().toString().trim();
                String password = ((EditText) findViewById(R.id.passwordtext)).getText().toString().trim();
                String share = ((EditText) findViewById(R.id.maxsharetext)).getText().toString().trim();
                String access = ((EditText) findViewById(R.id.maxaccesstext)).getText().toString().trim();
                if (name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(mContext, "wifi名称或者密码不能为空！", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(ShareActivity.this, "密码长度不能小于8！", Toast.LENGTH_SHORT).show();
                } else {
                    //上传数据
                    WiFi ap = new WiFi();
                    ap.setSSID(name);
                    ap.setPassword(password);
                    ap.setUpperLimit(Double.parseDouble(share));//没有判断非法输入，但在xml中做了输入限制
                    ap.setMaxConnect(Integer.parseInt(access));
                    ap.setBSSID(getLocalMacAddress());
                    boolean success = DatabaseUtil.getInstance().writeApToDatabase(mContext, ap);
                    if(success){
                        WifiApAdmin wifiAp = new WifiApAdmin(mContext);
                        wifiAp.startWifiAp(name, password);
                        Toast.makeText(mContext, "宝宝努力开启中...", Toast.LENGTH_LONG).show();
                        //保存ap信息
                        writeInCache(ap);
                        //跳转并销毁页面
                        Intent intent = new Intent(ShareActivity.this, CloseApActivity.class);
                        intent.putExtra("maxshare",Double.parseDouble(share));
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        overridePendingTransition(R.anim.scale_in, R.anim.alpha_out);
                        ShareActivity.this.finish();
                    }else{
                        Toast.makeText(mContext, "糟糕，网络不好哦...", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    private void isWiFiEnabled() {
        //wifi ap is open, go to next Activity.
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        try {
            String name = METHOD_IS_WIFI_AP_ENABLED;
            Method method = WifiManager.class.getMethod(name);
            boolean result = (boolean) method.invoke(wifiManager);
            if (result) {
                Intent intent = new Intent(ShareActivity.this, CloseApActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                ShareActivity.this.finish();
            }
        } catch (Exception e) {
            Log.e("cdd:", "SecurityException", e);
        }
    }

    /***
     * 获取MAC地址
     * @return
     */
    private String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /***
     * 保存wifiap信息
     */
    void writeInCache(WiFi ap){
        SharedPreferences sp = mContext.getSharedPreferences("WIFIAPIFNO", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("SSID",ap.getBSSID());
        editor.putString("password", ap.getPassword());
        editor.putFloat("upperLimit", Float.parseFloat(ap.getUpperLimit().toString()));
        editor.putInt("maxConnect", ap.getMaxConnect());
        editor.putString("BSSID", ap.getBSSID());
        //记录开始时间
        //
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
