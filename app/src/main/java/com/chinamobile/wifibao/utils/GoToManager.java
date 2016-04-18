package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.chinamobile.wifibao.activity.FlowUsingActivity;
import com.chinamobile.wifibao.activity.LoginActivity;
import com.chinamobile.wifibao.activity.WifiDetailsActivity;

import cn.bmob.v3.BmobUser;

/**
 * Created by dwt on 2016/4/8.
 */
public class GoToManager {

    private static GoToManager instance;
    private Context mContext;
    private Handler uiHandler;

    public static synchronized GoToManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new GoToManager(context);
        }
        return instance;
    }

    private GoToManager(Context context)
    {
        this.setmContext(context);
    }

    public void goToActivity(Intent destIntent){
        BmobUser bmobUser = BmobUser.getCurrentUser(mContext);
        if(bmobUser == null){
            Intent intent = new Intent();
            intent.setClass(mContext,LoginActivity.class);
            mContext.startActivity(intent);
        }else{
            mContext.startActivity(destIntent);
        }
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
