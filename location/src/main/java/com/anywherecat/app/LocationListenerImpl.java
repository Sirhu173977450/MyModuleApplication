package com.anywherecat.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


public class LocationListenerImpl implements LocationListener {

    private static LocationListenerImpl locationListenerImpl;
    private LocationManager locationManager;
    private Location location;
    private Context context;


    @SuppressLint("WrongConstant")
    private LocationListenerImpl(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(SocializeConstants.KEY_LOCATION);
    }

    public static LocationListenerImpl a(Context context) {
        if (locationListenerImpl == null) {
            locationListenerImpl = new LocationListenerImpl(context);
        }
        return locationListenerImpl;
    }

    public Location a() {
        if (!d()) {
            return null;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        this.locationManager.requestLocationUpdates("gps", 1000, 10.0f, this);
        this.location = this.locationManager.getLastKnownLocation("gps");
        return this.location;
    }


    public boolean c() {
        if (!d()) {
            Toast.makeText(this.context, "GPS没有开启，请在设置中开启再试！", Toast.LENGTH_LONG).show();
            return true;
        } else if (f()) {
            return false;
        } else {
            Toast.makeText(this.context, "模拟GPS没有开启，请在设置中开启再试！", Toast.LENGTH_LONG).show();
            return true;
        }
    }


    public boolean d() {
        boolean z = true;
        if (!this.locationManager.getAllProviders().contains("gps")) {
            Toast.makeText(this.context, "您的机器不支持GPS,无法使用本软件！", Toast.LENGTH_LONG).show();
            return true;
        } else if (Build.VERSION.SDK_INT < 28) {
            return this.locationManager.isProviderEnabled("gps");
        } else {
            if (!this.locationManager.isProviderEnabled("gps")) {
                if (!this.locationManager.isProviderEnabled("network")) {
                    z = false;
                }
            }
            return z;
        }
    }



//    public void removeUpdates_b() {
    public void b() {
        this.locationManager.removeUpdates(this);
    }


    public boolean e() {
        if (Build.VERSION.SDK_INT >= 28) {
            return false;
        }
        return this.locationManager.isProviderEnabled("network");
    }


    public boolean f() {
        boolean z = true;
        if (Build.VERSION.SDK_INT >= 23) {
            return true;
        }
        if (Settings.Secure.getInt(this.context.getContentResolver(), "mock_location", 0) == 0) {
            z = false;
        }
        return z;
    }



    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
