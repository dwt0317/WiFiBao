package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.chinamobile.wifibao.R;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by lab on 2016/4/19.
 */
public class WelcomeActivity extends Activity {
    private final int SKIP_DELAY_TIME = 2000;


    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); 设置无标题
        setContentView(R.layout.welcome);

        Timer time = new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                Intent intent=new Intent(WelcomeActivity.this,Home2Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        };
        time.schedule(task, SKIP_DELAY_TIME);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            System.exit(0);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
