package com.lazokin.socialeventplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MainSpinnerAdapter extends ArrayAdapter<String> {
	
	private Context context;
	private String[] options;

	public MainSpinnerAdapter(Context context, int resource, String[] options) {
		super(context, resource, options);
		this.context = context;
		this.options = options;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		// Inflate a new view when an old view is not being converted
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}
		
		TextView textView = (TextView)view.findViewById(android.R.id.text1);
		textView.setText(options[position]);
		
		return view;
	}

}
