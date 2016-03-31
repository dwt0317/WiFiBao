package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.utils.WifiApAdmin;

/**
 * Created by cdd on 2016/3/16.
 */
public class CloseApActivity extends Activity{
    private Context mContext = null;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        //setContentView(R.layout.close_share);
        setContentView(R.layout.flow_share);

        mContext = this;
        //close wifi ap
        Button stopBt = (Button)findViewById(R.id.share_stop);
        stopBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //WifiApAdmin wifiAp = new WifiApAdmin(mContext);
                WifiApAdmin.closeWifiAp(mContext);
                /*//统计分享的流量
                RunningProcess rp = new RunningProcess();
                int uid = rp.getUidOfProcess(mContext, "system");
                Log.i("cdd:",String.valueOf(uid));
                long uidTxBytes = TrafficStats.getUidTxBytes(uid);
                double uidTxKBytes_d = uidTxBytes*1.0/1024;
                java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
                String sR = df.format(uidTxKBytes_d);
                //显示使用流量
                final TextView show=(TextView)findViewById(R.id.total);
                show.setText(sR+"KB");*/
                Intent intent = new Intent(CloseApActivity.this, BalanceShareActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.scale_in, R.anim.alpha_out);

            }
        });
        /*//show connected ips
        Button bt2 = (Button)findViewById(R.id.button2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectedIP cp = new ConnectedIP();
                ArrayList<String> connectedIP;
                connectedIP = cp.getConnectedIP();
                for(String s: connectedIP){
                    Log.i("cdd",s);
                }
                Toast.makeText(mContext,"已接入"+String.valueOf(connectedIP.size()-1)+"台设备",Toast.LENGTH_LONG).show();

            }
        });
        //show activity processes
        Button bt3 = (Button)findViewById(R.id.button3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunningProcess rp = new RunningProcess();
                rp.showActivityProcesses(mContext);

            }
        });*/
    }

        @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.alpha_out, R.anim.translate_out);
    }

}
