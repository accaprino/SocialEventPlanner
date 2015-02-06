package com.lazokin.socialeventplanner.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class DeleteEventDialogFragment extends DialogFragment {
	
	public interface DeleteEventDialogListener {
        public void onDeleteEventDialogPositiveClick(DialogFragment dialog);
        public void onDeleteEventDialogNegativeClick(DialogFragment dialog);
    }
	
	DeleteEventDialogListener mListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return new AlertDialog.Builder(getActivity())
			.setMessage("Delete Event?")
			.setPositiveButton("Delete", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDeleteEventDialogPositiveClick(DeleteEventDialogFragment.this);
				}
			})
			.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDeleteEventDialogNegativeClick(DeleteEventDialogFragment.this);
				}
			})
			.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (DeleteEventDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
                    + " must implement DeleteEventDialogListener");
		}
		
		
	}
	
	

}
