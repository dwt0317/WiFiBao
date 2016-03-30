package com.chinamobile.wifibao.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.chinamobile.wifibao.R;

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
       TextView textView = (TextView) findViewById(R.id.flowusedText);
        String flowused = bundle.getString("flowUsed");
        textView.setText(flowused);
//        textView.setText("111");

    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            Intent intent = new Intent(BalanceUseActivity.this, WifiListActivity.class);
            startActivity(intent);
        }
        return super.dispatchKeyEvent(event);
    }

}
