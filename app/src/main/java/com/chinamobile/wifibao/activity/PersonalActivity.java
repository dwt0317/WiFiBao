package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by zb on 2016/4/7.
 */
public class PersonalActivity extends Activity {
    private Context mContext = null;

    protected void onCreate(Bundle savedInstanceState) {

        Bmob.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);
        mContext = this;


        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(this, "2im5888A", new GetListener<User>() {

            @Override
            public void onSuccess(User object) {
                //获得playerName的信息
                String username=String.valueOf(object.getUsername());
                String phonenumber=String.valueOf(object.getMobilePhoneNumber());
                String location=String.valueOf(object.getLocation());
                String balance=String.valueOf(object.getBalance());
                String getflowshared=String.valueOf(object.getFlowShared());
                String getflowused=String.valueOf(object.getFlowUsed());

                //Log.i("success", str);
                //获得数据的objectId信息
                String objectid=String.valueOf(object.getObjectId());
                Toast.makeText(mContext,objectid,Toast.LENGTH_SHORT).show();
                ((TextView)findViewById(R.id.getflowused)).setText(getflowused+"MB");
                ((TextView)findViewById(R.id.username)).setText(username);
                ((TextView)findViewById(R.id.phonenumber)).setText(phonenumber);
                ((TextView)findViewById(R.id.location)).setText(location);
                ((TextView)findViewById(R.id.balance)).setText(balance+"流量币");
                ((TextView)findViewById(R.id.getflowshared)).setText(getflowshared+"MB");
            }

            @Override
            public void onFailure(int code, String arg0) {
                Log.i("fail",arg0);
            }

        });
    }

}
