package com.lazokin.socialeventplanner.models;

import java.util.Calendar;
import java.util.List;
import java.util.Observer;

public interface SocialEventModel {

	public abstract boolean addEvent(SocialEvent event);

	public abstract boolean removeEvent(String id);

	public abstract SocialEvent getEvent(String id);

	public abstract List<SocialEvent> getEvents();

	public abstract List<String> getEventsForDay(Calendar calendar);

	public abstract List<SocialEvent> getFutureEvents(Calendar calendar);

	public abstract boolean editEvent(String id, String title,
			Calendar startTime, Calendar endTime, String venue,
			String location, String notes, String[] attendees);
	
	public void registerObserver(Observer o);
	
	public boolean dataHasChanged();
	
	public void notifyAllObservers();

}