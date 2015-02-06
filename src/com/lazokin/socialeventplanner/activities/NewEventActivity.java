package com.lazokin.socialeventplanner.activities;

import java.text.DateFormat;
import java.util.Calendar;

import com.lazokin.socialeventplanner.asynctasks.AddSocialEventTask;
import com.lazokin.socialeventplanner.fragments.WeeklyCalendarFragment;
import com.lazokin.socialeventplanner.models.BasicSocialEvent;

import android.content.Intent;
import android.os.Bundle;

public class NewEventActivity extends InputEventActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		// Get calendar if started with calendar
		if (intent.hasExtra(WeeklyCalendarFragment.EXTRA_SELECTED_DAY)) {
			startTimeOriginal = (Calendar)intent.getSerializableExtra(
					WeeklyCalendarFragment.EXTRA_SELECTED_DAY);
			DateFormat d = DateFormat.getDateInstance(DateFormat.MEDIUM);
			startDateString = d.format(startTimeOriginal.getTime());
			startDateField.setText(startDateString);
		}
	}

	@Override
	public boolean saveSocialEvent() {
		
		// Create and save a social event object
		BasicSocialEvent event = new BasicSocialEvent(titleString, startTimeModified, endTimeModified,
				venueString, locationString,notesString, attendeesStringArray);
		
		// Add event via asynchronous task
		new AddSocialEventTask(this).execute(event);

		return true;
		
	}
	
}
