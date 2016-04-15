package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chinamobile.wifibao.bean.ShareRecord;
import com.chinamobile.wifibao.bean.WiFi;


import cn.bmob.v3.listener.SaveListener;

/**
 * Created by cdd on 2016/3/31.
 */
public class DatabaseUtil {
    private static DatabaseUtil du;

    private boolean savaSuccess;
    private String apObjectId;

    private DatabaseUtil(){}
    public static DatabaseUtil getInstance(){
        if(du == null)
            du = new DatabaseUtil();
        return du;
    }

    public void writeApToDatabase(Context context, final Handler handler,final WiFi wifiAp) {
        if (wifiAp != null) {
            wifiAp.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    apObjectId = wifiAp.getObjectId();
                    Log.i("DatabaseUtil添加数据成功,", apObjectId);
                    Message message = new Message();
                    message.arg1=1;
                    handler.sendMessage(message);
                }

                @Override
                public void onFailure(int code, String arg0) {
                    Log.i("DatabaseUtil error,", "添加数据失败!!!!");
                    Message message = new Message();
                    message.arg1=0;
                    handler.sendMessage(message);
                }
            });
        }
    }

    public boolean writeShareToDatabase(Context context, final ShareRecord shareRecord){
        savaSuccess = false;
        if(shareRecord != null){
            shareRecord.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.i("DatabaseUtil添加数据成功,", shareRecord.getObjectId());
                    savaSuccess = true;
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.i("DatabaseUtil error,", "添加数据失败!!!!");
                    savaSuccess = false;
                }
            });

        }

        return savaSuccess;
    }
}
