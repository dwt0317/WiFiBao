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
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by dwt on 2016/4/20.
 */
public class UseRecordManager {
    private static UseRecordManager instance;
    private Context mContext;
    private Handler uiHandler;
    private String errorMsg;
    private  ArrayList<UseRecord> useRecordList;
    private HashMap<String,Integer> recordsSepByMonth = new HashMap<String,Integer>();

    public static synchronized UseRecordManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new UseRecordManager(context);
        }
        return instance;
    }

    public void queryUseRecord(User user){
        BmobQuery<UseRecord> bmobQuery = new BmobQuery<UseRecord>();
//        bmobQuery.addWhereEqualTo("objectId","661e74889d");
        bmobQuery.findObjects(mContext, new FindListener<UseRecord>() {
            @Override
            public void onSuccess(List<UseRecord> recordList) {
                useRecordList= new ArrayList<UseRecord>(recordList);
                separateRecords();
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

    private  void separateRecords(){
        ArrayList<String> months = new ArrayList<String>();
        for(int i=0;i<useRecordList.size();i++){
            UseRecord item = useRecordList.get(i);
            String dateStr = item.getStartTime().getDate();
            String[] dateArr = dateStr.split("-");
            String year_month=dateArr[0]+"-"+dateArr[1];
            if(!getRecordsSepByMonth().containsKey(year_month)){
                getRecordsSepByMonth().put(year_month, i);
            }else
                getRecordsSepByMonth().put(year_month, getRecordsSepByMonth().get(year_month) + 1);
        }
        Iterator<String> iter = getRecordsSepByMonth().keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            Integer value = getRecordsSepByMonth().get(key);
            Log.i("record",key+" "+value);

        }

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

    public HashMap<String, Integer> getRecordsSepByMonth() {
        return recordsSepByMonth;
    }

    public void setRecordsSepByMonth(HashMap<String, Integer> recordsSepByMonth) {
        this.recordsSepByMonth = recordsSepByMonth;
    }
}
