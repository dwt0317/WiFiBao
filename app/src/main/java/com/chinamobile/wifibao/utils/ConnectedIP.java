package com.chinamobile.wifibao.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
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

    public ArrayList<String> getConnectedIP(){
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
        int count=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    Log.i("ip", splitted[0]);
                    ++count;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count-1;
    }

}
