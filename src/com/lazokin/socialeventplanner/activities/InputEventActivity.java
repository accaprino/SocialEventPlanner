package com.lazokin.socialeventplanner.activities;

import java.text.DateFormat;
import java.util.Calendar;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.dialogs.DatePickerDialogFragment;
import com.lazokin.socialeventplanner.dialogs.SaveEventDialogFragment;
import com.lazokin.socialeventplanner.dialogs.TimePickerDialogFragment;
import com.lazokin.socialeventplanner.models.SocialEventMemoryModel;
import com.lazokin.socialeventplanner.models.SocialEventModel;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public abstract class InputEventActivity extends Activity
	implements DatePickerDialogFragment.DatePickerDialogListener,
	TimePickerDialogFragment.TimePickerDialogListener,
	SaveEventDialogFragment.SaveEventDialogListener {
	
	static final int PICK_CONTACT_REQUEST = 1;
	static final int GET_LOCATION_REQUEST = 2;
	
	public final static String EXTRA_LOCATION =
			"com.lazokin.socialeventplanner.InputEventActivity.extra_location";
	
	protected SocialEventModel eventModel = SocialEventMemoryModel.getInstance();

	protected EditText titleField;
	protected EditText startDateField;
	protected EditText startTimeField;
	protected EditText endDateField;
	protected EditText endTimeField;
	protected EditText venueField;
	protected EditText locationField;
	protected EditText notesField;
	protected EditText attendeesField;
	
	protected Calendar startTimeOriginal;
	protected Calendar endTimeOriginal;
	protected Calendar startTimeModified;
	protected Calendar endTimeModified;
	
	protected String titleString;
	protected String startDateString;
	protected String startTimeString;
	protected String endDateString;
	protected String endTimeString;
	protected String venueString;
	protected String locationString;
	protected String notesString;
	protected String[] attendeesStringArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_event);

		// Get view controls
		titleField = (EditText)findViewById(
				R.id.social_event_input_field_title);
		startDateField = (EditText)findViewById(
				R.id.social_event_input_field_start_date);
		startTimeField = (EditText)findViewById(
				R.id.social_event_input_field_start_time);
		endDateField = (EditText)findViewById(
				R.id.social_event_input_field_end_date);
		endTimeField = (EditText)findViewById(
				R.id.social_event_input_field_end_time);
		venueField = (EditText)findViewById(
				R.id.social_event_input_field_venue);
		locationField = (EditText)findViewById(
				R.id.social_event_input_field_location);
		notesField = (EditText)findViewById(
				R.id.social_event_input_field_notes);
		attendeesField = (EditText)findViewById(
				R.id.social_event_input_field_attendees);
		
		// Set view listeners
		startDateField.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				showStartDatePickerDialog();
			}
		});
		startTimeField.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				showStartTimePickerDialog();
			}
		});
		endDateField.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				showEndDatePickerDialog();
			}
		});
		endTimeField.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				showEndTimePickerDialog();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_input_event, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_save) {
			if (getAndValidateInput()) {
				if (saveSocialEvent()) {
					finish();
				}
			}
			return true;
		}
		if (id == R.id.action_get_location) {
			Intent intent = new Intent(this, GetLocationActivity.class);
			startActivityForResult(intent, GET_LOCATION_REQUEST);
			return true;
		}
		if (id == R.id.action_add_person) {
			Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_CONTACT_REQUEST);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// Contact picker code from
		// http://code.tutsplus.com/tutorials/android-essentials-using-the-contact-picker--mobile-2017
		
		// Contact returned
		if (requestCode == PICK_CONTACT_REQUEST) {
			if (resultCode == RESULT_OK) {
				Uri dataUri = data.getData();
				String id = dataUri.getLastPathSegment();
				
				Cursor cursor = getContentResolver().query(
						Email.CONTENT_URI,
						null,
						Email.CONTACT_ID + "=?",
						new String[]{id},
						null
						);

				String email = null;
				if (cursor.moveToFirst()) {
					int emailColumnIndex = cursor.getColumnIndex(Email.DATA);
					email = cursor.getString(emailColumnIndex);
					
				}
				
				if (email == null) {
					Toast.makeText(this, "Attendee not added\nNo email address found",
							Toast.LENGTH_SHORT).show();
				} else {
					if (attendeesField.getText().toString().isEmpty()) {
						attendeesField.append(email);
					} else {
						attendeesField.append("\n" + email);
					}
					Toast.makeText(this, "Attendee added \n" + email,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		// Location returned
		if (requestCode == GET_LOCATION_REQUEST) {
			if (resultCode == RESULT_OK) {
				String location = data.getStringExtra(EXTRA_LOCATION);
				locationField.setText(location);
			}
		}
	}
	

	@Override
	public void onBackPressed() {
		if (changesMade()) {
			SaveEventDialogFragment dialog = new SaveEventDialogFragment();
			dialog.show(getFragmentManager(), "saveEventDialog");
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onSaveEventDialogPositiveClick(DialogFragment dialog) {
		if (getAndValidateInput()) {
			if (saveSocialEvent()) {
				finish();
			}
		}
	}
	
	@Override
	public void onSaveEventDialogNeutralClick(DialogFragment dialog) {
		// Do nothing on a canceled save
	}

	@Override
	public void onSaveEventDialogNegativeClick(DialogFragment dialog) {
		Toast.makeText(this, "Changes Discarded", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void onDateSet(DialogFragment dialog, Calendar calendar) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String dateString = df.format(calendar.getTime());
		if (calendar == startTimeModified) {
			startDateField.setText(dateString);
		} else {
			endDateField.setText(dateString);
		}
	}
	
	@Override
	public void onTimeSet(DialogFragment dialog, Calendar calendar) {
		DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
		String timeString = df.format(calendar.getTime());
		if (calendar == startTimeModified) {
			startTimeField.setText(timeString);
		} else {
			endTimeField.setText(timeString);
		}
	}
	
	public void showStartDatePickerDialog() {
		if (startTimeModified == null) {
			startTimeModified = Calendar.getInstance();
		}
		DialogFragment newFragment = new DatePickerDialogFragment(startTimeModified);
	    newFragment.show(getFragmentManager(), "startDatePicker");
	}
	
	public void showEndDatePickerDialog() {
		if (endTimeModified == null) {
			endTimeModified = Calendar.getInstance();
		}
		DialogFragment newFragment = new DatePickerDialogFragment(endTimeModified);
	    newFragment.show(getFragmentManager(), "endDatePicker");
	}
	
	public void showStartTimePickerDialog() {
		if (startTimeModified == null) {
			startTimeModified = Calendar.getInstance();
		}
		DialogFragment newFragment = new TimePickerDialogFragment(startTimeModified);
	    newFragment.show(getFragmentManager(), "startTimePicker");
	}
	
	public void showEndTimePickerDialog() {
		if (endTimeModified == null) {
			endTimeModified = Calendar.getInstance();
		}
		DialogFragment newFragment = new TimePickerDialogFragment(endTimeModified);
	    newFragment.show(getFragmentManager(), "endTimePicker");
	}
	
	// Check for valid input
	public boolean getAndValidateInput() {
		
		// Ensure title is entered
		titleString = titleField.getText().toString();
		if (titleString.isEmpty())
		{
			// Make toast to indicate title not entered
			Toast toast = Toast.makeText(
					this, R.string.title_not_entered, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		
		// Ensure start date is entered
		startDateString = startDateField.getText().toString();
		if (startDateString.isEmpty())
		{
			// Make toast to indicate date not entered
			Toast toast = Toast.makeText(
					this, R.string.start_date_not_entered, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		
		// Ensure start time is entered
		startTimeString = startTimeField.getText().toString();
		if (startTimeString.isEmpty())
		{
			// Make toast to indicate date not entered
			Toast toast = Toast.makeText(
					this, R.string.start_time_not_entered, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		
		// Ensure end date is entered
		endDateString = endDateField.getText().toString();
		if (endDateString.isEmpty())
		{
			// Make toast to indicate date not entered
			Toast toast = Toast.makeText(
					this, R.string.end_date_not_entered, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		
		// Ensure end time is entered
		endTimeString = endTimeField.getText().toString();
		if (endTimeString.isEmpty())
		{
			// Make toast to indicate date not entered
			Toast toast = Toast.makeText(
					this, R.string.end_time_not_entered, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		
		
		// Ensure end date is after start date
		if (startTimeModified.compareTo(endTimeModified) > 0) {
			// Make toast to indicate date not entered
			Toast toast = Toast.makeText(
					this, R.string.end_time_before_start_time, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		
		// Get remaining fields
		venueString = venueField.getText().toString();
		locationString = locationField.getText().toString();
		notesString = notesField.getText().toString();
		attendeesStringArray = attendeesField.getText().toString().split("\n");
		
		return true;
	}
	

	// Check for changes
	private boolean changesMade() {
		boolean result = false;
		
		// Check for title change
		boolean titleChanged = false;
		if (titleString == null && !titleField.getText().toString().isEmpty()) {
			titleChanged = true;
		} else if (titleString != null) {
			titleChanged = (titleString.compareTo(titleField.getText().toString()) == 0) ? false : true;
		}
		
		// Check for start time change
		boolean startTimeChanged = false;
		if (startTimeModified != null) {
			if (startTimeOriginal == null) {
				startTimeChanged = true;
			} else if (startTimeOriginal != null) {
				startTimeChanged = (startTimeOriginal.compareTo(startTimeModified) == 0) ? false : true; 
			}
		}
		
		// Check for end time change
		boolean endTimeChanged = false;
		if (endTimeModified != null) {
			if (endTimeOriginal == null) {
				endTimeChanged = true;
			} else if (endTimeOriginal != null) {
				endTimeChanged = (endTimeOriginal.compareTo(endTimeModified) == 0) ? false : true; 
			}
		}
		
		// Check for venue change
		boolean venueChanged = false;
		if (venueString == null && !venueField.getText().toString().isEmpty()) {
			venueChanged = true;
		} else if (venueString != null) {
			venueChanged = (venueString.compareTo(venueField.getText().toString()) == 0) ? false : true;
		}
		
		// Check for location change
		boolean locationChanged = false;
		if (locationString == null && !locationField.getText().toString().isEmpty()) {
			locationChanged = true;
		} else if (locationString != null) {
			locationChanged = (locationString.compareTo(locationField.getText().toString()) == 0) ? false : true;
		}
		
		// Check for notes change
		boolean notesChanged = false;
		if (notesString == null && !notesField.getText().toString().isEmpty()) {
			notesChanged = true;
		} else if (notesString != null) {
			notesChanged = (notesString.compareTo(notesField.getText().toString()) == 0) ? false : true;
		}
		
		// Check for attendees change
		boolean attendeesChanged = false;
		String attendeesString = "";
		if (attendeesStringArray == null && !attendeesField.getText().toString().isEmpty()) {
			attendeesChanged = true;
		} else if (attendeesStringArray != null) {
			for (int i = 0; i < attendeesStringArray.length; i++) {
				attendeesString += attendeesStringArray[i];
				if (i < attendeesStringArray.length - 1) {
					attendeesString += "\n";
				}
			}
			attendeesChanged = (attendeesString.compareTo(attendeesField.getText().toString()) == 0) ? false : true;
		}
		
		
		if (titleChanged
				|| startTimeChanged
				|| endTimeChanged
				|| venueChanged
				|| locationChanged
				|| notesChanged
				|| attendeesChanged) {
			result = true;
		}
		
		return result;
	}
	
	protected abstract boolean saveSocialEvent();
}
