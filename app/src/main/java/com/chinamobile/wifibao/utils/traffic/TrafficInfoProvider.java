package com.chinamobile.wifibao.utils.traffic;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cdd on 2016/3/27.
 */
public class TrafficInfoProvider {
    private static final String TAG = "TrafficInfoProvider";

    private static PackageManager pm;
    private Context context;

    private long oldTotalTraffic;
    private long oldAllAppTraffic;

    public TrafficInfoProvider(Context context) {
        this.context = context;
        pm = context.getPackageManager();
    }
    public void initOldTotalTraffic(long oldTotalTraffic){
        this.oldTotalTraffic = oldTotalTraffic;
    }
    private long getOldTotalTraffic(){
        return this.oldTotalTraffic;
    }
    public void initOldAllAppTraffic(long oldAllAppTraffic) {
        this.oldAllAppTraffic = oldAllAppTraffic;
    }
    private long getOldAllAppTraffic() {
        return oldAllAppTraffic;
    }

    /**
     * 返回所有的有互联网访问权限的应用程序信息。
     * 一定不能为static方法
     * @return
     */
    private List<TrafficInfo> getTrafficInfos() {
        //安装的应用程序信息
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        //存放具有Internet权限的应用信息
        List<TrafficInfo> trafficInfos = new ArrayList<TrafficInfo>();
        for (PackageInfo packinfo : packinfos) {
            //获取该应用的所有权限信息
            String[] permissions = packinfo.requestedPermissions;
            if (permissions != null && permissions.length > 0) {
                for (String permission : permissions) {
                    //筛选出具有Internet权限的应用程序
                    if ("android.permission.INTERNET".equals(permission)) {
                        //用于封装具有Internet权限的应用程序信息
                        TrafficInfo trafficInfo = new TrafficInfo();
                        //封装应用信息
                        trafficInfo.setPackName(packinfo.packageName);
                        //获取到应用的uid（user id）
                        int uid = packinfo.applicationInfo.uid;
                        trafficInfo.setUid(uid);
                        //TrafficStats对象通过应用的uid来获取应用的下载、上传流量信息，不区分wifi和手机流量
                        trafficInfo.setRx(TrafficStats.getUidRxBytes(uid));
                        trafficInfo.setTx(TrafficStats.getUidTxBytes(uid));
                        trafficInfos.add(trafficInfo);
                        break;
                    }
                }
            }
        }
        return trafficInfos;
    }
    /**
     * 手机使用的全部流量
     * @return
     */
    public long getTotalTraffic(){
        //不区分wifi和手机流量，使用的总流量
        long totalRx = TrafficStats.getTotalRxBytes();
        long totalTx = TrafficStats.getTotalTxBytes();
        return totalRx+totalTx;
    }
    /*
     * 安装的应用程序使用的全部流量
     * @return
     */
    public long getAllAppTraffic(){
        long allAppTraffic=0L;
        List<TrafficInfo> trafficInfos = getTrafficInfos();
        for(TrafficInfo trafficInfo:trafficInfos){
            allAppTraffic = allAppTraffic + trafficInfo.getRx()+ trafficInfo.getTx();
        }
        return allAppTraffic;
    }
    /**
     * wifiap使用的流量
     * @return
     */
    public long getWifiApTotalTraffic(){
        long appTraffic = getAllAppTraffic() - getOldAllAppTraffic();
        long totalTraffic = getTotalTraffic() - getOldTotalTraffic();
        long apTraffic = totalTraffic-appTraffic;
        return (apTraffic>0)?apTraffic:(-apTraffic);
    }
    /**
     * 安装的应用程序
     * 测试功能
     */
    public void showInstalledApp(){
        /*List<TrafficInfo> trafficInfos = getTrafficInfos();
        for(TrafficInfo trafficInfo:trafficInfos){
            Log.i("cdd:",trafficInfo.getPackName()+":"+trafficInfo.getUid());
        }*/
    }
}
