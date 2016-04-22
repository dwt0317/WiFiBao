package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chinamobile.wifibao.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by lab on 2016/4/19.
 */
public class PasswordModifyActivity extends Activity {
    private Context mContext = null;
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


    }


    private void validInput() {
        password1 = (EditText) findViewById(R.id.newPassword1);
        password2 = (EditText) findViewById(R.id.newPassword2);
        password = (EditText) findViewById(R.id.oldPassword);
        oldPassword = password.getText().toString().trim();
        newPassword1 = password1.getText().toString().trim();
        newPassword2 = password2.getText().toString().trim();
        /*
        Toast.makeText(PasswordModifyActivity.this,
                oldPassword + " " + newPassword1 + " " + newPassword2,
                Toast.LENGTH_SHORT).show();
        */
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
            MyTask task = new MyTask();
            task.execute();
        }


    }
    private void modify() {
        BmobUser.updateCurrentUserPassword(mContext, oldPassword, newPassword1, new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.i("bmob", "密码修改成功：");
                Toast.makeText(PasswordModifyActivity.this, "密码修改成功！ ",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setClass(PasswordModifyActivity.this, HomeActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("bmob", "密码修改失败：" + s + "(" + i + ")");
                Toast.makeText(PasswordModifyActivity.this, "密码修改失败！",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            modify();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }





}


