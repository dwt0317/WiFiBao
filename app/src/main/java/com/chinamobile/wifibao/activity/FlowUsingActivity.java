package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chinamobile.wifibao.R;

/**
 * Created by apple on 2016/3/25.
 */
public class FlowUsingActivity extends Activity {
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setViewComponent();
    }
    private void setViewComponent() {
        setContentView(R.layout.flow_use);

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();

        //接收flowuse值
       TextView textView = (TextView) findViewById(R.id.flowusingText);
       String flowusing = bundle.getString("flowUsing");
       textView.setText(flowusing);
//        textView.setText("1111");

        Button button = (Button)findViewById(R.id.use_stop);//获取按钮资源
        button.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
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