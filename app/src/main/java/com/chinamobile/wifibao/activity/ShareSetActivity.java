package com.chinamobile.wifibao.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.utils.ContentAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShareSetActivity extends FragmentActivity {


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

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.set_share_main);

         // 初始化控件
         initView();
         // 初始化顶部按钮事件
         initEvent();

     }

     private void initEvent() {
         // 设置按钮监听
         ll_data.setOnClickListener(new MyListener());
         ll_wifi.setOnClickListener(new MyListener());


         //设置ViewPager滑动监听
         viewPager.setOnPageChangeListener(new MyPageListener());
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
         View page_01 = View.inflate(ShareSetActivity.this, R.layout.set_share_data, null);
         View page_02 = View.inflate(ShareSetActivity.this, R.layout.set_share_wifi, null);


         views = new ArrayList<View>();
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
         tv_data.setTextColor(0xffffffff);
         tv_wifi.setTextColor(0xffffffff);
;
     }


    class MyListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // 在每次点击后将所有的顶部按钮(ImageView,TextView)颜色改为灰色，然后根据点击着色
            restartTop();
            // ImageView和TetxView置为绿色，页面随之跳转
            switch (v.getId()) {
                case R.id.ll_data:
                    tv_data.setTextColor(0xff1B940A);
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.ll_wifi:
                    tv_wifi.setTextColor(0xff1B940A);
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
                    tv_data.setTextColor(0xff1B940A);
                    break;
                case 1:
                    //iv_address.setImageResource(R.drawable.tab_address_pressed);
                    tv_wifi.setTextColor(0xff1B940A);
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
