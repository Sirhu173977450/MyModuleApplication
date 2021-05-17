package com.example.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2017/6/10.
 */

public class ServiceStatusUtils {

    /**
     * 判断某个服务是否处于运行状态.
     * @param context 上下文
     * @param serviceFullName 服务的全路径名称
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceFullName){
        //得到系统的进程管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //得到系统里面正在运行的服务
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(200);
        for(ActivityManager.RunningServiceInfo info:infos){
            if(serviceFullName.equals(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
