package com.chinamobile.wifibao.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.UseManager;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;

public class TestActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test2);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        Bmob.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");
//        writeToDatabase();
        Toast.makeText(TestActivity2.this,"1111111",Toast.LENGTH_LONG);
        UseManager uh = UseManager.getInstance(this);
        uh.testConnect();
    }


    public  void writeToDatabase(){
        WiFi wifi = new WiFi();
        wifi.setSSID("12312");
        User user = new User();
        user.setBalance(10.0);
        user.setUsername("1111111");
        user.setPassword("12312");
        BmobGeoPoint loc = new BmobGeoPoint(10.0,20.0);
        user.setLocation(loc);
        user.setMobilePhoneNumber("15221111111");
        user.setPortrait("/ddd");
        user.setRemainedFlow(12.0);
        user.setFlowShared(12.0);
        user.setFlowUsed(12.0);
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
//                toast("添加数据成功，返回objectId为：" + ", 数据在服务端的创建时间为：");
                System.out.println("sfsdfsdfsd!!!!!");
                Log.i("DB", "first activity--------onStart()");
            }

            @Override
            public void onFailure(int code, String arg0) {
                System.out.println("shibai!!!!!");
                Log.e("DB", "shibai");

                // 添加失败
            }
        });
    }

}
