package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.chinamobile.wifibao.R;


/**
 * Created by lab on 2016/4/19.
 */
public class PasswordModifyActivity extends Activity {
    private Context mContext = null;



    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); 设置无标题
        setContentView(R.layout.passwordmodify);
        this.passwordModify();
    }

    private void passwordModify() {

    }
    private boolean validInput() {
        return true;
    }


}
