package com.chinamobile.wifibao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.bmob.v3.Bmob;
import android.app.Activity;

//import com.chinamobile.wifibao.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");

        //修改者cdd：只是为了跳转到wifi ap开启分享页面，并不代表要在这里实现跳转，应该在首页点击再跳转
        Intent intent = new Intent(MainActivity.this, Share.class);
        startActivity(intent);
    }

}
