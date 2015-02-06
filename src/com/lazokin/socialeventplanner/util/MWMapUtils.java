package com.lazokin.socialeventplanner.util;

import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

/**
 * A collection of useful utility methods when interacting with the Android
 * Google Maps
 * 
 * @author Matt Witherow
 * 
 */
public class MWMapUtils {

	private static final String TAG = MWMapUtils.class.getName();
	private static final float DEFAULT_ZOOM = 8;
	public static final double AUS_LAT = -37.859676;
	public static final double AUS_LNG = 145.184326;
	public static final double RMIT_LAT = -37.8086141;
	public static final double RMIT_LNG = 144.963811;

	/**
	 * Create a defult map fragment centered on Australia.
	 * 
	 * @return {@link SupportMapFragment}
	 */
	public static SupportMapFragment createDefaultMapFragment() {

		GoogleMapOptions options = new GoogleMapOptions();
		options.compassEnabled(true);
		options.tiltGesturesEnabled(true);
		options.camera(CameraPosition.fromLatLngZoom(new LatLng(AUS_LAT,
				AUS_LNG), DEFAULT_ZOOM));

		return SupportMapFragment.newInstance(options);
	}

	/**
	 * reset the map to Australian default.
	 * 
	 * @param map
	 */
	public static void resetMapCamera(GoogleMap map) {
		LatLng latLng = new LatLng(AUS_LAT, AUS_LNG);
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		map.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
	}

	/**
	 * Extract the GoogleMap object from the MapFragment housing.
	 * 
	 * @param fragment
	 * @return
	 */
	public static GoogleMap getMapFromMapFragment(MapFragment fragment) {

		GoogleMap map = fragment.getMap();
		if (map == null) {
			Log.e(TAG, "No map obtained from the map fragment");
		} else {
			map.setMyLocationEnabled(true);
			UiSettings settings = map.getUiSettings();
			settings.setMyLocationButtonEnabled(true);
			settings.setTiltGesturesEnabled(true);
		}

		return map;
	}

	/**
	 * Find the minimum bounds that fence all the markers currently on the map,
	 * then animate the camera display to fit them within the map layout (with
	 * courtesy padding) This expects a Map of markers and their corresponding
	 * data.
	 * @param <T>
	 * 
	 * @param map
	 * @param markers
	 */
	public static <T> void fitZoomForMarkers(GoogleMap map,
			Map<Marker, T> markers) {
		if (map == null) {
			Log.e(TAG, "map is null, can't zoom for markers");
			return;
		}

		double minX = 180, maxX = -180, minY = 85, maxY = -85;
		for (Entry<Marker, T> m : markers.entrySet()) {
			double newY = (m.getKey().getPosition()).latitude;
			double newX = (m.getKey().getPosition()).longitude;
			minX = minX < newX ? minX : newX;
			maxX = maxX > newX ? maxX : newX;
			minY = minY < newY ? minY : newY;
			maxY = maxY > newY ? maxY : newY;
		}

		if (minX < maxX && minY < maxY) {
			LatLngBounds bounds = new LatLngBounds(new LatLng(minY, minX),
					new LatLng(maxY, maxX));
			try {
				map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
						75));
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	/**
	 * Zoom in on a single map marker (Zoom level shows roads-detail terrain).
	 * This action can be animated or immediate.
	 * 
	 * @param map
	 * @param marker
	 * @param animated
	 */
	public static void fitZoomOnMarker(GoogleMap map, Marker marker,
			boolean animated) {

		try {
			if (animated) {
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(
						marker.getPosition(), 11));
			} else {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(
						marker.getPosition(), 11));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		marker.hideInfoWindow();

	}

}
