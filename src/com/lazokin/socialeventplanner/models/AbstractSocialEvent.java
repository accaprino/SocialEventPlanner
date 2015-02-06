package com.lazokin.socialeventplanner.models;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.UUID;

public class AbstractSocialEvent implements SocialEvent {
	
	private String id;
	private String title;
	private Calendar startTime;
	private Calendar endTime;
	private String venue;
	private String location;
	private String notes;
	private String[] attendees;
	
	public AbstractSocialEvent(String title, Calendar startTime, Calendar endTime,
			String venue, String location, String notes, String[] attendees)
	{
		this.id = UUID.randomUUID().toString();
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.venue = venue;
		this.location = location;
		this.notes = notes;
		this.attendees = attendees;
	}
	
	public AbstractSocialEvent(String id, String title, Calendar startTime, Calendar endTime,
			String venue, String location, String notes, String[] attendees)
	{
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.venue = venue;
		this.location = location;
		this.notes = notes;
		this.attendees = attendees;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Calendar getStartTime() {
		return startTime;
	}

	@Override
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	@Override
	public Calendar getEndTime() {
		return endTime;
	}

	@Override
	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	@Override
	public String getVenue() {
		return venue;
	}

	@Override
	public void setVenue(String venue) {
		this.venue = venue;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String getNotes() {
		return notes;
	}

	@Override
	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String[] getAttendees() {
		return attendees;
	}

	@Override
	public void setAttendees(String[] attendees) {
		this.attendees = attendees;
	}

	@Override
	public String getStartDateString() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(startTime.getTime());
	}

	@Override
	public String getStartTimeString() {
		DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
		return df.format(startTime.getTime());
	}

	@Override
	public String getEndDateString() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(endTime.getTime());
	}

	@Override
	public String getEndTimeString() {
		DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
		return df.format(endTime.getTime());
	}

}
