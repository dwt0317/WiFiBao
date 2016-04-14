package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.ShareRecord;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.DatabaseUtil;

import cn.bmob.v3.BmobUser;

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

        tv.setText(intent.getStringExtra("flow"));

        //返回HomeActivity
        home = (ImageView) findViewById(R.id.imageView8);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BalanceShareActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
        shareRecord = getShareRecord();
        //还没有获取用户信息
        sycData(mContext,shareRecord);
    }

    private User getUser() {
        User u=null;
        //获取当前登录用户,mContext似乎应该换成getApplicationContext,登陆时也应该修改成为之
        u = BmobUser.getCurrentUser(mContext, User.class);
        return u;
    }

    private ShareRecord getShareRecord() {
        ShareRecord sr=null;
        sr.setUser(getUser());
        return sr;
    }
    private WiFi getWiFi(){
        WiFi wf=null;
        sp = this.getSharedPreferences("passwordFile", mContext.MODE_PRIVATE);

        return wf;
    }

    private boolean sycData(Context context, ShareRecord shareRecord){
        boolean success= false;
        DatabaseUtil du = DatabaseUtil.getInstance();
        success = du.writeShareToDatabase(context, shareRecord);

        return success;
    }

}
