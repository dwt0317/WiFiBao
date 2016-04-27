package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.utils.cycleImage.ADInfo;
import com.chinamobile.wifibao.utils.cycleImage.ImageCycleView;

import java.util.ArrayList;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

import com.chinamobile.wifibao.utils.cycleImage.ImageCycleView.ImageCycleViewListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class Home2Activity extends Activity {
    private ImageView portrait;
    private ImageView portraitNotLogin;
    private TextView username;
    private LinearLayout usernameLayout;
    private LinearLayout logoutLayout;
    private LinearLayout aboutusLayout;
    private LinearLayout mywalletLayout;

    private LinearLayout use;
    private LinearLayout share;
    private LinearLayout userinfo;
    private LinearLayout hall;
    private LinearLayout mywallet;
    private LinearLayout usehistory;
    private LinearLayout sharehistory;
    private LinearLayout manual;

    private ImageCycleView mAdView;
    private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
    private String[] imageUrls = {
            "drawable://" + R.drawable.home_ad_0,
            "drawable://" + R.drawable.home_ad_1,
            "drawable://" + R.drawable.home_ad_2,
            "drawable://" + R.drawable.home_ad_3

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home2);
        setViewComponent();
        initImageLoader();
        initCycleImage();
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
        setLoginListener(share, ShareActivity.class);
        setLoginListener(userinfo, PersonalActivity.class);
        setLoginListener(mywallet, MywalletActivity.class);
        setLoginListener(hall, MobileServiceActivity.class);
        setLoginListener(usehistory,UseRecordActivity.class);
        setLoginListener(sharehistory,ShareRecordActivity.class);
        setLoginListener(manual,ManualActivity.class);

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

    private void setLoginListener(LinearLayout layout,final Class page){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Home2Activity.this, page);
                goToActivity(intent);
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
        aboutusLayout=(LinearLayout) setting_content.findViewById(R.id.aboutusLayout);
        mywalletLayout=(LinearLayout) setting_content.findViewById(R.id.mywalletLayout);

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
                    if (BmobUser.getCurrentUser(Home2Activity.this)!=null){
                        BmobUser.logOut(Home2Activity.this);
                        Intent intent = new Intent();
                        intent.setClass(Home2Activity.this, Home2Activity.class);
                        startActivity(intent);
                    }
                }
            });
            mywalletLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(Home2Activity.this, MywalletActivity.class);
                    goToActivity(intent);
                }
            });
            aboutusLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(Home2Activity.this, ManualActivity.class);
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

    /**
     * ImageCycleListener
     */
    private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {
        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {
            Toast.makeText(Home2Activity.this, "content: "+info.getContent(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView);// 使用ImageLoader对图片进行加装！
        }
    };

    /**
     * 初始化轮播广告
     */
    private void initCycleImage(){
        for(int i=0;i < imageUrls.length; i ++){
            ADInfo info = new ADInfo();
            info.setUrl(imageUrls[i]);
            info.setContent("ad " + i);
            infos.add(info);
        }
        mAdView = (ImageCycleView) findViewById(R.id.ad_top);
        mAdView.setImageResources(infos, mAdCycleViewListener);
    }

    private void initImageLoader(){
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .build(); // 创建配置过得DisplayImageOption对象
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdView.startImageCycle();
    };

    @Override
    protected void onPause() {
        super.onPause();
        mAdView.pushImageCycle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdView.pushImageCycle();
    }




}
