package com.example.mobilesafe.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {
	private LocationManager lm;
	private MyLocationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> names = lm.getAllProviders();
		if (names.size() > 0 && names.contains("gps")) {
			listener = new MyLocationListener();
			lm.requestLocationUpdates("gps", 0, 0, listener);
		}
		super.onCreate();
	}
	private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			String la = "la:"+location.getLatitude()+"\n";
			String lo = "lo:"+location.getLongitude();
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			String safenumber = sp.getString("safenumber", "");
			SmsManager.getDefault().sendTextMessage(safenumber, null, la+lo, null, null);
			lm.removeUpdates(listener);
			listener = null;
			//把自身的服务停止掉.
			stopSelf();
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}
}
