package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.*;
import android.content.Intent;
import java.util.HashMap;
import java.util.ArrayList;
/**
 * Created by Administrator on 2016/3/29.
 */
public class HomeActivity extends Activity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

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
                if(position == 0){
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this,FlowUsingActivity.class);
                    startActivity(intent);
                }
            }
        });

        //设置页面数据填充
        int settingicon[] = {R.drawable.home_userinfo,R.drawable.home_wallet,R.drawable.home_recharge,R.drawable.home_bill};
        String settingtext[] = {"个人信息","我的钱包","充值","我的账单"};

        final View setting_content= this.getLayoutInflater().inflate(R.layout.setting, null);
        ListView settinglist = (ListView) setting_content.findViewById(R.id.settinglist);
        ArrayList<HashMap<String, Object>> listdata = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < settingicon.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Image", settingicon[i]);
            map.put("Text", settingtext[i]);
            listdata.add(map);
        }
        SimpleAdapter settinglistadp = new SimpleAdapter(this,listdata,R.layout.settinglist_item,new String[]{"Image","Text"},
                new int[]{R.id.setting_img, R.id.setting_text});
        settinglist.setAdapter(settinglistadp);
        settinglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent();
                    intent.setClass(setting_content.getContext(),BalanceShareActivity .class);
                    startActivity(intent);
                }
            }
        });

        //设置页面弹框
        final PopupWindow popup = new PopupWindow(setting_content,600,800);
        popup.setFocusable(true);   //设置可以获取焦点
        popup.setBackgroundDrawable(new BitmapDrawable()); //防止弹出菜单获取焦点之后，点击activity的其他组件没有响应

        ImageView  image_set= (ImageView) findViewById(R.id.setting);
        final View top =  findViewById(R.id.top_layout);
        image_set.setOnClickListener(new View.OnClickListener() {
            boolean visibilty_flag = true;
            View top =  findViewById(R.id.top_layout);

            public void onClick(View v) {
                if (visibilty_flag) {
                    popup.showAsDropDown(top);
                    visibilty_flag = false;
                } else {
                    popup.dismiss();
                    visibilty_flag = true;
                }
            }
        });
    }
}
