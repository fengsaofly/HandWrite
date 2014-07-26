package scu.android.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MLocationManager {
	private static final int MINTIME = 5000;
	private static final int MINDISTANCE = 10;
	
	private static MLocationManager instance;
	private static LocationCallBack callback;
	
	private LocationManager locationManager;

	private MLocationManager(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MINTIME, MINDISTANCE, locationListener);
		}else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINTIME, MINDISTANCE,locationListener);
		}
	}

	public static MLocationManager getInstance(Context context,LocationCallBack mCallback) {
		if (null == instance) {
			instance = new MLocationManager(context);
		}
		callback=mCallback;
		return instance;
	}

	private final LocationListener locationListener = new LocationListener() {

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onLocationChanged(Location location) {
			callback.updateLocation(location);
		}
	};


	public interface LocationCallBack {
		void updateLocation(Location location);
	}

	public void destoryLocationManager() {
		locationManager.removeUpdates(locationListener);
	}
}
