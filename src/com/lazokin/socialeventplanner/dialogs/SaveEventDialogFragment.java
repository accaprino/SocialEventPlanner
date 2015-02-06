package com.lazokin.socialeventplanner.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class SaveEventDialogFragment extends DialogFragment {
	
	public interface SaveEventDialogListener {
        public void onSaveEventDialogPositiveClick(DialogFragment dialog);
        public void onSaveEventDialogNeutralClick(DialogFragment dialog);
        public void onSaveEventDialogNegativeClick(DialogFragment dialog);
    }
	
	SaveEventDialogListener mListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return new AlertDialog.Builder(getActivity())
			.setMessage("Save Changes?")
			.setPositiveButton("Save", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onSaveEventDialogPositiveClick(SaveEventDialogFragment.this);
				}
			})
			.setNeutralButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onSaveEventDialogNeutralClick(SaveEventDialogFragment.this);
				}
			})
			.setNegativeButton("Discard", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onSaveEventDialogNegativeClick(SaveEventDialogFragment.this);
				}
			})
			.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (SaveEventDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
                    + " must implement SaveEventDialogListener");
		}
		
		
	}
	
	

}
