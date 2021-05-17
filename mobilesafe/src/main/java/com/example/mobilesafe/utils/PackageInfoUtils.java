package com.example.mobilesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2017/6/10.
 */

public class PackageInfoUtils {
    /**
     * 获取应用程序apk包的版本信息
     *
     * @param context
     *            上下文
     * @return
     */
    public static String getPackageVersion(Context context) {
        try {
            PackageInfo packinfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "解析版本号失败";
        }
    }
}
