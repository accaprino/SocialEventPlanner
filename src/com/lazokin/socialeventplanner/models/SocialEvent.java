package com.lazokin.socialeventplanner.models;

import java.util.Calendar;

public interface SocialEvent {

	public abstract String getId();

	public abstract String getTitle();

	public abstract void setTitle(String title);

	public abstract Calendar getStartTime();

	public abstract void setStartTime(Calendar startTime);

	public abstract Calendar getEndTime();

	public abstract void setEndTime(Calendar endTime);

	public abstract String getVenue();

	public abstract void setVenue(String venue);

	public abstract String getLocation();

	public abstract void setLocation(String location);

	public abstract String getNotes();

	public abstract void setNotes(String notes);

	public abstract String[] getAttendees();

	public abstract void setAttendees(String[] attendees);
	
	public abstract String getStartDateString();
	
	public abstract String getStartTimeString();
	
	public abstract String getEndDateString();
	
	public abstract String getEndTimeString();

}