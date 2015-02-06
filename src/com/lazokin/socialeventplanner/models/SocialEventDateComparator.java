package com.lazokin.socialeventplanner.models;

import java.util.Comparator;

public class SocialEventDateComparator implements Comparator<SocialEvent> {

	@Override
	public int compare(SocialEvent event1, SocialEvent event2) {
		
		return event2.getStartTime().compareTo(event1.getStartTime());

	}

}
