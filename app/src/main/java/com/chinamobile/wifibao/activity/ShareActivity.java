package com.chinamobile.wifibao.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.ContentAdapter;
import com.chinamobile.wifibao.utils.DatabaseUtil;
import com.chinamobile.wifibao.utils.WiFiApGradeUtil;
import com.chinamobile.wifibao.utils.wifiap.WifiApAdmin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by cdd on 2016/3/16.
 * modified by cdd on 2016/4/25
 * 缓存的wifi热点信息可以用于，后面条件查询数据库；目前缓存热点开启时间，在上传热点分享信息可以用到。
 */

public class ShareActivity extends Activity {
    private Context mContext = null;
    private static final String METHOD_GET_WIFI_AP_STATE = "getWifiApState";
    private static final String METHOD_IS_WIFI_AP_ENABLED = "isWifiApEnabled";
    private WiFi ap;
    private User user;


    // 顶部菜单2个Linearlayout
    private LinearLayout ll_data;
    private LinearLayout ll_wifi;


    // 顶部菜单2个ImageView


    // 顶部菜单2个菜单标题
    private TextView tv_data;
    private TextView tv_wifi;


    // 中间内容区域
    private ViewPager viewPager;

    // ViewPager适配器ContentAdapter
    private ContentAdapter adapter;

    private List<View> views;
    private View page_01;
    private View page_02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_share_main);

        // 初始化控件
        initView();
        // 初始化顶部按钮事件
        initEvent();

        mContext = this;

        //用户
        user = getUser();
        //如果热点已经打开，跳转下个页面
        isWiFiEnabled();

        final Handler openHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == 1) {
                    open();
                }else {
                    Toast.makeText(mContext, "糟糕，网络不好哦...", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //点击打开热点

        Button shareButton = (Button)page_01.findViewById(R.id.share_submit);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOrDo(openHandle);
            }
        });


        //返回HomeActivity
        ImageView home = (ImageView) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareActivity.this, Home2Activity.class);
                startActivity(intent);
            }
        });

    }




    //功能实现
    /**
     * 非法输入
     */
    private void checkOrDo(Handler handler) {
        String name = ((EditText) page_01.findViewById(R.id.apnametext)).getText().toString().trim();
        String password = ((EditText) page_01.findViewById(R.id.passwordtext)).getText().toString().trim();
        String share = ((EditText) page_01.findViewById(R.id.maxsharetext)).getText().toString().trim();
        String access = ((EditText) page_01.findViewById(R.id.maxaccesstext)).getText().toString().trim();
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(mContext, "wifi名称或者密码不能为空！", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            Toast.makeText(ShareActivity.this, "密码长度不能小于8！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ShareActivity.this, "数据同步中...", Toast.LENGTH_SHORT).show();
            //上传数据
            ap = new WiFi();
            ap.setSSID(name);
            ap.setPassword(password);
            ap.setUpperLimit(Double.parseDouble(share));//没有判断非法输入，但在xml中做了输入限制
            ap.setMaxConnect(Integer.parseInt(access));
            ap.setScore(WiFiApGradeUtil.getGrade(ap));
            ap.setBSSID(getLocalMacAddress());
            ap.setState(true);
            ap.setUser(user);
            //上传热点信息，成功则打开热点
            DatabaseUtil.getInstance().writeApToDatabase(mContext, handler, ap);
        }
    }
    /**
     * 打开热点，并跳转页面
     */
    private void open(){
        WifiApAdmin wifiAp = new WifiApAdmin(mContext);
        wifiAp.startWifiAp(ap.getSSID(), ap.getPassword());
        Toast.makeText(mContext, "努力开启中...", Toast.LENGTH_SHORT).show();
        //热点信息已经上传成功，在本地保存ap信息
        writeInCache(ap);
        //跳转并销毁页面
        Intent intent = new Intent(ShareActivity.this, CloseApActivity.class);
        intent.putExtra("maxshare",ap.getUpperLimit());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.scale_in, R.anim.alpha_out);
        ShareActivity.this.finish();
    }

    /***
     * 热点是否已经开启
     */
    private void isWiFiEnabled() {
        //wifi ap is open, go to next Activity.
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        try {
            String name = METHOD_IS_WIFI_AP_ENABLED;
            Method method = WifiManager.class.getMethod(name);
            boolean result = (boolean) method.invoke(wifiManager);
            if (result) {
                Intent intent = new Intent(ShareActivity.this, CloseApActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                ShareActivity.this.finish();
            }
        } catch (Exception e) {
            Log.e("cdd:", "SecurityException", e);
        }
    }

    /***
     * 获取MAC地址
     * @return
     */
    private String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 当前登录用户
     * @return
     */
    private User getUser() {
        //User u=null;
        //获取当前登录用户,mContext似乎应该换成getApplicationContext,登陆时也应该修改成为之
        User u = BmobUser.getCurrentUser(mContext, User.class);
        return u;
    }

    /***
     * 保存wifiap信息
     */
    void writeInCache(WiFi ap){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("WIFIAPIFNO", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("objectId",ap.getObjectId());

        editor.putString("SSID", ap.getBSSID());
        editor.putString("password", ap.getPassword());
        editor.putFloat("upperLimit", Float.parseFloat(ap.getUpperLimit().toString()));
        editor.putInt("maxConnect", ap.getMaxConnect());
        editor.putString("BSSID", ap.getBSSID());
        editor.putBoolean("state", ap.getState());
        //记录开始时间
        editor.putLong("startTime",(new Date()).getTime());
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //初始化
    private void initEvent() {
        // 设置按钮监听
        ll_data.setOnClickListener(new MyListener());
        ll_wifi.setOnClickListener(new MyListener());


        //设置ViewPager滑动监听
        viewPager.addOnPageChangeListener(new MyPageListener());
    }

    private void initView() {

        // 顶部菜单2个Linearlayout
        this.ll_data = (LinearLayout) findViewById(R.id.ll_data);
        this.ll_wifi = (LinearLayout) findViewById(R.id.ll_wifi);


        // 顶部菜单2个ImageView


        // 顶部菜单2个菜单标题
        this.tv_data = (TextView) findViewById(R.id.set_data);
        this.tv_wifi = (TextView) findViewById(R.id.set_wifi);


        // 中间内容区域ViewPager
        this.viewPager = (ViewPager) findViewById(R.id.set_content);

        // 适配器
        //View page_01 = View.inflate(ShareSetActivity.this, R.layout.set_share_data, null);
        //View page_02 = View.inflate(ShareSetActivity.this, R.layout.set_share_wifi, null);


        views = new ArrayList<View>();
        LayoutInflater myInflater = getLayoutInflater();
        page_01 = myInflater.inflate(R.layout.set_share_data, null);
        page_02 = myInflater.inflate(R.layout.set_share_wifi, null);
        views.add(page_01);
        views.add(page_02);


        this.adapter = new ContentAdapter(views);
        viewPager.setAdapter(adapter);

    }



    private void restartTop() {
        // ImageView置为灰色

        //iv_home.setImageResource(R.drawable.tab_weixin_normal);
        //iv_address.setImageResource(R.drawable.tab_address_normal);
        //iv_friend.setImageResource(R.drawable.tab_find_frd_normal);
        //iv_setting.setImageResource(R.drawable.tab_settings_normal);

        // TextView置为白色
        /**
         tv_data.setTextColor(0xff1B940A);
         tv_wifi.setTextColor(0xff1B940A);
         **/
        tv_data.setBackground(getResources().getDrawable(R.drawable.textview_border));
        tv_wifi.setBackground(getResources().getDrawable(R.drawable.textview_border));
        ;
    }


    class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 在每次点击后将所有的顶部按钮(ImageView,TextView)颜色改为灰色，然后根据点击着色
            restartTop();
            // TetxView置为绿色，页面随之跳转
            switch (v.getId()) {
                case R.id.ll_data:
                    //tv_data.setTextColor(0xff1B940A);
                    //tv_data.setBackgroundColor(Color.WHITE);
                    tv_data.setBackground(getResources().getDrawable(R.drawable.textview_border_focused));
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.ll_wifi:
                    //tv_wifi.setTextColor(0xff1B940A);
                    //tv_wifi.setBackgroundColor(Color.WHITE);
                    tv_wifi.setBackground(getResources().getDrawable(R.drawable.textview_border_focused));
                    viewPager.setCurrentItem(1);
                    break;
                default:
                    break;
            }

        }
    }
    class MyPageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int arg0) {
            restartTop();
            //当前view被选择的时候,改变底部菜单图片，文字颜色
            switch (arg0) {
                case 0:
                    //iv_home.setImageResource(R.drawable.tab_weixin_pressed);
                    //tv_data.setTextColor(0xff1B940A);
                    //tv_data.setBackgroundColor(Color.WHITE);
                    tv_data.setBackground(getResources().getDrawable(R.drawable.textview_border_focused,null));

                    break;
                case 1:
                    //iv_address.setImageResource(R.drawable.tab_address_pressed);
                    //tv_wifi.setTextColor(0xff1B940A);
                    tv_wifi.setBackground(getResources().getDrawable(R.drawable.textview_border_focused,null));

                    break;

                default:
                    break;
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
