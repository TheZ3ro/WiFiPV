package org.thezero.wifipv.list;

import org.thezero.wifipv.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryView extends LinearLayout {
	
	private TextView ssid;
	private TextView psk;
	private TextView kt;
	
	public EntryView(Context context) {
		super(context);
		
		inflateView(context);
	}

	private void inflateView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.entry_view, this);
		
		ssid = (TextView) findViewById(R.id.ssid);
		psk = (TextView) findViewById(R.id.psk);
		kt = (TextView) findViewById(R.id.keytype);
	}
	
	public void update(Entry e) {
		this.ssid.setText(e.getSSID());
		this.psk.setText(e.getPSK());
		this.kt.setText(e.getKeyType());
	}
}