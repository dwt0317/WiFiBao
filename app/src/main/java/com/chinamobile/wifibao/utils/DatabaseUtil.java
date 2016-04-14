package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.util.Log;

import com.chinamobile.wifibao.bean.ShareRecord;
import com.chinamobile.wifibao.bean.WiFi;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by cdd on 2016/3/31.
 */
public class DatabaseUtil {

    private DatabaseUtil(){}
    private static DatabaseUtil du;
    private boolean savaSuccess;
    private String apObjectId;

    public static DatabaseUtil getInstance(){
        if(du == null)
            du = new DatabaseUtil();
        return du;
    }

    public boolean writeApToDatabase(Context context, final WiFi wifiAp) {
        savaSuccess = false;
        if (wifiAp != null) {
            wifiAp.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    apObjectId = wifiAp.getObjectId();
                    Log.i("添加数据成功，返回objectId为：", apObjectId);
                    //Log.i("succe:","添加数据成功");
                    savaSuccess = true;
                }

                @Override
                public void onFailure(int code, String arg0) {
                    Log.i("error：", "添加数据失败!!!!");
                    savaSuccess = false;
                }
            });
        }
        return savaSuccess;
    }

    public boolean writeShareToDatabase(Context context, final ShareRecord shareRecord){
        savaSuccess = false;
        if(shareRecord != null){
            shareRecord.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.i("添加数据成功，返回objectId为：", shareRecord.getObjectId());
                    savaSuccess = true;
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.i("error：", "添加数据失败!!!!");
                    savaSuccess = false;
                }
            });

        }

        return savaSuccess;
    }
}
