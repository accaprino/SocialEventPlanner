package com.lazokin.socialeventplanner.activities;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.services.EventNotificationService;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GetLocationActivity extends Activity implements OnMapClickListener {

    private GoogleMap mMap;
    private String locationString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        // Setup map
        setUpMapIfNeeded();
        
        // Setup map on click listener
        mMap.setOnMapClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_get_location, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_get_location) {
			Intent data = new Intent();
			data.putExtra(InputEventActivity.EXTRA_LOCATION, locationString);
			setResult(Activity.RESULT_OK, data);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onMapClick(LatLng latlng) {
		double lat = Math.floor(latlng.latitude * 1E6) / 1E6;
		double lng = Math.floor(latlng.longitude * 1E6) / 1E6;
		locationString = lat + "," + lng;
		mMap.clear();
		mMap.addMarker(new MarkerOptions().position(latlng));
	}

	private void setUpMapIfNeeded() {
    	// Don't already have a map
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.get_location)).getMap();
            // Map found successfully
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
    	
    	// Get current location
    	String currentLocation = getCurrentLocation();
    	
    	// Center and zoom map around current location. If no location available
    	// center and zoom map around latitude = 0 and longitude = 0
    	if (currentLocation != null) {
    		String[] currentLocationArray = currentLocation.split(",");
    		double lat = Double.parseDouble(currentLocationArray[0]);
    		double lng = Double.parseDouble(currentLocationArray[1]);
    		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 10f);
    		mMap.moveCamera(cameraUpdate);
    	} else {
    		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(0,0), 10f);
    		mMap.moveCamera(cameraUpdate);
    	}
    }
    
	private String getCurrentLocation()
	{
		String result = null;
		Location location = null;
		
		LocationManager lm = (LocationManager)this.getSystemService(
				EventNotificationService.LOCATION_SERVICE);
		
		location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		
		if(location != null) {
		     double lng = location.getLongitude();
		     double lat = location.getLatitude();
		     result = String.valueOf(lat) + "," + String.valueOf(lng);
		}
		
		return result;
	}
}
