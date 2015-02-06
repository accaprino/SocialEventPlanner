package com.lazokin.socialeventplanner.activities;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.adapters.MainSpinnerAdapter;
import com.lazokin.socialeventplanner.database.SocialEventDatabaseModel;
import com.lazokin.socialeventplanner.fragments.AgendaFragment;
import com.lazokin.socialeventplanner.listeners.MainSpinnerListener;
import com.lazokin.socialeventplanner.services.EventNotificationService;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public final static int MINUTES_TO_MILLISECONDS = 60 * 1000;
	public static long notificationPeriod;
	
	private static PendingIntent notificationServicePendingIntent;

	private Fragment agendaFragment;
	private Fragment weeklyCalendarFragment;
	private SpinnerAdapter spinnerAdapter;
	
	private SharedPreferences sharedPref;
	private AlarmManager alarmManager;

	public static PendingIntent getNotificationServicePendingIntent() {
		return notificationServicePendingIntent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Create database
		SocialEventDatabaseModel.getInstance(this);

		// Check activity is using fragment version of layout
		if (findViewById(R.id.fragment_container) != null) {
			
			// Restored from a previous state
			if (savedInstanceState != null) {
				return;
			}
			
			// Create starting fragment
			if (agendaFragment == null) {
				agendaFragment = new AgendaFragment();
			}
			
			// Add fragment to layout
			getFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container, agendaFragment)
				.commit();
			
			// Get spinner options
			String[] spinnerOptions =
					getResources().getStringArray(R.array.action_list);
			
			// Create spinner adapter
			spinnerAdapter = new MainSpinnerAdapter(this,
					android.R.layout.simple_spinner_dropdown_item, spinnerOptions);
			
			// Configure action bar
			getActionBar().setDisplayShowTitleEnabled(false);
			getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			getActionBar().setListNavigationCallbacks(spinnerAdapter,
					new MainSpinnerListener(this, agendaFragment,
							weeklyCalendarFragment));
			
		}
		
		// Get a reference to the application shared preferences
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		// Get notification preferences
		notificationPeriod = Long.parseLong(
				sharedPref.getString("pref_key_notification_period", "1"))
				* MINUTES_TO_MILLISECONDS;
		
		// Setup Event Notification Service
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(this, EventNotificationService.class);
		notificationServicePendingIntent = PendingIntent.getService(this, 0, i, 0);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				notificationPeriod, notificationPeriod,
				notificationServicePendingIntent);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_agenda, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_new_event) {
			Intent intent = new Intent(this, NewEventActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_map) {
			Intent intent = new Intent(this, MapActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_help) {
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		notificationServicePendingIntent.cancel();
		am.cancel(notificationServicePendingIntent);
		Toast.makeText(this, "destroyed", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
	
	
	
}
