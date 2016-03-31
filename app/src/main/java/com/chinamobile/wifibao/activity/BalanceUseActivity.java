package com.chinamobile.wifibao.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;

import java.text.DecimalFormat;

/**
 * Created by apple on 2016/3/25.
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
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}
