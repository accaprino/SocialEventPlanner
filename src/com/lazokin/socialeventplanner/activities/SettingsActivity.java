package com.lazokin.socialeventplanner.activities;

import com.lazokin.socialeventplanner.fragments.SettingsFragment;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();	
	}
}
