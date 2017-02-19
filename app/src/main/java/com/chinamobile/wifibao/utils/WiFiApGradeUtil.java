package com.chinamobile.wifibao.utils;

import com.chinamobile.wifibao.bean.WiFi;

/**
 * 计算热点评分
 */
public class WiFiApGradeUtil {

    public static double getGrade(WiFi wifi){
        int maxConnect = wifi.getMaxConnect();
        double maxShare = wifi.getUpperLimit();
        double grade = maxShare*1.5/maxConnect;
        return grade;
    }
}
