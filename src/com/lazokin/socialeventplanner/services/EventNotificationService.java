package com.lazokin.socialeventplanner.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lazokin.socialeventplanner.activities.MainActivity;
import com.lazokin.socialeventplanner.activities.ViewEventActivity;
import com.lazokin.socialeventplanner.database.SocialEventDatabaseModel;
import com.lazokin.socialeventplanner.models.SocialEvent;
import com.lazokin.socialeventplanner.models.SocialEventMemoryModel;
import com.lazokin.socialeventplanner.models.SocialEventModel;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;

public class EventNotificationService extends IntentService {
	
	private static long timeLastCheck;
	private long timeNow;
	private boolean timeForNotificationCheck;
	
	Handler mHandler;
	SharedPreferences sharedPref;
	boolean notificationsEnabled;
	long timeNotificationThreshold;
	private SocialEventModel eventModel;
	
	public final static String NEXT_NOTIFICATION_TIME =
			"com.lazokin.socialeventplanner.next_notification_time";

	public EventNotificationService() {
		super("EventReminderService");
		mHandler = new Handler();
	}

	@Override
	public void onCreate() {
		
		// Get a reference to the application shared preferences
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		// Initialize database if necessary
		SocialEventDatabaseModel.getInstance(this);
		
		// Get a reference to the local mode
		eventModel = SocialEventMemoryModel.getInstance();
		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    return super.onStartCommand(intent,flags,startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		// Return if no network connection
		if (!haveNetworkConnection()) {
			return;
		}
	
		// Get notification preferences
		notificationsEnabled =
				sharedPref.getBoolean("pref_key_enable_notifications", true);
		timeNotificationThreshold = Long.parseLong(
				sharedPref.getString("pref_key_notification_threshold", "15"))
				* MainActivity.MINUTES_TO_MILLISECONDS;

		// Check for location based notification if option enabled
		if (notificationsEnabled) {
			
			timeNow = System.currentTimeMillis();
			
			timeForNotificationCheck = (timeNow - timeLastCheck) >
				MainActivity.notificationPeriod;
			
			if (timeForNotificationCheck) {
				
				// Check upcoming events for location based notification
				checkUpcomingEventsForNotification();
				
				
				// Update time of last check
				timeLastCheck = System.currentTimeMillis();
			}

		}
	      
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	// Check all upcoming events for a notification
	private void checkUpcomingEventsForNotification() {
		
		long timeNow;
		long timeOfEvent;
		long timeToEvent;
		long travelTimeToEvent;
		String mode;
		
		// Get current time
		Calendar nowCalendar = Calendar.getInstance();
		timeNow = nowCalendar.getTimeInMillis();
		
		// Get future events
		List<SocialEvent> futureEvents = eventModel.getFutureEvents(nowCalendar);

		// Get current location
		String currentLocation = getCurrentLocation();
		if (currentLocation == null) {
			return;
		}
		
		// Get mode of transport from user preferences
		mode = sharedPref.getString("pref_key_transport_mode", "driving");
		
		// Loop through future events
		for (SocialEvent event : futureEvents) {
			
			// Get time of event
			timeOfEvent = event.getStartTime().getTimeInMillis();
			
			// Calculate time to event
			timeToEvent = timeOfEvent - timeNow;
			
			// Get destination location
			String destinationLocation = event.getLocation();
			if (destinationLocation.isEmpty())
			{
				continue;
			}
			
			// Calculate travel time to event
			travelTimeToEvent = getTravelTimeBetweenTwoLocations(
					currentLocation, destinationLocation, mode);
			
			// Could not calculate travel time
			if (travelTimeToEvent < 0)
			{
				continue;
			}
			
			// Post notification
			if ((timeToEvent - travelTimeToEvent) < timeNotificationThreshold) {
				postNotification(event);
			}
		}

	}
	
	// Post a notification for an event
	private void postNotification(SocialEvent event) {
		
		// Setup notification parameters
		String eventId = event.getId();
		String title = event.getTitle();
		String text = event.getStartDateString() + " - " + event.getStartTimeString();
		
		
		// Create intent to display event
		Intent intent = new Intent(getApplicationContext(), ViewEventActivity.class);
		intent.putExtra(ViewEventActivity.EXTRA_SELECTED_EVENT_ID, eventId);
	
		// Create an artificial back stack
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(ViewEventActivity.class);
		stackBuilder.addNextIntent(intent);
		
		// Create a pending intent
		PendingIntent pendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		
		// Build notification
		Notification notification =
		        new Notification.Builder(this)
		        .setSmallIcon(android.R.drawable.ic_popup_reminder)
		        .setContentTitle(title)
		        .setContentText(text)
		        .setContentIntent(pendingIntent)
		        .build();

		// Get the notification manager
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(EventNotificationService.NOTIFICATION_SERVICE);
	
		// Post notification
		mNotificationManager.notify(eventId, 0, notification);
		
	}

	// Get the travel time between two location. Travel time is in milliseconds.
	private long getTravelTimeBetweenTwoLocations(String loc1, String loc2, String mode) {
		
		long result = -1;
		HttpURLConnection connection = null;
		
		// Create a string builder
		StringBuilder sb = new StringBuilder();
		
		// Establish connection and make request
		try {
			
			// Create URL
			String urlString = "http://maps.googleapis.com/maps/api/directions/json?origin="
	        		+ loc1 + "&destination=" + loc2 + "&mode=" + mode;
			URL url = new URL(urlString);
			
			// Setup connection
			connection = (HttpURLConnection) url.openConnection();
			
			// check the response code
	        int statusCode = connection.getResponseCode();
	        if (statusCode != HttpURLConnection.HTTP_OK)
	        {
	            return result;
	        }
			
		    // Create a buffered reader from connection stream
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			
			// Read the input stream
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			
		} catch (Exception e) {
			
		} finally {
			if (connection != null) {
	            connection.disconnect();
			}
		}
		
		// Create JSON Object
		JSONObject jsonObject = new JSONObject();
        try {

            jsonObject = new JSONObject(sb.toString());

            JSONArray routes = jsonObject.getJSONArray("routes");

            JSONObject route = routes.getJSONObject(0);

            JSONArray legs = route.getJSONArray("legs");
            
            JSONObject leg = legs.getJSONObject(0);
            
            JSONObject duration = leg.getJSONObject("duration");
            
            result = duration.getInt("value");

        } catch (JSONException e) {

        }

        // Return time in milliseconds
		return result * 1000;
		
	}

	private String getCurrentLocation()
	{
		String result = null;
		Location location = null;
		
		LocationManager lm = (LocationManager)this.getSystemService(
				EventNotificationService.LOCATION_SERVICE);
		
		location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		
		if(location != null) {
		     double lng = location.getLongitude();
		     double lat = location.getLatitude();
		     result = String.valueOf(lat) + "," + String.valueOf(lng);
		}
		
		return result;
	}
	
	private boolean haveNetworkConnection() {
		
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(
						Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}
	
	public static String convertSecondsToHMmSs(long milliseconds) {
		int seconds = (int) (milliseconds / 1E3);
	    int m = (seconds / 60) % 60;
	    int h = (seconds / (60 * 60)) % 24;
	    if (h > 0) {
	    	return String.format(Locale.getDefault(), "%d hrs %02d min", h,m);
	    } else {
	    	return String.format(Locale.getDefault(), "%02d min", m);
	    }
	    
	}
	
}
