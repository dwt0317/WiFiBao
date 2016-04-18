package com.chinamobile.wifibao.utils;

import com.chinamobile.wifibao.bean.WiFi;

/**
 * Created by cdd on 2016/4/18.
 */
public class WiFiApGradeUtil {

    public static double getGrade(WiFi wifi){
        int maxConnect = wifi.getMaxConnect();
        double maxShare = wifi.getUpperLimit();
        double grade = maxShare*1.5/maxConnect;
        return grade;
    }
}
