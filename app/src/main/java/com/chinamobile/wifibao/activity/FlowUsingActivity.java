package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.TrafficMonitor;
import com.chinamobile.wifibao.utils.UseManager;

/**
 * Created by apple on 2016/3/25.
 */
public class FlowUsingActivity extends Activity {
    private String flowUsed;
    TextView flowusingText;
    TextView timeuseText;
    TextView moneyuseText;
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setViewComponent();
    }
    private void setViewComponent() {
        setContentView(R.layout.flow_use);
        //接收flowuse值
        flowusingText= (TextView) findViewById(R.id.flowusingText);
        //接收timeuse值
        timeuseText = (TextView) findViewById(R.id.timeuseText);
        //接收cost值
        moneyuseText= (TextView) findViewById(R.id.moneyuseText);


        Handler uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    flowUsed= TrafficMonitor.getInstance(FlowUsingActivity.this).getTotalTrafficStr();
                    flowusingText.setText(flowUsed);
                    TrafficMonitor.getInstance(FlowUsingActivity.this).refreshTraffic();
                }else{
//                    TrafficMonitor.getInstance(FlowUsingActivity.this).disableTrafficMonitor();
                }
            }
        };
        TrafficMonitor.getInstance(FlowUsingActivity.this).setUiHandler(uiHandler);
        TrafficMonitor.getInstance(FlowUsingActivity.this).startTrafficMonitor();
        //新页面接收数据
//        Bundle bundle = this.getIntent().getExtras();
//        WiFi wifi = (WiFi)this.getIntent().getSerializableExtra(WifiListActivity.SER_KEY);

        Button button = (Button)findViewById(R.id.use_stop);//获取按钮资源
        button.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
                TrafficMonitor.getInstance(FlowUsingActivity.this).disableTrafficMonitor();
                flowUsed= TrafficMonitor.getInstance(FlowUsingActivity.this).getTotalTrafficStr();
                Intent intent = new Intent(FlowUsingActivity.this, BalanceUseActivity.class);
                Bundle bundle=new Bundle();
                //传递参数
                bundle.putString("flowUsed","99" );
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();    //设置back键不可用
    }
}