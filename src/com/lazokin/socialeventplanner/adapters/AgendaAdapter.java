package com.lazokin.socialeventplanner.adapters;

import java.util.Calendar;
import java.util.List;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.models.SocialEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AgendaAdapter extends ArrayAdapter<SocialEvent> {
	
	private Context context;

	public AgendaAdapter(Context context, int resource, List<SocialEvent> socialEventList) {
		super(context, resource, socialEventList);
		this.context = context;
	}
	
	private static class ViewHolder {
		TextView eventListTitle;
		TextView eventListDate;
		TextView eventListTime;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Get social event at position
		SocialEvent socialEvent = getItem(position);
		
		// Inflate a new view when an old view is not being converted
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_agenda, parent, false);
			
			// Create a holder for the view
			ViewHolder holder = new ViewHolder();
			
			// Populate the holder
			holder.eventListTitle = (TextView) convertView.findViewById(R.id.eventListTitle);
			holder.eventListDate = (TextView) convertView.findViewById(R.id.eventListDate);
			holder.eventListTime = (TextView) convertView.findViewById(R.id.eventListTime);
			
			// Save the holder in the view
			convertView.setTag(holder);
		}
		
		// Get holder of views
		ViewHolder holder = (ViewHolder) convertView.getTag();
		
		// Populate list item contents
		holder.eventListTitle.setText(socialEvent.getTitle());
		if (socialEvent.getStartTime().get(Calendar.DAY_OF_YEAR) !=
				socialEvent.getEndTime().get(Calendar.DAY_OF_YEAR)) {
			holder.eventListDate.setText(socialEvent.getStartDateString() + 
					" - " +
					socialEvent.getEndDateString());
		} else {
			holder.eventListDate.setText(socialEvent.getStartDateString());
		}
		
		holder.eventListTime.setText(
				socialEvent.getStartTimeString() +
				" - " +
				socialEvent.getEndTimeString()
				);

		return convertView;
	}
	
}
