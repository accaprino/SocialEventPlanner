package com.lazokin.socialeventplanner.models;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SocialEventMemoryModel extends Observable implements SocialEventModel {
	
	private static SocialEventMemoryModel instance = null;
	
	private List<SocialEvent> events;
	
	private SocialEventMemoryModel() {
		this.events = new ArrayList<SocialEvent>();	
	}
	
	public static SocialEventMemoryModel getInstance() {
		if (instance == null) {
			instance = new SocialEventMemoryModel();
		}
		return instance;
	}

	@Override
	public boolean addEvent(SocialEvent event) {
		boolean result = false;
		if (events.add(event)) {
			result = true;
			setChanged();
		}
		return result;
	}

	@Override
	public boolean removeEvent(String id) {
		boolean result = false;
		for (SocialEvent event : events) {
			if (event.getId().toString().equals(id)) {
				events.remove(event);
				result = true;
				setChanged();
				break;
			}
		}
		return result;
	}
	
	@Override
	public SocialEvent getEvent(String id) {
		SocialEvent result = null;
		for (SocialEvent event : events) {
			if (event.getId().toString().equals(id)) {
				return event;
			}
		}
		return result;
	}

	@Override
	public List<SocialEvent> getEvents() {
		return events;
	}

	@Override
	public List<String> getEventsForDay(Calendar calendar) {
		
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
		
		List<String> result = new ArrayList<String>();
		for(SocialEvent event : events) {
			if (event.getStartTime().get(Calendar.DAY_OF_YEAR) == dayOfYear) {
				String eventString = "";
				eventString += df.format(event.getStartTime().getTime());
				eventString += "-";
				eventString += df.format(event.getEndTime().getTime());
				eventString += " -> ";
				eventString += event.getTitle();
				result.add(eventString);
			}
		}
		return result;
	}

	@Override
	public List<SocialEvent> getFutureEvents(Calendar calendar) {
		
		long eventTimeInMillis;
		long nowTimeInMillis = calendar.getTimeInMillis();
		
		List<SocialEvent> result = new ArrayList<SocialEvent>();
		for(SocialEvent event : events) {
			eventTimeInMillis = event.getStartTime().getTimeInMillis();
			if (eventTimeInMillis > nowTimeInMillis) {
				result.add(event);
			}
		}
		return result;
	}

	@Override
	public boolean editEvent(String id, String title,
			Calendar startTime, Calendar endTime, String venue,
			String location, String notes, String[] attendees) {
		
		boolean result = false;
		
		SocialEvent event = getEvent(id);
		
		if (event != null) {
			event.setTitle(title);
			event.setStartTime(startTime);
			event.setEndTime(endTime);
			event.setVenue(venue);
			event.setLocation(location);
			event.setNotes(notes);
			event.setAttendees(attendees);
			result = true;
			setChanged();
		}
		
		return result;
		
	}

	@Override
	public void registerObserver(Observer o) {
		addObserver(o);
	}

	@Override
	public boolean dataHasChanged() {
		return hasChanged();
	}

	@Override
	public void notifyAllObservers() {
		notifyObservers();
	}
	
}
