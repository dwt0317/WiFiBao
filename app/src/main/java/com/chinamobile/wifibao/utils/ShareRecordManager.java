package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.chinamobile.wifibao.bean.ShareRecord;
import com.chinamobile.wifibao.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 分享记录后台
 */
public class ShareRecordManager {

    private static ShareRecordManager instance;
    private Context mContext;
    private Handler uiHandler;
    private ArrayList<ShareRecord> shareRecordList;
    private HashMap<String,Integer> recordsSepByMonth = new HashMap<String,Integer>();

    public static synchronized ShareRecordManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new ShareRecordManager(context);
        }
        return instance;
    }
    private ShareRecordManager(Context context)
    {
        this.mContext = context;
    }

    /**
     * 查询分享记录
     * @param user
     */
    public void queryShareRecord(User user){
        BmobQuery<ShareRecord> bmobQuery = new BmobQuery<ShareRecord>();
        bmobQuery.include("WiFi");
        bmobQuery.addWhereEqualTo("user", user);
        bmobQuery.findObjects(mContext, new FindListener<ShareRecord>() {
            @Override
            public void onSuccess(List<ShareRecord> recordList) {
                shareRecordList= new ArrayList<ShareRecord>(recordList);
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
                Log.i("bmob", "read record done");
            }

            @Override
            public void onError(int code, String msg) {
                Message mess = new Message();
                mess.what = 0;
                getUiHandler().sendMessage(mess);
                Log.e("bomb", "read useRecord fail");
                Toast toast = Toast.makeText(mContext, code+" " +msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 10); //设置文本的位置，使文本显示靠下一些
                toast.show();
            }
        });
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

    public ArrayList<ShareRecord> getShareRecordList() {
        return shareRecordList;
    }

    public HashMap<String, Integer> getRecordsSepByMonth() {
        return recordsSepByMonth;
    }

}
