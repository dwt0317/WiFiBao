package com.chinamobile.wifibao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.User;
import com.chinamobile.wifibao.utils.LoginManager;
import com.chinamobile.wifibao.utils.SignupManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private ImageView home;
    private EditText phonenumber;
    private TextView username;
    private EditText password;
    private EditText repwd;
    private EditText verifyCode;
    private Button register;
    private Button verifyCodeBtn;

    private String usernameStr;
    private String phonenumberStr;
    private String passwordStr;
    private String rePwdStr;
    private String veriCodeStr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signup);
        setViewComponent();
    }

    private void setViewComponent() {
        phonenumber = (EditText) findViewById(R.id.phonenumber);
        username = (TextView) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.RegPwd);
        repwd = (EditText) findViewById(R.id.RegPwdAgain);
        verifyCode = (EditText) findViewById(R.id.RegMail);
        register = (Button) findViewById(R.id.register);
        verifyCodeBtn=(Button) findViewById(R.id.verifyCodeBtn);
        password.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repwd.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!verifyInput()) return;

                else {
                    final User user = constructUser();
                    //注册
                    final Handler signUpHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 1) {
                                Toast.makeText(SignupActivity.this, "注册成功！",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(SignupActivity.this, Home2Activity.class);
                                startActivity(intent);
                            } else {
                                String error = SignupManager.getInstance(SignupActivity.this).getErrorMsg();
                                Toast.makeText(SignupActivity.this, error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    SignupManager.getInstance(SignupActivity.this).setUiHandler(signUpHandler);
                    SignupManager.getInstance(SignupActivity.this).signUpOrLogin(user, veriCodeStr);
                }
            }
        });

        verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber=phonenumber.getText().toString().trim();
                if (phoneNumber == null || phoneNumber.equals("")) {
                    phonenumber.setError("手机号不能为空");
                    return;
                }
                //发送验证码
                final Handler verifySendHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == 1) {
                            Toast.makeText(SignupActivity.this, "验证码已发送，请注意查收",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String error = LoginManager.getInstance(SignupActivity.this).getErrorMsg();
                            Toast.makeText(SignupActivity.this, error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                SignupManager.getInstance(SignupActivity.this).setUiHandler(verifySendHandler);
                SignupManager.getInstance(SignupActivity.this).requestSMSCode(phoneNumber);
            }
        });

        home  = (ImageView) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SignupActivity.this,Home2Activity.class);
                startActivity(intent);
            }
        });
    }

    private User constructUser(){
        User user = new User();
        user.setBalance(110.0);
        user.setFlowUsed(0.0);
        user.setUsername(usernameStr);
        user.setPassword(passwordStr);
        user.setFlowShared(.0);
        user.setMobilePhoneNumber(phonenumberStr);
        user.setMobilePhoneNumberVerified(true);
        return user;
    }

    private boolean verifyInput(){
        phonenumberStr = phonenumber.getText().toString().trim();
        usernameStr = username.getText().toString().trim();
        passwordStr = password.getText().toString().trim();
        rePwdStr = repwd.getText().toString().trim();
        veriCodeStr = verifyCode.getText().toString();


        if (phonenumberStr == null || phonenumberStr.equals("")) {
            phonenumber.setError("手机号不能为空");
            return false;
        }
        Pattern pPhone = Pattern.compile("^(13[0-9]|15[0-9]|17[678]|18[0-9]|14[57])[0-9]{8}$") ;
        Matcher m = pPhone.matcher(phonenumberStr);
        if(!m.matches()){
            phonenumber.setError("请输入正确的手机号");
            return false;
        }
        if (usernameStr == null || usernameStr.equals("")) {
            username.setError("昵称不能为空");
            return false;
        }

        if (passwordStr == null || passwordStr.equals("")) {
            password.setError("密码不能为空");
            return false;
        }
        if (rePwdStr == null || rePwdStr.equals("")) {
            password.setError("请再次输入密码");
            return false;
        }

        if (!rePwdStr.equals(passwordStr)) {
            repwd.setError("两次输入密码不一致");
            return false;
        }

        if (veriCodeStr == null || veriCodeStr.equals("")) {
            verifyCode.setError("请输入验证码");
            return false;
        }
        return true;
    }
}

