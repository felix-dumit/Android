package br.unicamp.busfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MyLocOverlay extends Overlay {

	long start, stop;
	private Paint paint = new Paint();
	private GeoPoint point;
	private MapView map;

	public MyLocOverlay(GeoPoint point, MapView mapView) {
		this.point = point;
		this.map = mapView;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Point p = map.getProjection().toPixels(point, null);
		Bitmap pin = BitmapFactory.decodeResource(map.getResources(),
				R.drawable.map_pin);

		int scale = 10;
		pin = Bitmap.createScaledBitmap(pin, pin.getWidth() / scale,
				pin.getHeight() / scale, false);

		Rect rectOverlay = new Rect(p.x - pin.getWidth() / 2, p.y
				- pin.getHeight(), p.x + pin.getWidth() / 2, p.y);
		canvas.drawBitmap(pin, null, rectOverlay, paint);

	}
}
