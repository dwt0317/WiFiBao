package com.chinamobile.wifibao.activity;

import android.app.Activity;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.wifibao.R;
import com.chinamobile.wifibao.utils.ConnectedIP;
import com.chinamobile.wifibao.utils.RunningProcess;
import com.chinamobile.wifibao.utils.WifiApAdmin;

import java.util.ArrayList;

/**
 * Created by cdd on 2016/3/16.
 */
public class SharelHot extends Activity{
    private Context mContext = null;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.sharehot);

        mContext = this;
        //close wifi ap
        Button bt1 = (Button)findViewById(R.id.button1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiApAdmin wifiAp = new WifiApAdmin(mContext);
                wifiAp.closeWifiAp(mContext);
                //统计分享的流量
                RunningProcess rp = new RunningProcess();
                int uid = rp.getUidOfProcess(mContext, "system");
                Log.i("cdd:",String.valueOf(uid));
                long uidTxBytes = TrafficStats.getUidTxBytes(uid);
                double uidTxKBytes_d = uidTxBytes*1.0/1024;
                java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
                String sR = df.format(uidTxKBytes_d);
                //显示使用流量
                final TextView show=(TextView)findViewById(R.id.total);
                show.setText(sR+"KB");

            }
        });
        //show connected ips
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
        });
    }

}
