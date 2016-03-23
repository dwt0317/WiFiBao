package com.chinamobile.wifibao.utils;
/**
 * Created by cdd on 2016/3/21.
 */
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class RunningProcess {
	public void showActivityProcesses(Context context){
		ActivityManager mActivityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();
		for(ActivityManager.RunningAppProcessInfo rProcess:mRunningProcess){
			Log.e("Application", "PID:" + rProcess.pid + "(processName:" + rProcess.processName + ",UID:" + rProcess.uid + ")");
		}
	}

	public int getUidOfProcess(Context context,String pName){
		ActivityManager mActivityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();
		for(ActivityManager.RunningAppProcessInfo rProcess:mRunningProcess){
			if(pName.equalsIgnoreCase(rProcess.processName)){
				return rProcess.uid;
			}
		}
		return -1;
	}
}