package com.chinamobile.wifibao.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinamobile.wifibao.R;

import java.text.DecimalFormat;

/**
 * 热点使用结算页面
 */
public class BalanceUseActivity extends Activity {
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setViewComponent();
    }

    private void setViewComponent() {
        setContentView(R.layout.balance_use);

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();

        //接收flowuse值
        TextView flowusedtextView = (TextView) findViewById(R.id.flowusedText);
        String flowused = bundle.getString("flowUsed");
        flowusedtextView.setText(flowused);
        //接收cost值
        TextView costtextView = (TextView) findViewById(R.id.costText);
        String cost = bundle.getString("cost");
        costtextView.setText(cost);
        //设置节省流量费用的cost值
        double save = 0.0;
        save = 12 * Double.parseDouble(cost);
        DecimalFormat df  = new DecimalFormat("######0.00");
        TextView savetextView = (TextView) findViewById(R.id.saveText);
        savetextView.setText(String.valueOf(df.format(save)));
        //Button返回
        Button returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BalanceUseActivity.this,WifiListActivity.class);
                startActivity(intent);
            }
        });

        //返回HomeActivity
        ImageView home = (ImageView) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BalanceUseActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                && event.getAction() == KeyEvent.ACTION_DOWN
//                ) {
//            Intent intent = new Intent(BalanceUseActivity.this, HomeActivity.class);
//            startActivity(intent);
//        }
//        return super.dispatchKeyEvent(event);
//    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(BalanceUseActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}
