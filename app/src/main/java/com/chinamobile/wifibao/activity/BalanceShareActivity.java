package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinamobile.wifibao.R;

/**
 * Created by cdd on 2016/3/24.
 */
public class BalanceShareActivity extends Activity {
    private ImageView home = null;
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.balance_share);
        //显示已分享流量
        Intent intent = getIntent();
        TextView tv = (TextView)findViewById(R.id.textview51);
        tv.setText(intent.getStringExtra("flow"));

        //返回HomeActivity
        home = (ImageView) findViewById(R.id.imageView8);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BalanceShareActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
    }

}
