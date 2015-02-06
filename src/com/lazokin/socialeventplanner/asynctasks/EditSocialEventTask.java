package com.lazokin.socialeventplanner.asynctasks;

import java.util.Calendar;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.database.SocialEventDatabaseModel;
import com.lazokin.socialeventplanner.models.SocialEventModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class EditSocialEventTask extends AsyncTask<Void, Void, Boolean> {
	
	private Context context;
	private SharedPreferences sharedPref;
	private SocialEventModel eventModel;
	
	private String id;
	private String title;
	private Calendar startTime;
	private Calendar endTime;
	private String venue;
	private String location;
	private String notes;
	private String[] attendeesArray;
	
	
	public EditSocialEventTask(Context context, String id, String title,
			Calendar startTime, Calendar endTime, String venue,
			String location, String notes, String[] attendeesArray) {
		
		this.context = context;
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		this.eventModel = SocialEventDatabaseModel.getInstance(context);
		
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.venue = venue;
		this.location = location;
		this.notes = notes;
		this.attendeesArray = attendeesArray;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		
		// Get preferences
		boolean databaseDelay =
				sharedPref.getBoolean("pref_key_database_delay", false);
		int databaseDelayInSeconds = Integer.parseInt(
				sharedPref.getString("pref_key_database_delay_time", "0"));
		
		// Simulate delay
		if (databaseDelay) {
			try {
				Thread.sleep(databaseDelayInSeconds * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		
		// Edit social event
		return eventModel.editEvent(id, title, startTime, endTime,
				venue, location, notes, attendeesArray);

	}

	@Override
	protected void onPostExecute(Boolean eventAdded) {
		
		// Notify observers of event model if data has changed
		if (eventModel.dataHasChanged()) {
			eventModel.notifyAllObservers();
		}
		
		if (eventAdded) {
			// Make toast to indicate social event changed
			Toast toast = Toast.makeText(
					context, R.string.social_event_saved, Toast.LENGTH_SHORT);
			toast.show();
		} else {
			// Make toast to indicate social event not changed
			Toast toast = Toast.makeText(
					context, R.string.social_event_not_saved, Toast.LENGTH_SHORT);
			toast.show();
		}

		super.onPostExecute(eventAdded);
	}
	
	

}
