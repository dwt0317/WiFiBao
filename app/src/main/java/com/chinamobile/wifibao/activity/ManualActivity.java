package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.chinamobile.wifibao.R;

/**
 * 用户手册页面
 */
public class ManualActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual);

        ImageView home = (ImageView)findViewById(R.id.backhome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ManualActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
