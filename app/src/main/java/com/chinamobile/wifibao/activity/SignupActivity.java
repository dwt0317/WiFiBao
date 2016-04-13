package com.chinamobile.wifibao.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.text.InputType;

import com.chinamobile.wifibao.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText phonenumber;
    private TextView username;
    private EditText password;
    private EditText repassword;
    private EditText veriCode;
    private Button register;

    private String usernameStr;
    private String phonenumberStr;
    private String passwordStr;
    private String rePwdStr;
    private String veriCodeStr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        setViewComponent();
    }

    private void setViewComponent() {
        phonenumber = (EditText)findViewById(R.id.phonenumber);
        username = (TextView)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.RegPwd);
        repassword = (EditText)findViewById(R.id.RegPwdAgain);
        veriCode = (EditText)findViewById(R.id.RegMail);
        register = (Button)findViewById(R.id.register);

        password.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        repassword.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verifyInput()) return;
            }
        });
    }

    private boolean verifyInput(){
        phonenumberStr = phonenumber.getText().toString().trim();
        usernameStr = username.getText().toString().trim();
        passwordStr = password.getText().toString().trim();
        rePwdStr = repassword.getText().toString().trim();
        veriCodeStr = veriCode.getText().toString();


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

        if (passwordStr == null || usernameStr.equals("")) {
            password.setError("密码不能为空");
            return false;
        }
        if (rePwdStr == null || usernameStr.equals("")) {
            repassword.setError("请再次输入密码");
            return false;
        }

        if (!rePwdStr.equals(passwordStr)) {
            repassword.setError("两次输入密码不一致");
            return false;
        }

        if (veriCodeStr == null || usernameStr.equals("")) {
            veriCode.setError("请输入验证码");
            return false;
        }
        return true;
    }
}

