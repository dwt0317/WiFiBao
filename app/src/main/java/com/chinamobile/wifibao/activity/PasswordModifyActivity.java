package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.utils.SignupManager;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 修改密码页面
 */
public class PasswordModifyActivity extends Activity {
    private Context mContext = PasswordModifyActivity.this;
    private Button modifyButton = null;
    private EditText password1 = null;
    private EditText password2 = null;
    private EditText password = null;
    private String oldPassword = "123";
    private String newPassword1;
    private String newPassword2 ;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.passwordmodify);

        modifyButton = (Button) findViewById(R.id.modify);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validInput();
            }
        });
        ImageView goback = (ImageView)findViewById(R.id.back);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordModifyActivity.this, PersonalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


    }

    /**
     * 检测密码输入是否有效
     */
    private void validInput() {
        password1 = (EditText) findViewById(R.id.newPassword1);
        password2 = (EditText) findViewById(R.id.newPassword2);
        password = (EditText) findViewById(R.id.oldPassword);
        oldPassword = password.getText().toString().trim();
        newPassword1 = password1.getText().toString().trim();
        newPassword2 = password2.getText().toString().trim();
        if (oldPassword.isEmpty()) {
            System.out.print(oldPassword);
            Toast.makeText(PasswordModifyActivity.this, "请输入旧密码!"+ oldPassword, Toast.LENGTH_SHORT).show();

        } else if (newPassword1 == null || newPassword1.equals("")) {
            Toast.makeText(PasswordModifyActivity.this, "请输入新密码！", Toast.LENGTH_SHORT).show();
            //password1.setError("请输入新密码！");
            //return false;
        } else if (newPassword2 == null || newPassword2.equals("")) {
            Toast.makeText(PasswordModifyActivity.this, "请输入确认密码！", Toast.LENGTH_SHORT).show();
            //password2.setError("请输入确认密码！");
            //return false;
        } else if (!newPassword1.equals(newPassword2)) {
            Toast.makeText(PasswordModifyActivity.this, "两次输入新密码不一致！" + newPassword1 + " " + newPassword2, Toast.LENGTH_SHORT).show();
            //password2.setError("两次输入新密码不一致！");
            //return false;
        } else {


            final Handler modifyHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        Toast.makeText(mContext, "修改成功！",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(mContext, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        String error = SignupManager.getInstance(mContext).getErrorMsg();
                        Toast.makeText(mContext, error,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };
            SignupManager.getInstance(mContext).setUiHandler(modifyHandler);
            SignupManager.getInstance(mContext).modifyPassword(oldPassword, newPassword1);
        }
    }
}


