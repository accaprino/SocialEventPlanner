package com.lazokin.socialeventplanner.activities;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lazokin.socialeventplanner.util.MWMapUtils;
import com.lazokin.socialeventplanner.util.Util;

/**
 * This is the super class you will subclass. It will force you to implement the 2 key methods.
 * 
 *  - What is my action when you click a window?
 *  - How does my window look ? 
 *  
 *  Everything else to do with getting markers on the map is handled for you, as long as you use addMapItem();
 *  
 *  This Activity takes a generic argument, you must specify your model type as the generic type when subclassing.
 * 
 * @author Matt Witherow
 * 
 * @modified Nik Ambukovski
 *
 * @param <T>
 */
public abstract class AbstractMapActivity<T> extends Activity implements
		OnInfoWindowClickListener {

	private static final String TAG = AbstractMapActivity.class.getName();

	private Map<Marker, T> mMarkerMap;
	private GoogleMap mMap;

	public abstract InfoWindowAdapter getInfoWindowAdapter();

	public abstract void onMapInfoWindowClicked(Marker marker);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(com.lazokin.socialeventplanner.R.layout.activity_map);
		mMap = getMapFromFragment();

		if (mMap != null) {
			MWMapUtils.resetMapCamera(mMap);
			mMarkerMap = new HashMap<Marker, T>();
			mMap.setInfoWindowAdapter(getInfoWindowAdapter());
			mMap.setOnInfoWindowClickListener(this);
		} else {
			Util.toast(this, "Could not find map");
		}
	}

	/**
	 * Return an instantiated GoogleMap from the flat fragment xml.
	 * 
	 * @return {@link GoogleMap}
	 */
	private GoogleMap getMapFromFragment() {
		MapFragment f = (MapFragment) getFragmentManager().findFragmentById(
				com.lazokin.socialeventplanner.R.id.map);
		f.setHasOptionsMenu(true);
		getFragmentManager().executePendingTransactions();
		return MWMapUtils.getMapFromMapFragment(f);
	}

	/**
	 * Exposes the map to others
	 * 
	 * @return {@link GoogleMap}
	 */
	public GoogleMap getMap() {
		return mMap;
	}

	/**
	 * Hide/ show all markers held in the MarkerMap
	 */
	public void toggleMarkers() {
		for (Marker marker : mMarkerMap.keySet()) {
			marker.setVisible(!marker.isVisible());
		}
	}

	public void fitZoomForMarkers() {
		MWMapUtils.fitZoomForMarkers(getMap(), getModel());
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		this.onMapInfoWindowClicked(marker);
	}

	public void setModel(HashMap<Marker, T> model) {
		if (mMarkerMap == null) {
			Log.e(TAG, "Error: Setting model for map before map is ready");
			return;
		}
		mMarkerMap = model;
	}

	public Map<Marker, T> getModel() {
		return mMarkerMap;
	}

	public void addMapItem(MarkerOptions m, T event) {

		if (mMarkerMap == null || mMap == null) {
			Log.e(TAG, "Error: Setting model for map before map is ready");
			return;
		}
		mMarkerMap.put(mMap.addMarker(m), event);
	}

}
