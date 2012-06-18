package br.unicamp.busfinder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class BusFinderActivity extends MapActivity implements
		OnSharedPreferenceChangeListener, OnClickListener {

	private static final int CENTER_LATITUDE = (int) (-22.8177 * 1E6);
	private static final int CENTER_LONGITUDE = (int) (-47.0683 * 1E6);
	public static GeoPoint myPoint = new GeoPoint(CENTER_LATITUDE,
			CENTER_LONGITUDE);
	private static final String TAG = "busFinder";

	public static MapView map;
	private MapController controller;
	private Overlay myPosition;
	private GeoPoint gpoint;

	private static LocationManager lm;
	UnicampLocationListener locationListener;
	String towers;
	Drawable d;
	static BusPoints busPoints;
	static FavoritePoints favorites;

	private SharedPreferences prefs;
	ImageButton button;
	AutoCompleteTextView acTextView;
	boolean isFavorite = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//bundle = savedInstanceState;
		//if(bundle==null)bundle = new Bundle();

		try{
		Log.d(TAG, "OnCreate:"+ savedInstanceState.toString());
		}catch(Exception e){
			e.printStackTrace();
		}

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		prefs.registerOnSharedPreferenceChangeListener(this);

		map = (MapView) findViewById(R.id.map);
		map.setBuiltInZoomControls(true);
		map.setSatellite(false);
		map.setTraffic(false);
		map.setStreetView(false);
		map.setStreetView(false);

		busPoints = new BusPoints(getResources()
				.getDrawable(R.drawable.busstop), this);
		map.getOverlays().add(busPoints);

		loadbusStops(busPoints);

		favorites = new FavoritePoints(getResources().getDrawable(
				R.drawable.favorites), this);
		map.getOverlays().add(favorites);

		loadPrefs();
		map.invalidate();

		controller = map.getController();

		

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		
		locationListener = new UnicampLocationListener(this, map);

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locationListener);
		
		gpoint = getCurrentPosition(this);
		myPosition = new MyLocOverlay(gpoint, map);
		map.getOverlays().add(myPosition);

		controller.setCenter(gpoint);
		controller.animateTo(gpoint);
		controller.setZoom(16);

		TouchOverlay t = new TouchOverlay(this);
		map.getOverlays().add(t);

		

		
		

		acTextView = (AutoCompleteTextView) findViewById(R.id.fav_search);
		acTextView.setAdapter(favorites.getAdapter());
		// textView.setAdapter(busPoints.getAdapter());

		button = (ImageButton) findViewById(R.id.imageButton1);
		button.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onDestroy");
		super.onDestroy();


	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		lm.removeUpdates(locationListener);
		
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locationListener);
		}

	@Override
	protected void onStop() {
		Log.d(TAG,"onStop");
		super.onStop();	
	}
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "onRestoreBundle:"+savedInstanceState.toString());
		super.onRestoreInstanceState(savedInstanceState);
		
		ArrayList<PItem> favs = savedInstanceState
				.getParcelableArrayList("favs");

		if (favorites == null) {
			favorites = new FavoritePoints(getResources().getDrawable(
					R.drawable.favorites), this);
			map.getOverlays().add(favorites);
		}

		favorites.setPinpoints(favs);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveBundle:"+outState.toString());
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("favs", favorites.getPinpoints());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		Log.d(TAG, "CreateMenu");
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPrefs:

			Log.d(TAG, "PrefsMENU");

			startActivity(new Intent(this, PrefsActivity.class));
			break;
		case R.id.itemKML:

			Log.d(TAG, "KML");

			loadbusStops(busPoints);

			break;
		case R.id.itemLocation:

			GeoPoint gpoint = getCurrentPosition(this);
			myPosition = new MyLocOverlay(gpoint, map);
			map.getOverlays().add(myPosition);

			break;
		}
		Log.d(TAG, "Menu Selected: " + item.getItemId());
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs,
			String key) {

		Log.d(TAG, "Prefs Changed: " + key);
		this.loadPrefs();

	}

	private void loadPrefs() {
		map.setStreetView((prefs.getBoolean("streetView", false)));
		map.setSatellite(prefs.getBoolean("satelliteView", false));
	}

	public static GeoPoint getCurrentPosition(Context c) {
		GeoPoint src;
		//LocationManager locationMgr = (LocationManager) c
		//		.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		String towers = lm.getBestProvider(criteria, false);

		Location loc = lm.getLastKnownLocation(towers);

		if (loc != null) {
			src = new GeoPoint((int) (loc.getLatitude() * 1E6),
					(int) (loc.getLongitude() * 1E6));

		} else {
			Toast.makeText(c, "no provider found", Toast.LENGTH_SHORT).show();
			src = myPoint;
		}

		return src;
	}

	public static float GeoDistance(GeoPoint p1, GeoPoint p2) {

		Location l1, l2;
		l1 = new Location("");
		l2 = new Location("");
		l1.setLatitude(p1.getLatitudeE6() / 1E6);
		l1.setLongitude(p1.getLongitudeE6() / 1E6);
		l2.setLatitude(p2.getLatitudeE6() / 1E6);
		l2.setLongitude(p2.getLongitudeE6() / 1E6);

		return l1.distanceTo(l2);

	}

	private String[] loadbusStops(ListPoints busStops) {

		Log.d(TAG, "GETDIRECTIONDATA");
		Document doc = null;

		InputStream fis = null;

		try {
			fis = getAssets().open("bus_stops.kml");
		} catch (IOException e) {
			Log.d(TAG, "FILENOTFOUND");
			e.printStackTrace();
		}

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(fis);
			if (doc == null)
				Log.d(TAG, "DOC NULL");
			Log.d("TEXTCONTEXT:", doc.getElementsByTagName("Coordinates")
					.toString());

			NodeList nplace = doc.getElementsByTagName("Placemark");
			int j;

			for (int i = 0; i < nplace.getLength(); i++) {
				NodeList kids = nplace.item(i).getChildNodes();
				GeoPoint gpoint = null;
				String stopName = null;

				// Log.d("Child Size", "LEN : " + kids.getLength());
				for (j = 0; j < kids.getLength(); j++) {
					// Log.d("NAME", "NAME : " + kids.item(j).getNodeName());
					if (kids.item(j).getNodeName().contains("Point")) {
						String[] pairs = kids.item(j).getTextContent().trim()
								.split(",");
						Log.d("Point", "LAT:" + pairs[0] + "LON:" + pairs[1]);
						gpoint = new GeoPoint(
								(int) (Double.parseDouble(pairs[1]) * 1E6),
								(int) (Double.parseDouble(pairs[0]) * 1E6));
					} else if (kids.item(j).getNodeName().contains("name")) {
						stopName = kids.item(j).getTextContent().trim();
						Log.d("Point Name", "BUSSTOP NAME : " + stopName);
					}
				}
				if (gpoint == null || stopName == null)
					continue;

				busStops.insertPinpoint(new PItem(gpoint, stopName, "snippet"));

			}

			/*
			 * NodeList ncoords = doc.getElementsByTagName("coordinates");
			 * 
			 * for (int i = 0; i < ncoords.getLength(); i++) {
			 * 
			 * String[] pairs = ncoords.item(i).getTextContent().split(",");
			 * Log.d("Point", "LAT:" + pairs[0] + "LON:" + pairs[1]);
			 * 
			 * GeoPoint gpoint = new GeoPoint( (int)
			 * (Double.parseDouble(pairs[1]) * 1E6), (int)
			 * (Double.parseDouble(pairs[0]) * 1E6));
			 * 
			 * busStops.insertPinpoint(new OverlayItem(gpoint, "point:" + i,
			 * "snippet"));
			 * 
			 * }
			 */

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "ERROROR:" + e);
		}

		if (doc == null)
			Log.d(TAG, "DOC NULLL");

		return null;

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageButton1:

			if (!isFavorite) {
				button.setImageResource(R.drawable.favorites);
				acTextView.setAdapter(favorites.getAdapter());
				acTextView.setHint(R.string.searchFavoritesHint);
				acTextView.setText("");
				isFavorite = true;
			} else if (isFavorite) {
				button.setImageResource(R.drawable.busstop);
				// button.setText(R.string.busStopSearch);
				acTextView.setAdapter(busPoints.getAdapter());
				acTextView.setHint(R.string.searchBusStopsHint);
				acTextView.setText("");
				isFavorite = false;
			}
			Log.d(TAG, "onClickImageButton!");

			break;
		default:
			break;
		}

	}

}
