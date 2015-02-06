package com.lazokin.socialeventplanner.activities;

import java.util.Calendar;

import com.lazokin.socialeventplanner.asynctasks.EditSocialEventTask;
import com.lazokin.socialeventplanner.models.SocialEvent;

import android.content.Intent;
import android.os.Bundle;

public class EditEventActivity extends InputEventActivity {
	
	private String eventId;
	SocialEvent socialEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get selected social event
		Intent intent = getIntent();
		eventId = intent.getStringExtra(
				ViewEventActivity.EXTRA_SELECTED_EVENT_ID);
		socialEvent = eventModel.getEvent(eventId);
		
		// Get selected social event data
		titleString = socialEvent.getTitle();
		venueString = socialEvent.getVenue();
		locationString = socialEvent.getLocation();
		notesString = socialEvent.getNotes();
		attendeesStringArray = socialEvent.getAttendees();
		
		// Store calendars
		startTimeOriginal = (Calendar) socialEvent.getStartTime().clone();
		endTimeOriginal = (Calendar) socialEvent.getEndTime().clone();
		startTimeModified = (Calendar) socialEvent.getStartTime().clone();
		endTimeModified = (Calendar) socialEvent.getEndTime().clone();
		
		// Format Date
		startDateString = socialEvent.getStartDateString();
		endDateString = socialEvent.getEndDateString();
		
		// Format Times
		startTimeString = socialEvent.getStartTimeString();
		endTimeString = socialEvent.getEndTimeString();
		
		// Update views with social event data
		titleField.setText(titleString);
		startDateField.setText(startDateString);
		startTimeField.setText(startTimeString);
		endDateField.setText(endDateString);
		endTimeField.setText(endTimeString);
		venueField.setText(venueString);
		locationField.setText(locationString);
		notesField.setText(notesString);
		if (attendeesStringArray != null) {
			for (int i = 0; i < attendeesStringArray.length; i++) {
				attendeesField.append(attendeesStringArray[i]);
				if (i < attendeesStringArray.length - 1) {
					attendeesField.append("\n");
				}
			}
		}
	}
	
	@Override
	protected boolean saveSocialEvent() {
		
		// Edit event via asynchronous task
		new EditSocialEventTask(this, socialEvent.getId(), titleString,
				startTimeModified, endTimeModified, venueString,
				locationString, notesString, attendeesStringArray).execute();
		
		return true;
	}
}
