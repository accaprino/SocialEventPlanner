package com.lazokin.socialeventplanner.broadcastreceivers;

import com.lazokin.socialeventplanner.activities.MainActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

	public ConnectivityChangeReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// Get the connectivity manager
		ConnectivityManager cm =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		// Check network connectivity
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		
		// Get the pending intent for notification
		PendingIntent pi = MainActivity.getNotificationServicePendingIntent();
		
		// Get the alarm manager
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		// Start a new notification service when connected to network, else cancel
		// notification service when not connected to network
		if (isConnected) {
			if (pi != null) {
				am.cancel(pi);
				am.setRepeating(
						AlarmManager.ELAPSED_REALTIME,
						MainActivity.notificationPeriod,
						MainActivity.notificationPeriod,
						pi);
			}
		} else {
			if (pi != null) {
				am.cancel(pi);
			}
		}

	}

}
