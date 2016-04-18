package com.chinamobile.wifibao.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by cdd on 2016/3/20.
 */
public class ConnectedIP {
    private ConnectedIP(){}
    private static ConnectedIP cp;
    public static  ConnectedIP getInstance(){
        if(cp ==null)
            cp = new ConnectedIP();
        return cp;
    }

    private ArrayList<String> getConnectedIP(){
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }

    public int getConnectedIpCount(){
        int n=0;
        ArrayList<String> connectedIP = getConnectedIP();
        if(connectedIP==null ||connectedIP.size()==1)
            return 0;
        for(int i=1; i<connectedIP.size(); ++i){
            if(pingIpAddr(connectedIP.get(i))) {
                ++n;
            }
        }
        return n;
    }

    private boolean pingIpAddr(String ipAddress) {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 1 " + ipAddress);
            int status = p.waitFor();
            if (status == 0) {
                return true;
            }
        } catch (IOException e) {

        } catch (InterruptedException e) {

        }
        return false;
    }

}
