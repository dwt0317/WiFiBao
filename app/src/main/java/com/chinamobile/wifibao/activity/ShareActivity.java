package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.utils.WifiApAdmin;

import java.lang.reflect.Method;

public class ShareActivity extends Activity {
    private Context mContext = null;

    private static final String METHOD_GET_WIFI_AP_STATE = "getWifiApState";
    private static final String METHOD_SET_WIFI_AP_ENABLED = "setWifiApEnabled";
    private static final String METHOD_GET_WIFI_AP_CONFIG = "getWifiApConfiguration";
    private static final String METHOD_IS_WIFI_AP_ENABLED = "isWifiApEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mContext = this;
        //open wifi ap
        Button bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String hotname=((EditText)findViewById(R.id.hotnametext)).getText().toString().trim();
                String password=((EditText)findViewById(R.id.passsettext)).getText().toString().trim();
                if(hotname.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"wifi名称或者密码不能为空！",Toast.LENGTH_LONG).show();
                }
                else if(password.length() < 8 ){
                    Toast.makeText(getApplicationContext(),"密码长度不能小于8！",Toast.LENGTH_LONG).show();
                }
                else {
                    WifiApAdmin wifiAp = new WifiApAdmin(mContext);
                    wifiAp.startWifiAp(hotname, password);
                    //跳转到开启ap成功的页面，下个页面可以关闭ap（sharehot页面有很多测试功能）
                    Intent intent = new Intent(ShareActivity.this, CloseApActivity.class);
                    startActivity(intent);
                }
            }
        });
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

}
