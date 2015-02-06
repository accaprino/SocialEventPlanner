package com.lazokin.socialeventplanner.asynctasks;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.database.SocialEventDatabaseModel;
import com.lazokin.socialeventplanner.models.SocialEvent;
import com.lazokin.socialeventplanner.models.SocialEventModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class AddSocialEventTask extends AsyncTask<SocialEvent, Void, Boolean> {
	
	private Context context;
	private SharedPreferences sharedPref;
	private SocialEventModel eventModel;
	
	public AddSocialEventTask(Context context) {
		this.context = context;
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		this.eventModel = SocialEventDatabaseModel.getInstance(context);
	}

	@Override
	protected Boolean doInBackground(SocialEvent... params) {
		
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

		// Add social event
		if (eventModel.addEvent(params[0])) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	protected void onPostExecute(Boolean eventAdded) {
		
		// Notify observers of event model if data has changed
		if (eventModel.dataHasChanged()) {
			eventModel.notifyAllObservers();
		}
		
		if (eventAdded) {
			// Make toast to indicate social event created
			Toast toast = Toast.makeText(
					context, R.string.social_event_created, Toast.LENGTH_SHORT);
			toast.show();
		} else {
			// Make toast to indicate social event not created
			Toast toast = Toast.makeText(
					context, R.string.social_event_not_created, Toast.LENGTH_SHORT);
			toast.show();
		}
		
		super.onPostExecute(eventAdded);
	}
	
	

}
