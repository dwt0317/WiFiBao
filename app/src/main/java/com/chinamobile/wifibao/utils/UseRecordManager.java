package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.chinamobile.wifibao.bean.ConnectionPool;
import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 使用记录后台
 */
public class UseRecordManager {
    private static UseRecordManager instance;
    private Context mContext;
    private Handler uiHandler;
    private String errorMsg;
    private  ArrayList<UseRecord> useRecordList;
    private LinkedHashMap<String,Integer> recordsSepByMonth = new LinkedHashMap<String,Integer>();

    public static synchronized UseRecordManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new UseRecordManager(context);
        }
        return instance;
    }

    /**
     * 查询使用记录
     */
    public void queryUseRecord(User user){
        BmobQuery<UseRecord> bmobQuery = new BmobQuery<UseRecord>();
        bmobQuery.include("WiFi");
        bmobQuery.addWhereEqualTo("user", user);
        bmobQuery.findObjects(mContext, new FindListener<UseRecord>() {
            @Override
            public void onSuccess(List<UseRecord> recordList) {
                useRecordList= new ArrayList<>(recordList);
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
                Log.i("bmob","read record done");
            }
            @Override
            public void onError(int code, String msg) {
                Log.e("bomb", "read useRecord fail");
                Toast toast = Toast.makeText(mContext, code+" " +msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
            }
        });
    }


    private UseRecordManager(Context context)
    {
        this.setmContext(context);
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

    public String getErrorMsg() {
        return errorMsg;
    }

    public ArrayList<UseRecord> getUseRecordList() {
        return useRecordList;
    }

    public LinkedHashMap<String, Integer> getRecordsSepByMonth() {
        return recordsSepByMonth;
    }

    public void setRecordsSepByMonth(LinkedHashMap<String, Integer> recordsSepByMonth) {
        this.recordsSepByMonth = recordsSepByMonth;
    }
}
