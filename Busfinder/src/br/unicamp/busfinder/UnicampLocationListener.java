package br.unicamp.busfinder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import br.unicamp.busfinder.R;

public class UnicampLocationListener implements LocationListener {

	private static final String TAG = "LocationListener:";
	private Context context;
	private MapView map;

	public UnicampLocationListener(Context context, MapView map) {
		this.context = context;
		this.map = map;
	}

	public void onLocationChanged(Location location) {

		GeoPoint gpoint = new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		Drawable d = context.getResources().getDrawable(R.drawable.ic_launcher);
		BusFinderActivity.myPoint = gpoint;
		
		//Overlay myPosition = new MyLocOverlay(gpoint, map);
		//map.getOverlays().remove(myPosition);
		//map.getOverlays().add(myPosition);

		MapController controller = map.getController();
		controller.animateTo(gpoint);

		Log.d(TAG, "LocationChanged to" + gpoint.getLatitudeE6() + "LAT "
				+ gpoint.getLongitudeE6() + "LON");

	}

	public void onProviderDisabled(String provider) {

	}

	public void onProviderEnabled(String provider) {

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public Context getContext() {
		return this.context;
	}

}
