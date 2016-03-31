package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.bean.UseRecord;
import com.chinamobile.wifibao.bean.WiFi;
import com.chinamobile.wifibao.utils.TrafficMonitor;
import com.chinamobile.wifibao.utils.FlowUsingManager;

import java.text.DecimalFormat;
import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by apple on 2016/3/25.
 */
public class FlowUsingActivity extends Activity {
    private String flowUsed;
    private TextView flowusingText;
//    TextView timeuseText;
    private Chronometer chronometer;
    private TextView moneyuseText;
    private UseRecord useRecord;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setViewComponent();
    }
    private void setViewComponent() {
        setContentView(R.layout.flow_use);
        //接收flowuse值
        flowusingText= (TextView) findViewById(R.id.flowusingText);
        //接收timeuse值
//        timeuseText = (TextView) findViewById(R.id.timeuseText);
        //计时器
        chronometer=(Chronometer)findViewById(R.id.chronometer);
        //接收cost值
        moneyuseText= (TextView) findViewById(R.id.moneyuseText);
        //新页面接收数据
        final WiFi wifi = (WiFi)this.getIntent().getSerializableExtra(WifiDetailsActivity.wifiDetailSER_KEY);

        Handler uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //更新流量以及已消费金额
                if(msg.what == 1){
                    flowUsed= TrafficMonitor.getInstance(FlowUsingActivity.this).getTotalTrafficStr();
                    flowusingText.setText(flowUsed);
                    moneyuseText.setText(computeCost(flowUsed));
                    TrafficMonitor.getInstance(FlowUsingActivity.this).refreshTraffic();
                }else{
//                    TrafficMonitor.getInstance(FlowUsingActivity.this).disableTrafficMonitor();
                }
            }
        };
        TrafficMonitor.getInstance(FlowUsingActivity.this).setUiHandler(uiHandler);
        TrafficMonitor.getInstance(FlowUsingActivity.this).startTrafficMonitor();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        useRecord=new UseRecord();
        useRecord.setWiFi(wifi);
        useRecord.setStartTime(new BmobDate(new Date()));



        Button button = (Button)findViewById(R.id.use_stop);//断开连接
        button.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
                TrafficMonitor.getInstance(FlowUsingActivity.this).disableTrafficMonitor();
                flowUsed= TrafficMonitor.getInstance(FlowUsingActivity.this).getTotalTrafficStr();

                Intent intent = new Intent(FlowUsingActivity.this, BalanceUseActivity.class);
                Bundle bundle=new Bundle();
                //传递参数
                bundle.putString("flowUsed", flowUsed);
                bundle.putString("cost", computeCost(flowUsed));
                intent.putExtras(bundle);
                useRecord.setEndTime(new BmobDate(new Date()));
                double cost=Double.parseDouble(computeCost(flowUsed));
                useRecord.setCost(cost);
                useRecord.setFlowUsed(Double.parseDouble(flowUsed));

                FlowUsingManager.getInstance(FlowUsingActivity.this).disconnect(wifi,useRecord);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();    //设置back键不可用
    }

    private String computeCost(String flowUsed){
        double cost=0.0;
        cost = 0.2 * Double.parseDouble(flowUsed);
        DecimalFormat df  = new DecimalFormat("######0.00");
        return String.valueOf(df.format(cost));
    }
}