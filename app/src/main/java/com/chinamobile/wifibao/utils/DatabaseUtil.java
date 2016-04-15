package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chinamobile.wifibao.bean.ShareRecord;
import com.chinamobile.wifibao.bean.WiFi;


import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by cdd on 2016/3/31.
 */
public class DatabaseUtil {
    private static DatabaseUtil du;

    private boolean savaSuccess;
    private String apObjectId;

    private DatabaseUtil() {
    }

    public static DatabaseUtil getInstance() {
        if (du == null)
            du = new DatabaseUtil();
        return du;
    }

    public void writeApToDatabase(final Context context, final Handler handler, final WiFi wifiAp) {
        if(wifiAp == null){
            Message message = new Message();
            message.arg1 = 0;
            handler.sendMessage(message);
        }

        BmobQuery<WiFi> query = new BmobQuery<WiFi>();

        query.addWhereEqualTo("BSSID", wifiAp.getBSSID());
        query.addWhereEqualTo("SSID", wifiAp.getBSSID());
        query.addWhereEqualTo("password", wifiAp.getPassword());
        query.addWhereEqualTo("upperLimit", wifiAp.getUpperLimit());
        query.addWhereEqualTo("maxConnect", wifiAp.getMaxConnect());

        query.setLimit(10);

        query.findObjects(context, new FindListener<WiFi>() {
            @Override
            public void onSuccess(List<WiFi> object) {
                Log.i("DatabaseU obj: ",String.valueOf(object.size()));
                final Message message = new Message();
                message.what=0;
                for (WiFi obj : object) {
                    //修改状态
                    obj.setState(true);
                    obj.update(context,obj.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("update succ", "!");
                            message.what+=1;
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.i("update erro", s);

                        }
                    });
                }
                if(message.what == object.size()){
                    message.arg1 = 1;
                }else{
                    message.arg1 = 0;
                }
                handler.sendMessage(message);

            }

            @Override
            public void onError(int code, String msg) {
                Log.i("查询失败，执行插入：", msg);

                //wifiAp.setState(true);
                wifiAp.save(context, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        apObjectId = wifiAp.getObjectId();
                        Log.i("DatabaseUtil添加数据成功,", apObjectId);
                        Message message = new Message();
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(int code, String arg0) {
                        Log.i("DatabaseUtil error,", "添加数据失败!!!!");
                        Message message = new Message();
                        message.arg1 = 0;
                        handler.sendMessage(message);
                    }
                });
            }
        });

    }

    public boolean writeShareToDatabase(Context context, final ShareRecord shareRecord) {
        savaSuccess = false;
        if (shareRecord != null) {
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
