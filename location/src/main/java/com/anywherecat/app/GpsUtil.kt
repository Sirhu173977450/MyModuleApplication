package com.anywherecat.app

import android.app.AppOpsManager
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings

object GpsUtil {
    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    fun isOPen(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps || network
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    fun openGPS(context: Context?) {
        val GPSIntent = Intent()
        GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider")
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE")
        GPSIntent.data = Uri.parse("custom:3")
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send()
        } catch (e: CanceledException) {
            e.printStackTrace()
        }
    }

    /**
     * 打开开发者模式界面
     */
    fun startDevelopmentActivity(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val componentName = ComponentName("com.android.settings", "com.android.settings.DevelopmentSettings")
                val intent = Intent()
                intent.component = componentName
                intent.action = "android.intent.action.View"
                context.startActivity(intent)
            } catch (e1: Exception) {
                try {
                    val intent = Intent("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS") //部分小米手机采用这种方式跳转
                    context.startActivity(intent)
                } catch (e2: Exception) {
                }
            }
        }
    }


    fun isMockSettingsON(context: Context): Boolean {
        var isMockLocation = false
        isMockLocation = try {
            //if marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val opsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, Process.myUid(),
                        BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED
            } else {
                // in marshmallow this will always return true
                Settings.Secure.getString(context.contentResolver, "mock_location") != "0"
            }
        } catch (e: java.lang.Exception) {
            return isMockLocation
        }
        return isMockLocation
    }
}