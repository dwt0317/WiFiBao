package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.ShareRecord;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.DatabaseUtil;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by cdd on 2016/3/24.
 */
public class BalanceShareActivity extends Activity {
    private ImageView home = null;
    private Context mContext;

    private ShareRecord shareRecord;
    SharedPreferences sp;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        mContext = this;
        setContentView(R.layout.balance_share);
        //显示已分享流量
        Intent intent = getIntent();
        TextView tv = (TextView)findViewById(R.id.textview51);

        tv.setText(intent.getStringExtra("flow"));//这里带有单位MB

        //返回HomeActivity
        home = (ImageView) findViewById(R.id.imageView8);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BalanceShareActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
        shareRecord = getShareRecord(Double.parseDouble("20"));
        //还没有获取热点信息
        sycData(mContext,shareRecord);
    }

    private User getUser() {
        //User u=null;
        //获取当前登录用户,mContext似乎应该换成getApplicationContext,登陆时也应该修改成为之
        User u = BmobUser.getCurrentUser(mContext, User.class);
        return u;
    }

    private ShareRecord getShareRecord(Double flow) {
        ShareRecord sr=new ShareRecord();
        sr.setWiFi(getWifiAp());
        sr.setIncome(0.0);
        sr.setStartTime(new BmobDate(new Date()));
        sr.setEndTime(new BmobDate(new Date()));
        sr.setFlowShared(flow);

        sr.setUser(getUser());
        return sr;
    }

    /**
     * 本地获取数据
     * @return
     */
    private WiFi getWifiAp(){
        WiFi ap= new WiFi();
        sp = getApplicationContext().getSharedPreferences("WIFIAPIFNO", MODE_PRIVATE);
        ap.setObjectId(sp.getString("objectId",""));
        ap.setSSID(sp.getString("SSID",""));
        ap.setPassword(sp.getString("password", ""));
        ap.setUpperLimit(Double.parseDouble(String.valueOf(sp.getFloat("upperLimit", 0))));
        ap.setMaxConnect(sp.getInt("maxConnect", 0));
        ap.setBSSID(sp.getString("BSSID", ""));
        Log.i("BSSID",sp.getString("BSSID", ""));
        return ap;
    }

    private boolean sycData(Context context, ShareRecord shareRecord){
        boolean success= false;
        if(shareRecord == null)
            return success;

        DatabaseUtil du = DatabaseUtil.getInstance();
        success = du.writeShareToDatabase(context, shareRecord);

        return success;
    }

}
