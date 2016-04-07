package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chinamobile.wifibao.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by dwt on 2016/4/6.
 */
public class LoginManager {
    private static LoginManager instance;
    private Context mContext;
    private Handler uiHandler;
    private User loginUser;


    public static synchronized LoginManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new LoginManager(context);
        }
        return instance;
    }


    public void loginByPhoneNumber(String phoneNumber,String password){

        BmobUser.loginByAccount(mContext, phoneNumber, password, new LogInListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                // TODO Auto-generated method stub
                if (user != null) {
                    loginUser=user;
                    Log.i("login", "用户登陆成功");
                    Message msg = new Message();
                    msg.what = 1;
                    getUiHandler().sendMessage(msg);
                }else{
                    Log.e("login",e.toString());
                }
            }
        });
    }


    private LoginManager(Context context)
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
}
