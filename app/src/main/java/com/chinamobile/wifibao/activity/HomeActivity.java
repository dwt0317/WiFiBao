package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import java.util.HashMap;
import java.util.ArrayList;
import com.chinamobile.wifibao.R;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/3/29.
 */
public class HomeActivity extends Activity{

    private ImageView portrait;
    private ImageView portraitNotLogin;
    private TextView username;
    private LinearLayout usernameLayout;
    private LinearLayout logoutLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Bmob.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");
        BmobSMS.initialize(this, "81c22e29e8d2f6204f9d1e58dee89f8c");

        //gridview填充数据
        int[] icon = {R.drawable.home_useflow,R.drawable.home_shareflow,R.drawable.home_userinfo,R.drawable.home_wallet,
                R.drawable.home_viewused,R.drawable.home_viewshared,R.drawable.home_recharge,R.drawable.home_bill};
        String[] iconName = {"使用流量" ,"分享流量","个人信息","我的钱包","查看使用","查看分享","充值","我的账单"};

        GridView gridview = (GridView) findViewById(R.id.gridview1);
        ArrayList<HashMap<String, Object>> ImageItem = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < 8; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Image", icon[i]);
            map.put("ImageText", iconName[i]);
            ImageItem.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(this,ImageItem,R.layout.homegrid_item,new String[]{"Image", "ImageText"},
                new int[]{R.id.ItemImage, R.id.ItemText});
        gridview.setAdapter(saImageItems);
        //设置跳转
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, WifiListActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, ShareActivity.class);
                    goToActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, PersonalActivity.class);
                    goToActivity(intent);
                }
                else if (position == 4) {
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, UseRecordActivity.class);
                    goToActivity(intent);
                }
            }
        });

        createSettingPanel();

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

    private void createSettingPanel(){
        //用户设置页面
        final View setting_content= this.getLayoutInflater().inflate(R.layout.haslogged, null);
        final PopupWindow popup = new PopupWindow(setting_content,900,2560);
        popup.setFocusable(true);   //设置可以获取焦点
        popup.setBackgroundDrawable(new BitmapDrawable()); //防止弹出菜单获取焦点之后，点击activity的其他组件没有响应

        final ImageView  image_set= (ImageView) findViewById(R.id.setting);
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
        if(bmobUser == null){
            usernameLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            portrait.setVisibility(View.VISIBLE);
            portraitNotLogin.setVisibility(View.GONE);
            logoutLayout.setVisibility(View.VISIBLE);
            username.setText(bmobUser.getUsername());
            usernameLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, PersonalActivity.class);
                    startActivity(intent);
                }
            });
            logoutLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    BmobUser.logOut(HomeActivity.this);
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    private void goToActivity(Intent destIntent){
        BmobUser bmobUser = BmobUser.getCurrentUser(this);
        if(bmobUser == null){
            Intent intent = new Intent();
            intent.setClass(this,LoginActivity.class);
            this.startActivity(intent);
        }else{
            this.startActivity(destIntent);
        }
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
