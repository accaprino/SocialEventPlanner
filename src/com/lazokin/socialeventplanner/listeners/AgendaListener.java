package com.lazokin.socialeventplanner.listeners;

import java.util.SortedSet;

import com.lazokin.socialeventplanner.R;
import com.lazokin.socialeventplanner.asynctasks.DeleteSocialEventTask;
import com.lazokin.socialeventplanner.fragments.AgendaFragment;
import com.lazokin.socialeventplanner.models.SocialEventMemoryModel;
import com.lazokin.socialeventplanner.models.SocialEventModel;

import android.app.ListFragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AgendaListener implements MultiChoiceModeListener {
	
	private SocialEventModel eventManager = SocialEventMemoryModel.getInstance();
	
	SortedSet<Integer> selectedItems;
	
	ListFragment listFragment;
	ListView listView;
	ListAdapter listAdpater;
	

	public AgendaListener(ListFragment listFragment) {
		this.listFragment = listFragment;
		this.listView = listFragment.getListView();
		this.listAdpater = listFragment.getListAdapter();
		this.selectedItems = ((AgendaFragment)listFragment).getSelectedItems();
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		
		if (checked == true) {
			selectedItems.add(position);
		} else {
			selectedItems.remove(position);
		}

		if (selectedItems.size() > 1) {
			mode.setTitle("Delete Events");
			mode.setSubtitle(selectedItems.size() + " Selected");
		} else {
			mode.setTitle("Delete Event");
			mode.setSubtitle("");
		}
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_discard:
		{
			// Collect IDs of selected events
			String[] eventIds = new String[selectedItems.size()];
			int idx = 0;
			for (int pos : selectedItems) {
				eventIds[idx++] = eventManager.getEvents().get(pos).getId().toString();
			}

			// Delete events via asynchronous task
			new DeleteSocialEventTask(listFragment.getActivity()).execute(eventIds);
			
			selectedItems.clear();
			mode.finish();
			return true;
		}
		default:
			return false;
		}
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		
		MenuInflater inflator = mode.getMenuInflater();
		inflator.inflate(R.menu.menu_agenda_contextual, menu);
		
		if (!selectedItems.isEmpty()) {
			if (selectedItems.size() > 1) {
				mode.setTitle("Delete Events");
				mode.setSubtitle(selectedItems.size() + " events selected");
			} else {
				mode.setTitle("Delete Event");
				mode.setSubtitle("");
			}
		}
		
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		selectedItems.clear();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

}
