package com.lazokin.socialeventplanner.dialogs;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerDialogFragment extends DialogFragment implements
		OnDateSetListener {
	
	public interface DatePickerDialogListener {
        public void onDateSet(DialogFragment dialog, Calendar calendar);
    }
	
	private DatePickerDialogListener mListener;
	
	private Calendar calendar;
	
	public DatePickerDialogFragment(Calendar calendar) {
		this.calendar = calendar;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
		
		return new DatePickerDialog(getActivity(), this, y, m, d);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (DatePickerDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
                    + " must implement DatePickerDialogListener");
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		mListener.onDateSet(this, calendar);
	}

}