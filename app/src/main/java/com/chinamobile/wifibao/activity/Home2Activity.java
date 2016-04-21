package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.chinamobile.wifibao.R;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class Home2Activity extends Activity {
    private ImageView portrait;
    private ImageView portraitNotLogin;
    private TextView username;
    private LinearLayout usernameLayout;
    private LinearLayout logoutLayout;

    private LinearLayout use;
    private LinearLayout share;
    private LinearLayout userinfo;
    private LinearLayout hall;
    private LinearLayout mywallet;
    private LinearLayout usehistory;
    private LinearLayout sharehistory;
    private LinearLayout manual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home2);
        setViewComponent();
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    private void setViewComponent(){
        Bmob.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");
        BmobSMS.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");

        createSettingPanel();

        use = (LinearLayout)findViewById(R.id.layout1);
        share = (LinearLayout)findViewById(R.id.layout2);
        userinfo = (LinearLayout)findViewById(R.id.layout3);
        hall = (LinearLayout)findViewById(R.id.layout4);
        mywallet = (LinearLayout)findViewById(R.id.layout5);
        usehistory = (LinearLayout)findViewById(R.id.layout6);
        sharehistory = (LinearLayout)findViewById(R.id.layout7);
        manual = (LinearLayout)findViewById(R.id.layout8);

        setListener(use, WifiListActivity.class);
        setListener(share,ShareActivity.class);
        setListener(userinfo,PersonalActivity.class);
        setListener(mywallet,MywalletActivity.class);
        setListener(hall,MobileServiceActivity.class);
        setListener(usehistory,UseRecordActivity.class);

     //   setListener(hall,MobileServiceActivity.class);
    }

   private void setListener(LinearLayout layout,final Class page ){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Home2Activity.this, page);
                startActivity(intent);
            }
        });
    }

    private void createSettingPanel() {
        //侧边栏
        final View setting_content = this.getLayoutInflater().inflate(R.layout.haslogged, null);
        final PopupWindow popup = new PopupWindow(setting_content, 900, 2560);
        popup.setFocusable(true);   //设置可以获取焦点
        popup.setBackgroundDrawable(new BitmapDrawable()); //防止弹出菜单获取焦点之后，点击activity的其他组件没有响应

        final ImageView image_set = (ImageView) findViewById(R.id.setting);
        image_set.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popup.setAnimationStyle(R.style.PopupAnimation);
                popup.showAtLocation(image_set, Gravity.NO_GRAVITY, 0, 0);
            }
        });

        portrait = (ImageView) setting_content.findViewById(R.id.portrait);
        portraitNotLogin = (ImageView) setting_content.findViewById(R.id.portraitNotLogin);
        username = (TextView) setting_content.findViewById(R.id.username);
        usernameLayout = (LinearLayout) setting_content.findViewById(R.id.usernameLayout);
        logoutLayout = (LinearLayout) setting_content.findViewById(R.id.logoutLayout);

        BmobUser bmobUser = BmobUser.getCurrentUser(this);
        if (bmobUser == null) {
            usernameLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(Home2Activity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            portrait.setVisibility(View.VISIBLE);
            portraitNotLogin.setVisibility(View.GONE);
            logoutLayout.setVisibility(View.VISIBLE);
            username.setText(bmobUser.getUsername());
            usernameLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(Home2Activity.this, PersonalActivity.class);
                    startActivity(intent);
                }
            });
            logoutLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    BmobUser.logOut(Home2Activity.this);
                    Intent intent = new Intent();
                    intent.setClass(Home2Activity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

}