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
import cn.bmob.v3.listener.GetListener;

/**
 * 个人信息页面
 */
public class PersonalActivity extends Activity {
    private Context mContext = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);
        mContext = this;

        ImageView home = (ImageView)findViewById(R.id.gotohome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, HomeActivity.class);
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

    /**
     * 显示用户信息
     */
    private void showUserInfo() {
        BmobUser u =  BmobUser.getCurrentUser(mContext);
        if(u == null){
            Toast.makeText(PersonalActivity.this, "未登录！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(PersonalActivity.this, HomeActivity.class);
            startActivity(intent);
            return;
        }

        String obectid=u.getObjectId();
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
                ((TextView)findViewById(R.id.getflowused)).setText(getflowused+" MB");
                ((TextView)findViewById(R.id.username)).setText(username);
                ((TextView)findViewById(R.id.phonenumber)).setText(phonenumber);
                ((TextView)findViewById(R.id.location)).setText(location);
                ((TextView)findViewById(R.id.balance)).setText(balance+" 流量币");
                ((TextView)findViewById(R.id.getflowshared)).setText(getflowshared+" MB");
            }
            @Override
            public void onFailure(int code, String arg0) {
                Log.i("fail", arg0);
            }
        });
    }
}
