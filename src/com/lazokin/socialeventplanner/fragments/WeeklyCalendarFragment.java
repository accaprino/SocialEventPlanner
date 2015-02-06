package com.lazokin.socialeventplanner.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.activities.NewEventActivity;
import com.lazokin.socialeventplanner.database.SocialEventDatabaseModel;
import com.lazokin.socialeventplanner.models.SocialEventModel;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class WeeklyCalendarFragment extends Fragment implements Observer {
	
	public final static String EXTRA_SELECTED_DAY =
			"com.lazokin.socialeventplanner.weekly_calendar_fragment_day";
	
	private SocialEventModel eventModel;
	
	private LinearLayout layoutSun;
	private LinearLayout layoutMon;
	private LinearLayout layoutTue;
	private LinearLayout layoutWed;
	private LinearLayout layoutThu;
	private LinearLayout layoutFri;
	private LinearLayout layoutSat;
	
	private TextView dateSun;
	private TextView dateMon;
	private TextView dateTue;
	private TextView dateWed;
	private TextView dateThu;
	private TextView dateFri;
	private TextView dateSat;
	
	private ScrollView scrollSun;
	private ScrollView scrollMon;
	private ScrollView scrollTue;
	private ScrollView scrollWed;
	private ScrollView scrollThu;
	private ScrollView scrollFri;
	private ScrollView scrollSat;
	
	private TextView eventsSun;
	private TextView eventsMon;
	private TextView eventsTue;
	private TextView eventsWed;
	private TextView eventsThu;
	private TextView eventsFri;
	private TextView eventsSat;
	
	Calendar sun;
	Calendar mon;
	Calendar tue;
	Calendar wed;
	Calendar thu;
	Calendar fri;
	Calendar sat;

	public WeeklyCalendarFragment() {
		super();
		eventModel = SocialEventDatabaseModel.getInstance(getActivity());
		eventModel.registerObserver(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate view
		View view = inflater.inflate(
				R.layout.fragment_weekly_calendar, container, false);
		
		// Setup views
		layoutSun = (LinearLayout) view.findViewById(R.id.weekly_item_sun);
		layoutMon = (LinearLayout) view.findViewById(R.id.weekly_item_mon);
		layoutTue = (LinearLayout) view.findViewById(R.id.weekly_item_tue);
		layoutWed = (LinearLayout) view.findViewById(R.id.weekly_item_wed);
		layoutThu = (LinearLayout) view.findViewById(R.id.weekly_item_thu);
		layoutFri = (LinearLayout) view.findViewById(R.id.weekly_item_fri);
		layoutSat = (LinearLayout) view.findViewById(R.id.weekly_item_sat);
		
		dateSun = (TextView) layoutSun.findViewById(R.id.weekly_item_date_text);
		dateMon = (TextView) layoutMon.findViewById(R.id.weekly_item_date_text);
		dateTue = (TextView) layoutTue.findViewById(R.id.weekly_item_date_text);
		dateWed = (TextView) layoutWed.findViewById(R.id.weekly_item_date_text);
		dateThu = (TextView) layoutThu.findViewById(R.id.weekly_item_date_text);
		dateFri = (TextView) layoutFri.findViewById(R.id.weekly_item_date_text);
		dateSat = (TextView) layoutSat.findViewById(R.id.weekly_item_date_text);
		
		scrollSun = (ScrollView) layoutSun.findViewById(R.id.weekly_item_events_scroll);
		scrollMon = (ScrollView) layoutMon.findViewById(R.id.weekly_item_events_scroll);
		scrollTue = (ScrollView) layoutTue.findViewById(R.id.weekly_item_events_scroll);
		scrollWed = (ScrollView) layoutWed.findViewById(R.id.weekly_item_events_scroll);
		scrollThu = (ScrollView) layoutThu.findViewById(R.id.weekly_item_events_scroll);
		scrollFri = (ScrollView) layoutFri.findViewById(R.id.weekly_item_events_scroll);
		scrollSat = (ScrollView) layoutSat.findViewById(R.id.weekly_item_events_scroll);

		eventsSun = (TextView) scrollSun.findViewById(R.id.weekly_item_events_text);
		eventsMon = (TextView) scrollMon.findViewById(R.id.weekly_item_events_text);
		eventsTue = (TextView) scrollTue.findViewById(R.id.weekly_item_events_text);
		eventsWed = (TextView) scrollWed.findViewById(R.id.weekly_item_events_text);
		eventsThu = (TextView) scrollThu.findViewById(R.id.weekly_item_events_text);
		eventsFri = (TextView) scrollFri.findViewById(R.id.weekly_item_events_text);
		eventsSat = (TextView) scrollSat.findViewById(R.id.weekly_item_events_text);

		// Add on click listeners to scroll views
		eventsSun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addEventOnDay(sun);
			}	
		});
		eventsMon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addEventOnDay(mon);
			}	
		});
		eventsTue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addEventOnDay(tue);
			}	
		});
		eventsWed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addEventOnDay(wed);
			}	
		});
		eventsThu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addEventOnDay(thu);
			}	
		});
		eventsFri.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addEventOnDay(fri);
			}	
		});
		eventsSat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addEventOnDay(sat);
			}	
		});

		return view;
	}

	@Override
	public void onStart() {
		
		fillCalender();
		
		super.onStart();
	}

	private void fillCalender() {
		Calendar today = Calendar.getInstance();
		
		sun = (Calendar) today.clone();
		sun.add(Calendar.DAY_OF_WEEK,
				today.getFirstDayOfWeek() -
				today.get(Calendar.DAY_OF_WEEK));
		
		mon = (Calendar) sun.clone();
		mon.add(Calendar.DAY_OF_WEEK, 1);
		
		tue = (Calendar) mon.clone();
		tue.add(Calendar.DAY_OF_WEEK, 1);
		
		wed = (Calendar) tue.clone();
		wed.add(Calendar.DAY_OF_WEEK, 1);
		
		thu = (Calendar) wed.clone();
		thu.add(Calendar.DAY_OF_WEEK, 1);
		
		fri = (Calendar) thu.clone();
		fri.add(Calendar.DAY_OF_WEEK, 1);
		
		sat = (Calendar) fri.clone();
		sat.add(Calendar.DAY_OF_WEEK, 1);
		
		Locale current = getResources().getConfiguration().locale;
		SimpleDateFormat sdf = new SimpleDateFormat("EEE\nd MMM", current);
		
		fillWeekItem(dateSun, eventsSun, sun, sdf);
		fillWeekItem(dateMon, eventsMon, mon, sdf);
		fillWeekItem(dateTue, eventsTue, tue, sdf);
		fillWeekItem(dateWed, eventsWed, wed, sdf);
		fillWeekItem(dateThu, eventsThu, thu, sdf);
		fillWeekItem(dateFri, eventsFri, fri, sdf);
		fillWeekItem(dateSat, eventsSat, sat, sdf);
	}
	
	private void fillWeekItem(TextView date, TextView events, Calendar dayOfWeek, SimpleDateFormat sdf) {
		date.setText(sdf.format(dayOfWeek.getTime()));
		events.setText("");
		List<String> eventsForDay = eventModel.getEventsForDay(dayOfWeek);
		for (int i = 0; i < eventsForDay.size(); i++) {
			events.append(eventsForDay.get(i));
			if (i < eventsForDay.size() - 1) {
				events.append("\n");
			}
		}
	}
	
	public void addEventOnDay(Calendar dayOfWeek) {
		Intent intent = new Intent(getActivity(), NewEventActivity.class);
		intent.putExtra(EXTRA_SELECTED_DAY, dayOfWeek);
		startActivity(intent);
	}

	@Override
	public void update(Observable observable, Object data) {
		if (isResumed()) {
			fillCalender();
		}
	}

}
