package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.util.Log;

import com.chinamobile.wifibao.bean.WiFi;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by cdd on 2016/3/31.
 */
public class DatabaseUtil {

    private DatabaseUtil(){}
    private static DatabaseUtil du;

    public static DatabaseUtil getInstance(){
        if(du == null)
            du = new DatabaseUtil();
        return du;
    }

    public  static void writeApToDatabase(Context context, final WiFi wifiAp){
        if(wifiAp!=null){
            wifiAp.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.i("添加数据成功，返回objectId为：",wifiAp.getObjectId());
                }

                @Override
                public void onFailure(int code, String arg0) {
                    Log.i("error：","添加数据失败!!!!");
                }
            });
        }
    }
}
