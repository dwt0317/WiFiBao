package com.chinamobile.wifibao.utils.usingFlow;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.chinamobile.wifibao.activity.WifiListActivity;
import com.chinamobile.wifibao.bean.ConnectionPool;
import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.bean.WiFi;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 热点使用后台
 */
public class FlowUsingManager {
    private static FlowUsingManager instance;
    private Context mContext;
    private Handler uiHandler;
    private int useInfoFlag, balanceFlag,useRecordFlag,curConnectFlag;
    private Handler endDetectHandler = new Handler();
    private WiFi curWiFi;
    private String errorMsg;

    public static synchronized FlowUsingManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new FlowUsingManager(context);
        }
        return instance;
    }

    private FlowUsingManager(Context context)
    {
        this.setmContext(context);
    }


    /**
     * 停止使用热点
     */
    public void disconnect(WiFi wifi,UseRecord useRecord,double flowDiff,int flag){
        curWiFi=wifi;
        errorMsg="";
        useInfoFlag= balanceFlag=useRecordFlag=curConnectFlag=0;
        curConnectFlag=1;
        updateUseInfo(wifi, flowDiff,flag);
        updateUseRecord(wifi, useRecord);
        updateUserBalance(wifi, useRecord);
        endDetectHandler.postDelayed(endDetectRunnable,100);
    }

    private Runnable endDetectRunnable  = new Runnable() {
        @Override
        public void run() {
            Toast toast=Toast.makeText(mContext, "正在结算...", Toast.LENGTH_SHORT);
            if(endFinish()==1){
                toast.cancel();
                endDetectHandler.removeCallbacks(endDetectRunnable);
                endDetectHandler.removeMessages(0);
                disconnectWiFi(curWiFi);
                Toast.makeText(mContext,  "结算成功", Toast.LENGTH_LONG).show();
            }else if(endFinish()==0){
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
                endDetectHandler.postDelayed(endDetectRunnable,2000);
            }else{
                disconnectWiFi(curWiFi);
//                Toast.makeText(mContext,  "上传结算数据失败,请稍候再试 \n"+errorMsg, Toast.LENGTH_LONG).show();
            }
            deleteWiFiConfig(curWiFi.getSSID());
        }
    };

    private int endFinish(){
        int undone=0, finish=1, error=2;
        if( useInfoFlag==1 && balanceFlag==1 && useRecordFlag==1 && curConnectFlag==1)
            return finish;
        else if( useInfoFlag==0 || balanceFlag==0 || useRecordFlag==0 || curConnectFlag==0)
            return undone;
        else if( useInfoFlag==2 || balanceFlag==2 || useRecordFlag==2 || curConnectFlag==2)
            return error;
        else
            return undone;
    }

    private void disconnectWiFi(WiFi wifi){
        String SSID = wifi.getSSID();
        WifiManager wifiManager = (WifiManager) getmContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.disconnect();
    }

    /**
     * 在手机中删除wifi设置信息
     */
    private void deleteWiFiConfig(String SSID){
        WifiManager wifiManager = (WifiManager) getmContext().getSystemService(Context.WIFI_SERVICE);
        SharedPreferences sp = mContext.getApplicationContext().getSharedPreferences("WiFiInfo", mContext.MODE_PRIVATE);
        String wifiSSID = sp.getString("WiFiSSID", "");

        //删除之前添加的conf
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+wifiSSID+"\"")){
                wifiManager.removeNetwork(existingConfig.networkId);
            }
        }
    }

    /**
     * 更新用户使用记录
     */
    private void updateUseRecord(final WiFi wifi,final UseRecord useRecord){
        useRecord.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i("bmob", "add use record done!");
                useRecordFlag=1;
            }
            @Override
            public void onFailure(int code, String arg0) {
                useRecordFlag=2;
                errorMsg+=arg0+" ";
                Log.e("bmob", "add use record fail!");
            }
        });
    }


    /**
     * @param wifi
     * 更新connectionPool中的分享的cost和flow
     */
    //0:被动断开 1：主动断开 2：没有断开
    public void updateUseInfo(WiFi wifi, final double trafficDiff,final int flag){
        BmobQuery<ConnectionPool> query = new BmobQuery<ConnectionPool>();
        query.addWhereEqualTo("WiFi", wifi);
        query.findObjects(mContext, new FindListener<ConnectionPool>() {
            @Override
            public void onSuccess(List<ConnectionPool> object) {
                ConnectionPool conn = object.get(0);
                conn.setFlowUsed(trafficDiff);
                double cost = trafficDiff * 0.05;
                conn.setCost(cost);
                if(flag==2)
                    conn.setCurConnect(conn.getCurConnect());
                else {
                    conn.setCurConnect(conn.getCurConnect()-1);
                }
                conn.update(mContext, conn.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        useInfoFlag=1;
                        Log.i("bmob", "流量花费更新成功");
                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        useInfoFlag=2;
                        errorMsg+=msg+" ";
                        Log.e("bmob", "流量花费更新失败：" + msg);
//                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i("bomb", "写入cost和flow成功 " + trafficDiff);
            }

            @Override
            public void onError(int code, String msg) {
                errorMsg+=msg+" ";
                useInfoFlag=2;
                Log.e("bomb", "写入cost和flow失败");
//                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }



    /**
     * 更新用户余额
     */
    private void updateUserBalance(final WiFi wifi, final UseRecord useRecord){
        User user = User.getCurrentUser(mContext,User.class);
        user.setBalance(user.getBalance() - useRecord.getCost());
        user.setFlowUsed(user.getFlowUsed()-useRecord.getFlowUsed());
        user.update(mContext, user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                balanceFlag=1;
                Log.i("bmob", "balance 更新成功");
            }

            @Override
            public void onFailure(int code, String msg) {
                errorMsg+=msg+" ";
                balanceFlag=2;
                Log.i("bmob", "balance 更新失败：" + msg);
            }
        });
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public Handler getUiHandler() {
        return uiHandler;
    }

    public void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }
}
