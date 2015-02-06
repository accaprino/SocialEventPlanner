package com.lazokin.socialeventplanner.listeners;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.fragments.AgendaFragment;
import com.lazokin.socialeventplanner.fragments.WeeklyCalendarFragment;

import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.Fragment;

public class MainSpinnerListener implements OnNavigationListener {
	
	Activity activity;
	Fragment agendaFragment;
	Fragment weeklyCalendarFragment;

	public MainSpinnerListener(Activity activity, Fragment agendaFragment,
			Fragment weeklyCalendarFragment) {
		this.activity = activity;
		this.agendaFragment = agendaFragment;
		this.weeklyCalendarFragment = weeklyCalendarFragment;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		
		if (itemPosition == 0) {
			if (agendaFragment == null) {
				agendaFragment = new AgendaFragment();
			}
			activity.getFragmentManager()
			.beginTransaction()
			.replace(R.id.fragment_container, agendaFragment)
			.commit();
		}
		if (itemPosition == 1) {
			if (weeklyCalendarFragment == null) {
				weeklyCalendarFragment = new WeeklyCalendarFragment();
			}
			activity.getFragmentManager()
			.beginTransaction()
			.replace(R.id.fragment_container, weeklyCalendarFragment)
			.commit();
		}
		return false;
	}

}
