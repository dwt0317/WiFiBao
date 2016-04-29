package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chinamobile.wifibao.bean.User;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by dwt on 2016/4/13.
 */
public class SignupManager {
    private static SignupManager instance;
    private Context mContext;
    private Handler uiHandler;
    private String errorMsg;


    public static synchronized SignupManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new SignupManager(context);
        }
        return instance;
    }


    public void requestSMSCode(String phoneNumber){
        BmobSMS.requestSMSCode(mContext, phoneNumber, "wifiBao", new RequestSMSCodeListener() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {//验证码发送成功
                    Log.i("bmob", "短信id：" + smsId);//用于查询本次短信发送详情
                    Message msg = new Message();
                    msg.what = 1;
                    getUiHandler().sendMessage(msg);
                }else{
                    Log.e("bmob",ex.getLocalizedMessage());
                    errorMsg = ex.getLocalizedMessage();
                    Message msg = new Message();
                    msg.what = 0;
                    getUiHandler().sendMessage(msg);
                }
            }
        });
    }

    public void signUpOrLogin(final User user,String verifyCode){
        user.signOrLogin(mContext, verifyCode, new SaveListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.i("bmob", "" + user.getUsername() + "一键注册登陆成功");
                Message msg = new Message();
                msg.what = 1;
                getUiHandler().sendMessage(msg);
            }

            @Override
            public void onFailure(int code, String error) {
                // TODO Auto-generated method stub
                Log.e("bmob", error);
                errorMsg = error;
                Message msg = new Message();
                msg.what = 0;
                getUiHandler().sendMessage(msg);
            }
        });
    }
    //修改密码
    public void modifyPassword( String  oldPassword, String newPassword){

        BmobUser.updateCurrentUserPassword(mContext, oldPassword, newPassword,
                new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.i("bmob", "密码修改成功：");
                        Message msg = new Message();
                        msg.what = 1;
                        getUiHandler().sendMessage(msg);
                    }

                    @Override
                    public void onFailure(int i, String s) {

                        Log.i("bmob", "密码修改失败：" + s + "(" + i + ")");
                        errorMsg = s;
                        Message msg = new Message();
                        msg.what = 0;
                        getUiHandler().sendMessage(msg);

                    }
                });

    }

    public void verifySMSCode(String phoneNumber, String verifyCode){
        BmobSMS.verifySmsCode(mContext,phoneNumber, verifyCode, new VerifySMSCodeListener() {

            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if(ex==null){//短信验证码已验证成功
                    Log.i("bmob", "验证通过");
                    Message msg = new Message();
                    msg.what = 1;
                    getUiHandler().sendMessage(msg);
                }else{
                    Log.i("bmob", "验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                    errorMsg = ex.getLocalizedMessage();
                    Message msg = new Message();
                    msg.what = 0;
                    getUiHandler().sendMessage(msg);
                }
            }
        });
    }

    private SignupManager(Context context)
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

}
