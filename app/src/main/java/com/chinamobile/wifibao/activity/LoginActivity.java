package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.chinamobile.wifibao.R;

/**
 * Created by apple on 2016/4/6.
 */
public class LoginActivity extends Activity {
    Button loginbutton;
    CheckBox savePassword;
    EditText password;
    AutoCompleteTextView username;
    SharedPreferences sp;
    String usernameStr;
    String passwordStr;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); 设置无标题
        setViewComponent();
    }

    private void setViewComponent() {
        setContentView(R.layout.login);

        username = (AutoCompleteTextView) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        sp = this.getSharedPreferences("passwordFile", MODE_PRIVATE);
        savePassword = (CheckBox) findViewById(R.id.savePassword);
        savePassword.setChecked(true);// 默认为记住密码
        username.setThreshold(1);// 输入1个字母就开始自动提示
        password.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);// 隐藏密码为InputType.TYPE_TEXT_VARIATION_PASSWORD，也就是0x81
                                                              // 显示密码为InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD，也就是0x91

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String[] allUserName = new String[sp.getAll().size()];// sp.getAll().size()返回的是有多少个键值对
                allUserName = sp.getAll().keySet().toArray(new String[0]);
                // sp.getAll()返回一张hash map
                // keySet()得到的是a set of the keys.
                // hash map是由key-value组成的

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        allUserName);

                username.setAdapter(adapter);// 设置数据适配器
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                password.setText(sp.getString(username.getText()
                        .toString(), ""));// 自动输入密码
            }
        });

        loginbutton = (Button)findViewById(R.id.loginbutton);//登陆按钮
        loginbutton.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
                // TODO Auto-generated method stub

                usernameStr = username.getText().toString();
                passwordStr = password.getText().toString();
                if (TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(passwordStr)) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空",
                            Toast.LENGTH_SHORT).show();
                }else if (!((usernameStr.equals("test")) && (passwordStr
                        .equals("test")))) {                                        //test test
                    Toast.makeText(LoginActivity.this, "密码错误，请重新输入",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (savePassword.isChecked()) {// 登陆成功才保存密码
                        sp.edit().putString(usernameStr, passwordStr).commit();
                    }
                    Toast.makeText(LoginActivity.this, "登陆成功，正在获取用户数据……",
                            Toast.LENGTH_SHORT).show();
                    // 跳转到另一个Activity
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

}
