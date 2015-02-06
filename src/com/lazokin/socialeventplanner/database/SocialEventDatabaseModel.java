package com.lazokin.socialeventplanner.database;

import java.util.Calendar;
import java.util.List;
import java.util.Observer;

import com.lazokin.socialeventplanner.models.BasicSocialEvent;
import com.lazokin.socialeventplanner.models.SocialEvent;
import com.lazokin.socialeventplanner.models.SocialEventMemoryModel;
import com.lazokin.socialeventplanner.models.SocialEventModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SocialEventDatabaseModel extends SQLiteOpenHelper implements SocialEventModel {
	
	private static SocialEventDatabaseModel instance = null;
	
	private SocialEventMemoryModel memoryModel = SocialEventMemoryModel.getInstance();
	
	// Database details
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "SocialEventPlanner.db";
	
	// Schema details
	private static final String TABLE_NAME_SOCIAL_EVENTS = "social_events_table";
	private static final String TABLE_NAME_ATTENDEES = "attendees_table";
	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_TITLE = "title";
	private static final String COLUMN_NAME_START_TIME = "start_time";
	private static final String COLUMN_NAME_END_TIME = "end_time";
	private static final String COLUMN_NAME_VENUE = "venue";
	private static final String COLUMN_NAME_LOCATION = "location";
	private static final String COLUMN_NAME_NOTES = "notes";
	private static final String COLUMN_NAME_ATTENDEE = "attendee";
	
	// Create table methods
	private static final String SQL_CREATE_TABLE_SOCIAL_EVENTS = 
			"CREATE TABLE " + TABLE_NAME_SOCIAL_EVENTS + " (" + 
			COLUMN_NAME_ID + " TEXT," +
			COLUMN_NAME_TITLE + " TEXT," +
			COLUMN_NAME_START_TIME + " TEXT," +
			COLUMN_NAME_END_TIME + " TEXT," +
			COLUMN_NAME_VENUE + " TEXT," +
			COLUMN_NAME_LOCATION + " TEXT," +
			COLUMN_NAME_NOTES + " TEXT," +
			"PRIMARY KEY (" + COLUMN_NAME_ID + ")" +
			")";
	
	private static final String SQL_CREATE_TABLE_ATTENDEES = 
			"CREATE TABLE " + TABLE_NAME_ATTENDEES + " (" + 
			COLUMN_NAME_ID + " TEXT," +
			COLUMN_NAME_ATTENDEE + " TEXT," +
			"PRIMARY KEY (" + COLUMN_NAME_ID + "," + COLUMN_NAME_ATTENDEE + ")," +
			"FOREIGN KEY (" + COLUMN_NAME_ID + ") REFERENCES " + TABLE_NAME_SOCIAL_EVENTS + " (" + COLUMN_NAME_ID + ")" +
			")";

	private SocialEventDatabaseModel(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		updateLocalModel();
	}

	public static SocialEventDatabaseModel getInstance(Context context) {
		if (instance == null) {
			instance = new SocialEventDatabaseModel(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_SOCIAL_EVENTS);
		db.execSQL(SQL_CREATE_TABLE_ATTENDEES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean addEvent(SocialEvent event) {
		
		boolean result = false;
		
		// Get database
		SQLiteDatabase db = getWritableDatabase();
		
		// Create content values
		ContentValues eventValues = new ContentValues();
		eventValues.put(COLUMN_NAME_ID, event.getId());
		eventValues.put(COLUMN_NAME_TITLE, event.getTitle());
		eventValues.put(COLUMN_NAME_START_TIME, String.valueOf(event.getStartTime().getTimeInMillis()));
		eventValues.put(COLUMN_NAME_END_TIME, String.valueOf(event.getEndTime().getTimeInMillis()));
		eventValues.put(COLUMN_NAME_VENUE, event.getVenue());
		eventValues.put(COLUMN_NAME_LOCATION, event.getLocation());
		eventValues.put(COLUMN_NAME_NOTES, event.getNotes());
		
		// Insert event data into table
		db.insert(
				TABLE_NAME_SOCIAL_EVENTS,
				null,
				eventValues);
		
		// Add attendees if there are any
		if (event.getAttendees().length > 0) {
			// Create content values
			ContentValues attendeeValues = new ContentValues();
			for (String attendee : event.getAttendees()) {
				attendeeValues.put(COLUMN_NAME_ID, event.getId());
				attendeeValues.put(COLUMN_NAME_ATTENDEE, attendee);
				// Insert event data into table
				db.insert(TABLE_NAME_ATTENDEES, null, attendeeValues);
			}	
		}

		// Local data synchronization
		if (memoryModel.addEvent(event)) {
			result = true;
		}
		
		db.close();

		return result;
	}

	@Override
	public boolean removeEvent(String id) {
		
		boolean result = false;
		
		// Get database
		SQLiteDatabase db = getWritableDatabase();
		
		// Remove event data from tables
		db.delete(
				TABLE_NAME_SOCIAL_EVENTS,
				COLUMN_NAME_ID + " = " + "'" + id + "'",
				null);
		db.delete(
				TABLE_NAME_ATTENDEES,
				COLUMN_NAME_ID + " = " + "'" + id + "'",
				null);
		
		// Local data synchronization
		if (memoryModel.removeEvent(id)) {
			result = true;
		}
		
		db.close();
		
		return result;
	}

	@Override
	public boolean editEvent(String id, String title,
			Calendar startTime, Calendar endTime, String venue,
			String location, String notes, String[] attendeesArray) {
		
		boolean result = false;
		
		// Get database
		SQLiteDatabase db = getWritableDatabase();
		
		// Create content values
		ContentValues eventValues = new ContentValues();
		eventValues.put(COLUMN_NAME_TITLE, title);
		eventValues.put(COLUMN_NAME_START_TIME, String.valueOf(startTime.getTimeInMillis()));
		eventValues.put(COLUMN_NAME_END_TIME, String.valueOf(endTime.getTimeInMillis()));
		eventValues.put(COLUMN_NAME_VENUE, venue);
		eventValues.put(COLUMN_NAME_LOCATION, location);
		eventValues.put(COLUMN_NAME_NOTES, notes);
		
		// Update event data in social event table
		db.update(
				TABLE_NAME_SOCIAL_EVENTS,
				eventValues,
				COLUMN_NAME_ID + " = " + "'" + id + "'",
				null);
		
		// Delete any attendees for event
		db.delete(
				TABLE_NAME_ATTENDEES,
				COLUMN_NAME_ID + " = " + "'" + id + "'",
				null);
		
		// Add any attendees to event
		if (attendeesArray.length > 0) {

			// Create content values
			ContentValues attendeeValues = new ContentValues();
			for (String attendee : attendeesArray) {
				attendeeValues.put(COLUMN_NAME_ID, id);
				attendeeValues.put(COLUMN_NAME_ATTENDEE, attendee);
				// Insert event data into table
				db.insert(TABLE_NAME_ATTENDEES, null, attendeeValues);
			}	
		}
		
		// Local data synchronization
		if (memoryModel.editEvent(id, title, startTime, endTime,
				venue, location, notes, attendeesArray)) {
			result = true;
		}
		
		db.close();
		
		return result;
	}
	
	private void updateLocalModel() {
		
		// Get database
		SQLiteDatabase db = getReadableDatabase();
		
		String[] columns = {
				COLUMN_NAME_ID,
				COLUMN_NAME_TITLE,
				COLUMN_NAME_START_TIME,
				COLUMN_NAME_END_TIME,
				COLUMN_NAME_VENUE,
				COLUMN_NAME_LOCATION,
				COLUMN_NAME_NOTES
		};
		
		Cursor eventCursor = db.query(
				TABLE_NAME_SOCIAL_EVENTS,
				columns,
				null,
				null,
				null,
				null,
				null);
		
		Cursor attendeesCursor = null;
		while (eventCursor.moveToNext()) {
			String id = eventCursor.getString(eventCursor.getColumnIndex(COLUMN_NAME_ID));
			String title = eventCursor.getString(eventCursor.getColumnIndex(COLUMN_NAME_TITLE));
			String startTimeMillis = eventCursor.getString(eventCursor.getColumnIndex(COLUMN_NAME_START_TIME));
			String endTimeMillis = eventCursor.getString(eventCursor.getColumnIndex(COLUMN_NAME_END_TIME));
			String venue = eventCursor.getString(eventCursor.getColumnIndex(COLUMN_NAME_VENUE));
			String location = eventCursor.getString(eventCursor.getColumnIndex(COLUMN_NAME_LOCATION));
			String notes = eventCursor.getString(eventCursor.getColumnIndex(COLUMN_NAME_NOTES));
			
			String[] attendees = null;
			attendeesCursor = db.query(
					TABLE_NAME_ATTENDEES,
					new String[] {COLUMN_NAME_ATTENDEE},
					COLUMN_NAME_ID + " = " + "'" + id + "'",
					null,
					null,
					null,
					null);
			if (attendeesCursor.getCount() > 0) {
				attendees = new String[attendeesCursor.getCount()];
				int idx = 0;
				while (attendeesCursor.moveToNext()) {
					String attendee = attendeesCursor.getString(attendeesCursor.getColumnIndex(COLUMN_NAME_ATTENDEE));
					attendees[idx++] = attendee;
				}
			}

			Calendar startTime = Calendar.getInstance();
			startTime.setTimeInMillis(Long.parseLong(startTimeMillis));
			Calendar endTime = Calendar.getInstance();
			endTime.setTimeInMillis(Long.parseLong(endTimeMillis));
			
			SocialEvent event = new BasicSocialEvent(id, title, startTime, endTime,
					venue, location,notes, attendees);
			
			memoryModel.addEvent(event);
		}
		
		db.close();
		eventCursor.close();
		if (attendeesCursor != null) {
			attendeesCursor.close();
		}
		
	}

	@Override
	public SocialEvent getEvent(String id) {
		return memoryModel.getEvent(id);
	}

	@Override
	public List<SocialEvent> getEvents() {
		return memoryModel.getEvents();
	}

	@Override
	public List<String> getEventsForDay(Calendar calendar) {
		return memoryModel.getEventsForDay(calendar);
	}

	@Override
	public List<SocialEvent> getFutureEvents(Calendar calendar) {
		return memoryModel.getFutureEvents(calendar);
	}

	public SocialEventMemoryModel getMemoryModel() {
		return memoryModel;
	}

	@Override
	public void registerObserver(Observer o) {
		memoryModel.registerObserver(o);
	}

	@Override
	public boolean dataHasChanged() {
		return memoryModel.dataHasChanged();
	}

	@Override
	public void notifyAllObservers() {
		memoryModel.notifyAllObservers();
	}

	

}
