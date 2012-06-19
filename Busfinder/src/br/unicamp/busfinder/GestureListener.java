package br.unicamp.busfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayGestureDetector.OnOverlayGestureListener;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.ZoomEvent;

public class GestureListener implements OnOverlayGestureListener {

	private static final String TAG = "GestureListener";
	Context c;

	public GestureListener(Context c) {
		this.c = c;
	}

	public boolean onDoubleTap(MotionEvent arg0, ManagedOverlay arg1,
			GeoPoint arg2, ManagedOverlayItem arg3) {

		Log.d(TAG, "onDoubleTAp");

		return false;
	}

	public void onLongPress(MotionEvent arg0, ManagedOverlay arg1) {

		Log.d(TAG, "LongPress");

	}

	public void onLongPressFinished(MotionEvent event, ManagedOverlay mo,
			final GeoPoint gpoint, ManagedOverlayItem moi) {

		Log.d(TAG, "LongPressFinished");

	}

	public boolean onScrolled(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3, ManagedOverlay arg4) {

		Log.d(TAG, "OnScrolled");
		return false;
	}

	public boolean onSingleTap(MotionEvent arg0, ManagedOverlay arg1,
			GeoPoint arg2, ManagedOverlayItem arg3) {

		Log.d(TAG, "OnSingleTap");
		return false;
	}

	public boolean onZoom(ZoomEvent arg0, ManagedOverlay arg1) {
		Log.d(TAG, "onZoom");
		return false;
	}

}
