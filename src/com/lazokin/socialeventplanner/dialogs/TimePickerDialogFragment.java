package com.lazokin.socialeventplanner.dialogs;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.widget.TimePicker;

public class TimePickerDialogFragment extends DialogFragment implements
		OnTimeSetListener {
	
	public interface TimePickerDialogListener {
        public void onTimeSet(DialogFragment dialog, Calendar calendar);
    }
	
	private TimePickerDialogListener mListener;
	
	private Calendar calendar;
	
	public TimePickerDialogFragment(Calendar calendar) {
		this.calendar = calendar;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
		
		return new TimePickerDialog(getActivity(), this, h, m, false);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (TimePickerDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
                    + " must implement TimePickerDialogListener");
		}
	}

	@Override
	public void onTimeSet(TimePicker view, int hour, int minute) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		mListener.onTimeSet(this, calendar);
	}

}