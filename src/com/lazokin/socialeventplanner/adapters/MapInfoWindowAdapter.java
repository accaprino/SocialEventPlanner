package com.lazokin.socialeventplanner.adapters;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.models.SocialEvent;

/**
 * Window Adapters are just like normal adapters, instead of describing how a list item looks
 * we are describing how a window for a map marker will look. 
 * I have provided a basic layout.
 * 
 * There is a slight difference between the two methods required for InfoWindowAdapter's but I didn't 
 * care for this simple purpose.
 * 
 * @author Matt Witherow
 * 
 * @modified Nik Ambukovski
 *
 */
public class MapInfoWindowAdapter implements InfoWindowAdapter {

	private Context mContext;
	private Map<Marker, SocialEvent> mModel;

	public MapInfoWindowAdapter(Context context, Map<Marker, SocialEvent> map) {
		mContext = context;
		mModel = map;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return getWindowFor(marker);
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return getWindowFor(marker);
	}

	/**
	 * Returns the view to be shown for the popup window of a map marker
	 * 
	 * @param marker
	 * @return view
	 */
	private View getWindowFor(Marker marker) {

		LayoutInflater inflater = LayoutInflater.from(mContext);

		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.layout_maps_info_window, null);
		SocialEvent event = mModel.get(marker);

		TextView title = (TextView) view.findViewById(R.id.map_info_window_title);
		TextView venue = (TextView) view.findViewById(R.id.map_info_window_venue);
		TextView date = (TextView) view.findViewById(R.id.map_info_window_date);
		TextView time = (TextView) view.findViewById(R.id.map_info_window_time);

		title.setText(event.getTitle());
		if (event.getVenue().isEmpty()) {
			venue.setVisibility(View.GONE);
		} else {
			venue.setText(event.getVenue());
		}
		date.setText(event.getStartDateString());
		time.setText(event.getStartTimeString());
		
		return view;
	}

}