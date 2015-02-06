package com.lazokin.socialeventplanner.activities;

import java.util.Observable;
import java.util.Observer;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.asynctasks.DeleteSocialEventTask;
import com.lazokin.socialeventplanner.database.SocialEventDatabaseModel;
import com.lazokin.socialeventplanner.dialogs.DeleteEventDialogFragment;
import com.lazokin.socialeventplanner.models.SocialEvent;
import com.lazokin.socialeventplanner.models.SocialEventModel;
import com.lazokin.socialeventplanner.services.EventNotificationService;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewEventActivity extends Activity 
	implements DeleteEventDialogFragment.DeleteEventDialogListener, Observer {
	
	private SocialEventModel eventModel;
	
	public final static String EXTRA_SELECTED_EVENT_ID =
			"com.lazokin.socialeventplanner.display_activity_selected_event_id";
	
	private String eventId;
	private SocialEvent event;
	
	private TextView titleField;
	private TextView startDateField;
	private TextView startTimeField;
	private TextView endDateField;
	private TextView endTimeField;
	private TextView venueField;
	private TextView locationField;
	private TextView notesField;
	private TextView attendeesField;

	public ViewEventActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_event);
		
		// Get reference to database
		eventModel = SocialEventDatabaseModel.getInstance(this);
		
		// Register as observer
		eventModel.registerObserver(this);
		
		// Get view references
		titleField = (TextView)findViewById(
				R.id.social_event_display_field_title);
		startDateField = (TextView)findViewById(
				R.id.social_event_display_field_start_date);
		startTimeField = (TextView)findViewById(
				R.id.social_event_display_field_start_time);
		endDateField = (TextView)findViewById(
				R.id.social_event_display_field_end_date);
		endTimeField = (TextView)findViewById(
				R.id.social_event_display_field_end_time);
		venueField = (TextView)findViewById(
				R.id.social_event_display_field_venue);
		locationField = (TextView)findViewById(
				R.id.social_event_display_field_location);
		notesField = (TextView)findViewById(
				R.id.social_event_display_field_notes);
		attendeesField = (TextView)findViewById(
				R.id.social_event_display_field_attendees);
		
		// Get selected social event
		Intent intent = getIntent();
		eventId = intent.getStringExtra(EXTRA_SELECTED_EVENT_ID);
		event = eventModel.getEvent(eventId);
		
		// Dismiss notification if one exists
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(EventNotificationService.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(eventId, 0);

	}

	@Override
	protected void onStart() {
		super.onStart();
		updateViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_display_event, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_discard) {
			DeleteEventDialogFragment dialog = new DeleteEventDialogFragment();
			dialog.show(getFragmentManager(), "deleteEventDialog");
			return true;
		}
		if (id == R.id.action_edit) {
			Intent intent = new Intent(this, EditEventActivity.class);
			intent.putExtra(EXTRA_SELECTED_EVENT_ID, eventId);
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
	public void onDeleteEventDialogPositiveClick(DialogFragment dialog) {
		
		// Delete event via asynchronous task
		new DeleteSocialEventTask(this).execute(event.getId().toString());
		
		finish();
	}

	@Override
	public void onDeleteEventDialogNegativeClick(DialogFragment dialog) {
		// Do Nothing
	}
	
	private void updateViews() {
		titleField.setText(event.getTitle());
		startDateField.setText(event.getStartDateString());
		startTimeField.setText(event.getStartTimeString());
		endDateField.setText(event.getEndDateString());
		endTimeField.setText(event.getEndTimeString());
		venueField.setText(event.getVenue());
		locationField.setText(event.getLocation());
		notesField.setText(event.getNotes());
		attendeesField.setText("");
		if (event.getAttendees() != null) {
			for (int i = 0; i < event.getAttendees().length; i++) {
				attendeesField.append(event.getAttendees()[i]);
				if (i < event.getAttendees().length - 1) {
					attendeesField.append("\n");
				}
			}
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		updateViews();
	}

}
