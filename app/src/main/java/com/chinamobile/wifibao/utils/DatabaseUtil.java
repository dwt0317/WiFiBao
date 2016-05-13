package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chinamobile.wifibao.bean.ConnectionPool;
import com.chinamobile.wifibao.bean.ShareRecord;
import com.chinamobile.wifibao.bean.WiFi;


import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by cdd on 2016/3/31.
 */
public class DatabaseUtil {
    private static DatabaseUtil du;

    //private boolean savaSuccess;
    //private String apObjectId;

    private DatabaseUtil() {
    }

    public static DatabaseUtil getInstance() {
        if (du == null)
            du = new DatabaseUtil();
        return du;
    }

    /**
     * 写入热点信息
     * @param context
     * @param handler
     * @param wifiAp
     */
    public void writeApToDatabase(final Context context, final Handler handler, final WiFi wifiAp) {
        if(wifiAp == null){
            Message message = new Message();
            message.arg1 = 0;
            handler.sendMessage(message);
        }

        BmobQuery<WiFi> query = new BmobQuery<WiFi>();

        query.addWhereEqualTo("BSSID", wifiAp.getBSSID());
        query.addWhereEqualTo("SSID", wifiAp.getSSID());
        query.addWhereEqualTo("password", wifiAp.getPassword());
        query.addWhereEqualTo("upperLimit", wifiAp.getUpperLimit());
        query.addWhereEqualTo("maxConnect", wifiAp.getMaxConnect());
        query.addWhereEqualTo("user", wifiAp.getUser());

        query.setLimit(10);

        query.findObjects(context, new FindListener<WiFi>() {
            @Override
            public void onSuccess(final List<WiFi> object) {
                final int len = object.size();
                Log.i("DatabaseU obj: ",String.valueOf(len));
                if(len == 0){
                    wifiAp.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            //apObjectId = wifiAp.getObjectId();
                            Log.i("DatabaseUtil添加数据成功,", wifiAp.getObjectId());
                            Message message = new Message();
                            message.arg1 = 1;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onFailure(int code, String arg0) {
                            Log.i("DatabaseUtil error,", arg0);
                            Message message = new Message();
                            message.arg1 = 0;
                            handler.sendMessage(message);
                        }
                    });
                    return;
                }
                //数据库中存在，保存id
                wifiAp.setObjectId(object.get(0).getObjectId());
                final Message message = new Message();
                message.what=0;
                //数据库中存在记录，更新记录，不必插入
                for (final WiFi obj : object) {
                    //修改状态
                    obj.setState(true);
                    obj.update(context,obj.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("update succ", "!");
                            message.what+=1;
                            //异步，不要再外面判断
                            if(message.what == len){
                                Log.i("message what: ",String.valueOf(message.what));
                                message.arg1 = 1;
                                handler.sendMessage(message);
                            }else if(obj.equals(object.get(len-1))){
                                Log.i("message what: ",String.valueOf(message.what));
                                message.arg1 = 0;
                                handler.sendMessage(message);
                            }

                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.i("update erro", s);
                            Message message = new Message();
                            message.arg1 = 0;
                            handler.sendMessage(message);

                        }
                    });
                }
                //异步，不要再外面判断

            }

            @Override
            public void onError(int code, String msg) {
                Log.i("查询失败", msg);// The network is not available,please check your network!
                Message message = new Message();
                message.arg1 = 0;
                handler.sendMessage(message);
            }
        });

    }

    /**
     * 分享记录写入
     * @param context
     * @param shareRecord
     * @return
     */
    public void writeShareRecordToDatabase(Context context, final Handler handler, final ShareRecord shareRecord) {
        //savaSuccess = false;
        if (shareRecord != null) {
            shareRecord.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.i("DatabaseUtil添加数据成功,", shareRecord.getObjectId());
                    Message mess = new Message();
                    mess.arg1=1;
                    handler.sendMessage(mess);
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.i("DatabaseUtil error,", s);
                    Message mess = new Message();
                    mess.arg1=0;
                    handler.sendMessage(mess);
                }
            });

        }

        //return savaSuccess;//异步操作这样做没有效果
    }




    /**
     * 写入热点到ConnectPool信息
     * @param context
     * @param handler
     * @param wifiAp
     */
    public void writeApToPool(final Context context, final Handler handler, final WiFi wifiAp) {
        if(wifiAp == null){
            Message message = new Message();
            message.arg1 = 0;
            handler.sendMessage(message);
        }
        //形成记录
        final ConnectionPool cp = new ConnectionPool();
        cp.setWiFi(wifiAp);
        cp.setMaxConnect(wifiAp.getMaxConnect());
        cp.setCurConnect(0);
        cp.setFlowUsed(0.0);
        cp.setCost(0.0);
        cp.setUser(wifiAp.getUser());

        BmobQuery<ConnectionPool> query = new BmobQuery<ConnectionPool>();
        //query.include("WiFi");
        query.addWhereEqualTo("WiFi", wifiAp);
        query.addWhereEqualTo("User", wifiAp.getUser());
        query.setLimit(10);

        query.findObjects(context, new FindListener<ConnectionPool>() {
            @Override
            public void onSuccess(final List<ConnectionPool> object) {
                final int len = object.size();
                Log.i("DatabaseU obj: ", String.valueOf(len));
                //数据库中不存在该wifi对应的connetionpool信息
                if (len == 0) {
                    cp.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            //apObjectId = wifiAp.getObjectId();
                            Log.i("DatabaseUtil添加数据成功,", cp.getObjectId());
                            writeConnectionPoolInCache(cp, context);
                            Message message = new Message();
                            message.arg1 = 1;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onFailure(int code, String arg0) {
                            Log.i("DatabaseUtil error,", arg0);
                            Message message = new Message();
                            message.arg1 = 0;
                            handler.sendMessage(message);
                        }
                    });
                    return;
                }
                //数据库中存在wifi对应的ConnectionPool，保存id
                cp.setObjectId(object.get(0).getObjectId());
                writeConnectionPoolInCache(cp, context);
                final Message message = new Message();
                message.what = 0;
                //数据库中存在记录，更新记录，不必插入
                for (final ConnectionPool obj : object) {
                    //修改状态
                    obj.setMaxConnect(wifiAp.getMaxConnect());
                    obj.setCurConnect(0);
                    obj.setFlowUsed(0.0);
                    obj.setCost(0.0);
                    obj.update(context, obj.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("update succ", "!");
                            message.what += 1;
                            //异步，不要再外面判断
                            if (message.what == len) {
                                Log.i("message what: ", String.valueOf(message.what));
                                message.arg1 = 1;
                                handler.sendMessage(message);
                            } else if (obj.equals(object.get(len - 1))) {//到最后一个
                                Log.i("message what: ", String.valueOf(message.what));
                                message.arg1 = 0;
                                handler.sendMessage(message);
                            }

                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.i("update erro", s);
                            Message message = new Message();
                            message.arg1 = 0;
                            handler.sendMessage(message);

                        }
                    });
                }
                //异步，不要再外面判断

            }

            @Override
            public void onError(int code, String msg) {
                Log.i("查询失败", msg);// The network is not available,please check your network!
                Message message = new Message();
                message.arg1 = 0;
                handler.sendMessage(message);
            }
        });

    }

    /***
     * 删除连接池记录
     * @param context
     */
    public void deletefromConnetionPool(Context context){
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("ConnectionPoolIFNO", context.MODE_PRIVATE);
        String objId = sp.getString("ConnetctionPoolId", "");
        ConnectionPool cp = new ConnectionPool();
        cp.setObjectId(objId);
        cp.delete(context, new DeleteListener() {
            @Override
            public void onSuccess() {
                Log.i("bmob","删除成功");
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i("bmob","删除失败："+msg);
            }
        });
    }

    /***
     * 本地缓存
     * @param cp
     * @param context
     */
    private void writeConnectionPoolInCache(ConnectionPool cp, Context context){
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("ConnectionPoolIFNO", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ConnetctionPoolId",cp.getObjectId());
        editor.commit();
    }

}
