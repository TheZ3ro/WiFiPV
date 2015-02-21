/*
 * 
 * Hello Bro!
 * 
 * If you are here, I suppose you are very curious. Nice. :)
 * I've decide to made this very simple application for understand how
 * Android and Superuser work.
 *
 * I know, there are many hole and bad choice in my code,
 * so feel free to contribute on my Repository on Github
 * [https://github.com/TheZ3ro].
 * 
 * Good night, Sith. :)
 *
 * :42:
 * 
 * Author: Davide [@Th3Zer0] Silvetti (silvetti.davide@gmail.com)
 * 
 * Released under "Do What The Fuck You Want" License.
 * 
 */

package org.thezero.wifipv;

import org.thezero.wifipv.list.Entry;
import org.thezero.wifipv.list.EntryAdapter;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import eu.chainfire.libsuperuser.Shell;
import android.app.ActionBar;

public class MainActivity extends ActionBarActivity {
	
	private ListView listView;
	private List<Entry> entry;
	public static boolean backup = false;
	public static int reload=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Let's do some background stuff
		entry = new ArrayList<Entry>();
		listView = (ListView)findViewById(R.id.lista_domande);
		
		(new Startup()).setContext(this).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_about);
		TextView content = (TextView)dialog.findViewById(R.id.dialogContent);
		content.setLinksClickable(true);
		content.setLinkTextColor(Color.BLUE);
				
		((Button)dialog.findViewById(R.id.btnCloseDialog)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {dialog.dismiss();}
			
		});
		
		switch (id) {
			case R.id.action_about:
				dialog.setTitle(getString(R.string.about_title));
				content.setText(getString(R.string.about_content));
				dialog.show();
				return true;
		}
		return false;
	}


	private class Startup extends AsyncTask<Void, Void, Void> {
		private ProgressDialog dialog = null;
		private Context context = null;
		private boolean suAvailable = false;
		private List<String> suResult = null;
		
		public Startup setContext(Context context) {
			this.context = context;
			return this;
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setMessage(getString(R.string.dialog_msg));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Let's do some SU stuff
			suAvailable = Shell.SU.available();
			if (suAvailable) {
				suResult = Shell.SU.run("cat /data/misc/wifi/wpa_supplicant.conf");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();			
			
			if(suAvailable) {
				if (suResult != null) {
					String ssid = null, psk = null, kt = null;
					Boolean d=false;
					for (int i=0;i<suResult.size();i++){
						String line = suResult.get(i);
						// Motherfucker, wpa_supplicant.conf isn't json!
						if(line.startsWith("network={")) {
							d=true;
						} else if(line.startsWith("\tssid=") && d==true){
							ssid=line.split("=")[1];
							ssid=ssid.substring(1, ssid.length()-1);
							Log.v("",ssid);
						} else if(line.startsWith("\tpsk=") && d==true){
							psk=line.split("=")[1];
							psk=psk.substring(1, psk.length()-1);
						} else if(line.startsWith("\tkey_mgmt=") && d==true){
							kt=line.split("=")[1];
						} else if(line.startsWith("}")){
							d=false;
							Entry test = new Entry(ssid,psk,kt);
							entry.add(test);
                            ssid = ""; psk = ""; kt = "";
						}
					}
					listView.setAdapter(new EntryAdapter(MainActivity.this, entry));
				}
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage(R.string.dialog_msg_nosu).setTitle(R.string.dialog_title_nosu);
				AlertDialog dialog = builder.create();
				dialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				dialog.show();
			}
		}		
	}
}