package com.chinamobile.wifibao.activity;
import android.app.Activity;
import android.os.Bundle;
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
}
