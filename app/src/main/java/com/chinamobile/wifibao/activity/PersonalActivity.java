package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.LogInListener;

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

//        BmobUser.loginByAccount(mContext, "cdd", "123", new LogInListener<User>() {
//
//            @Override
//            public void done(User user, BmobException e) {
//                if (user != null) {
//                    Log.i("fdf:", user.getObjectId());
//                    showUserInfo();
//                } else {
//                    Log.i("fdf:", "err");
//                }
//            }
//        });
        ImageView home = (ImageView)findViewById(R.id.gotohome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, Home2Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        LinearLayout modify = (LinearLayout)findViewById(R.id.modifypass);
        modify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PersonalActivity.this,PasswordModifyActivity.class);
                startActivity(intent);
            }
        });

        showUserInfo();

    }

    private void showUserInfo() {
        BmobUser u =  BmobUser.getCurrentUser(mContext);
        if(u == null){
            Toast.makeText(PersonalActivity.this, "未登录！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(PersonalActivity.this, Home2Activity.class);
            startActivity(intent);
            return;
        }
        //String str=u.getUsername();
        String obectid=u.getObjectId();
        //String username=u.getUsername();
        //String phonenumber=u.getMobilePhoneNumber();
        //Log.i("success", str);


        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(this, obectid, new GetListener<User>() {

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
                //Toast.makeText(mContext, objectid, Toast.LENGTH_SHORT).show();
                ((TextView)findViewById(R.id.getflowused)).setText(getflowused+"MB");
                ((TextView)findViewById(R.id.username)).setText(username);
                ((TextView)findViewById(R.id.phonenumber)).setText(phonenumber);
                ((TextView)findViewById(R.id.location)).setText(location);
                ((TextView)findViewById(R.id.balance)).setText(balance+"流量币");
                ((TextView)findViewById(R.id.getflowshared)).setText(getflowshared+"MB");
            }

            @Override
            public void onFailure(int code, String arg0) {
                Log.i("fail", arg0);
            }

        });
    }

}
