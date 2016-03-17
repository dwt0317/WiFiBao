package com.chinamobile.wifibao.handler;

import android.content.Context;

import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.bean._User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by dwt on 2016/3/17.
 */
public class UseHandler {
    private ArrayList<WiFi> wifiList=new ArrayList<WiFi>();;
    private ArrayList<_User> userList = new ArrayList<_User>();
    private Context mContext;

    private UseHandler(Context context)
    {
        this.mContext = context;
    }


    public ArrayList<WiFi> getNearbyWiFi(double[] userLoc){
        BmobGeoPoint userPoint = new BmobGeoPoint(userLoc[0], userLoc[1]);
        BmobQuery<WiFi> bmobQuery = new BmobQuery<WiFi>();
        bmobQuery.addWhereNear("location",userPoint);
        bmobQuery.setLimit(10);    //获取最接近用户地点的10条数据
        bmobQuery.findObjects(mContext, new FindListener<WiFi>() {
            @Override
            public void onSuccess(List<WiFi> object) {
                // TODO Auto-generated method stub
//                toast("查询成功：共" + object.size() + "条数据。");
//                System.out.println("查询成功：共" + object.size() + "条数据。");
                wifiList=new ArrayList<WiFi>(object);
            }
            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
//                toast("查询失败：" + msg);
                System.out.println("查询失败：" + msg);
            }
        });


        return this.wifiList;
    }
}
