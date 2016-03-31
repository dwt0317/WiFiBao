package com.chinamobile.wifibao.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
        //open wifi ap
        Button bt = (Button)findViewById(R.id.share_submit);
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name=((EditText)findViewById(R.id.apnametext)).getText().toString().trim();
                String password=((EditText)findViewById(R.id.passwordtext)).getText().toString().trim();
                String share = ((EditText) findViewById(R.id.maxsharetext)).getText().toString().trim();
                String access = ((EditText) findViewById(R.id.maxaccesstext)).getText().toString().trim();
                if(name.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"wifi名称或者密码不能为空！",Toast.LENGTH_SHORT).show();
                }
                else if(password.length() < 8 ){
                    Toast.makeText(getApplicationContext(),"密码长度不能小于8！",Toast.LENGTH_SHORT).show();
                }
                else {
                    WifiApAdmin wifiAp = new WifiApAdmin(mContext);
                    wifiAp.startWifiAp(name, password);
                    Toast.makeText(mContext, "宝宝努力开启中...", Toast.LENGTH_LONG).show();
                    WiFi ap = new WiFi();
                    ap.setSSID(name);
                    ap.setPassword(password);
                    ap.setUpperLimit(Double.parseDouble(share));//没有判断非法输入
                    ap.setMaxConnect(Integer.parseInt(access));
                    ap.setBSSID(getLocalMacAddress());
                    //DatabaseUtil.writeApToDatabase(mContext, ap);

                    Intent intent = new Intent(ShareActivity.this, CloseApActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.scale_in, R.anim.alpha_out);
                }
            }
        });
        isWiFiEnabled();
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
                startActivity(intent);
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
}
