package com.lazokin.socialeventplanner.fragments;

import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.activities.ViewEventActivity;
import com.lazokin.socialeventplanner.adapters.AgendaAdapter;
import com.lazokin.socialeventplanner.database.SocialEventDatabaseModel;
import com.lazokin.socialeventplanner.listeners.AgendaListener;
import com.lazokin.socialeventplanner.models.SocialEventDateComparator;
import com.lazokin.socialeventplanner.models.SocialEventModel;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AgendaFragment extends ListFragment implements Observer {
	
	public final static String STATE_SELECTED_ITEMS =
			"com.lazokin.socialeventplanner.list_fragment_selected_items";

	private SocialEventModel eventModel;
	
	private SortedSet<Integer> selectedItems = new TreeSet<Integer>();
	
	private AgendaAdapter adapter;
	
	public AgendaFragment() {
		super();
		eventModel = SocialEventDatabaseModel.getInstance(getActivity());
		eventModel.registerObserver(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(
				R.layout.fragment_agenda, container, false);
		
        // Restore saved data
        if (savedInstanceState != null) {
        	int[] selectedItemsArray =
        			savedInstanceState.getIntArray(STATE_SELECTED_ITEMS);
        	for (int selectedItem : selectedItemsArray) {
        		selectedItems.add(selectedItem);
        	}
        }
		
        // Create list adapter for fragment
        adapter = new AgendaAdapter(getActivity(),
        		R.layout.item_agenda, eventModel.getEvents());
		setListAdapter(adapter);
		
		return view;
	}

	@Override
	public void onStart() {
		
		// Sort adapter on date
		adapter.sort(new SocialEventDateComparator());
		
        // Setup batch contextual actions
        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AgendaListener(this));
		
		super.onStart();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		int[] selectedItemsArray = new int[selectedItems.size()];
		int idx = 0;
		for(Integer selectedItem : selectedItems) {
			selectedItemsArray[idx++] = selectedItem;
		}
		outState.putIntArray(STATE_SELECTED_ITEMS, selectedItemsArray);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		String eventId = eventModel.getEvents().get(position).getId();
		
		Intent intent = new Intent(getActivity(), ViewEventActivity.class);
		intent.putExtra(ViewEventActivity.EXTRA_SELECTED_EVENT_ID, eventId);
		startActivity(intent);
	}
	
	public SortedSet<Integer> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(SortedSet<Integer> selectedItems) {
		this.selectedItems = selectedItems;
	}

	@Override
	public void update(Observable observable, Object data) {
		if (isResumed()) {
			adapter.sort(new SocialEventDateComparator());
		}
	}
	
}
