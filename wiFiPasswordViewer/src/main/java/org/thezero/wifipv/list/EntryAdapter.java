package org.thezero.wifipv.list;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class EntryAdapter extends ArrayAdapter<Entry> {
	public EntryAdapter(Context context, List<Entry> question) {
		super(context, 0, question);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntryView view;
		
		if(convertView != null && convertView instanceof EntryView) {
			view = (EntryView) convertView;
		} else {
			view = new EntryView(getContext());
		}
		
		view.update(getItem(position));
		return view;
	}
}