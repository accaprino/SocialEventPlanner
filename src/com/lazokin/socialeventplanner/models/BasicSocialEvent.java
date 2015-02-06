package com.lazokin.socialeventplanner.models;

import java.util.Calendar;

public class BasicSocialEvent extends AbstractSocialEvent {
	
	public BasicSocialEvent(String title, Calendar startTime, Calendar endTime,
			String venue, String location, String notes, String[] attendees)
	{
		super(title, startTime, endTime, venue, location, notes, attendees);
	}
	
	public BasicSocialEvent(String id, String title, Calendar startTime, Calendar endTime,
			String venue, String location, String notes, String[] attendees)
	{
		super(id, title, startTime, endTime, venue, location, notes, attendees);
	}

}
