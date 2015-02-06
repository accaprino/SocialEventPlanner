package com.lazokin.socialeventplanner.activities;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.adapters.MapInfoWindowAdapter;
import com.lazokin.socialeventplanner.models.SocialEvent;
import com.lazokin.socialeventplanner.models.SocialEventMemoryModel;
import com.lazokin.socialeventplanner.models.SocialEventModel;

/**
 * A high level way to interact with Google Maps. Make sure to add your API key
 * to your manifest with all appropriate permissions. (make sure your package path is
 * correct too!)
 * 
 * You should be creating your own activity that subclasses RMITMapActivty<YOUR
 * MARKER MODEL>
 * 
 * The RMITMapActivity holds a local java.util.map of markers & models. Each marker is 'backed' by that model.
 * Use the addMapItem(Marker, Model) to add to this java.util.map and have them available on your GoogleMap.
 * 
 * Here is an example activity of a map that displays basic "events". 
 * This class is an example of what you should do.
 * 
 * Additionally, to show how to interact with the map, 
 * you can hide/show the markers. See details (click a marker). Do an action
 * (click an info window) & animate a zoom at any time to fit all markers in
 * frame.
 * 
 * GPS is enabled by default. You will see your own location.
 * 
 * @author Matt Witherow
 * 
 * @modified Nik Ambukovski
 * 
 */
public class MapActivity extends AbstractMapActivity<SocialEvent> {

	private SocialEventModel eventModel = SocialEventMemoryModel.getInstance();
	
	@Override
	protected void onResume() {
		super.onResume();
		
		
		// Get current calendar
		Calendar nowCalendar = Calendar.getInstance();
		
		// Get future events
		List<SocialEvent> futureEvents = eventModel.getFutureEvents(nowCalendar);
		
		
		// Loop through future events and post to map
		MarkerOptions marker = null;
		for (SocialEvent event : futureEvents) {
			
			// Skip event if no location data available
			if (event.getLocation().isEmpty())
				continue;
			
			// Get latitude and longitude of event
			String[] latlng = event.getLocation().split(",");
			
			// Convert latitude and longitude to numbers
			// Skip event if numbers not formatted correctly
			double lat;
			double lng;
			try {
				lat = Double.parseDouble(latlng[0]);
				lng = Double.parseDouble(latlng[1]);
			} catch (NumberFormatException e) {
				continue;
			}
			
			// Create a marker
			marker = new MarkerOptions();
			marker.position(new LatLng(lat, lng));
			
			// Add marker and event to map
			addMapItem(marker, event);
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//examples of how to play with the map camera or its markers.
		int id = item.getItemId();
		// show or hide all the markers on a map.
		if (id == R.id.action_toggle_markers) {
			toggleMarkers();
			return true;
		}
		// fix your zoom to neatly fit all the markers
		if (id == R.id.action_fit_zoom) {
			fitZoomForMarkers();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public InfoWindowAdapter getInfoWindowAdapter() {
		return new MapInfoWindowAdapter(this, getModel());
	}

	// Launch view event window for selected event
	@Override
	public void onMapInfoWindowClicked(Marker marker) {
		
		Map<Marker, SocialEvent> mMarkerMap = getModel();
		SocialEvent event = mMarkerMap.get(marker);
		
		Intent intent = new Intent(this, ViewEventActivity.class);
		intent.putExtra(ViewEventActivity.EXTRA_SELECTED_EVENT_ID, event.getId());
		startActivity(intent);
	}

}
