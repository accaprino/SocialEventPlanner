package com.lazokin.socialeventplanner.asynctasks;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.database.SocialEventDatabaseModel;
import com.lazokin.socialeventplanner.models.SocialEventModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class DeleteSocialEventTask extends AsyncTask<String, Void, DeletionType> {
	
	private Context context;
	private SharedPreferences sharedPref;
	private SocialEventModel eventModel;
	
	public DeleteSocialEventTask(Context context) {
		this.context = context;
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		this.eventModel = SocialEventDatabaseModel.getInstance(context);
	}

	@Override
	protected DeletionType doInBackground(String... params) {
		
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
		
		// Delete social events
		int deletionCount = 0;
		for (String id : params) {
			if (eventModel.removeEvent(id)) {
				deletionCount++;
			}
		}
		
		// Return result of deletion
		if (params.length > 1) {
			if (deletionCount > 0) {
				return DeletionType.EVENTS_DELETED;
			} else {
				return DeletionType.EVENTS_NOT_DELETED;
			}
		} else {
			if (deletionCount > 0) {
				return DeletionType.EVENT_DELETED;
			} else {
				return DeletionType.EVENT_NOT_DELETED;
			}
		}

	}

	@Override
	protected void onPostExecute(DeletionType deletionType) {
		
		// Notify observers of event model if data has changed
		if (eventModel.dataHasChanged()) {
			eventModel.notifyAllObservers();
		}
		
		switch (deletionType) {
			case EVENTS_DELETED:
			{
				// Make toast to indicate multiple social events deleted
				Toast toast = Toast.makeText(
						context, R.string.social_events_deleted, Toast.LENGTH_SHORT);
				toast.show();
				break;
			}
			case EVENTS_NOT_DELETED:
			{
				// Make toast to indicate multiple social events not deleted
				Toast toast = Toast.makeText(
						context, R.string.social_events_not_deleted, Toast.LENGTH_SHORT);
				toast.show();
				break;
			}
			case EVENT_DELETED:
			{
				// Make toast to indicate single social event deleted
				Toast toast = Toast.makeText(
						context, R.string.social_event_deleted, Toast.LENGTH_SHORT);
				toast.show();
				break;
			}
			case EVENT_NOT_DELETED:
			{
				// Make toast to indicate single social event not deleted
				Toast toast = Toast.makeText(
						context, R.string.social_event_not_deleted, Toast.LENGTH_SHORT);
				toast.show();
				break;
			}
		}
		
		
		
		super.onPostExecute(deletionType);
	}
	
	

}
