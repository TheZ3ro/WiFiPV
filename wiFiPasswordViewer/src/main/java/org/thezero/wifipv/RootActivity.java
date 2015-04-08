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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jooik.tabbeddialog.fragments.FragmentDialog;

import org.thezero.wifipv.list.CardAdapter;
import org.thezero.wifipv.list.WiFiEntry;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class RootActivity extends ActionBarActivity {

	private List<WiFiEntry> entry = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private static CardAdapter cardAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_root);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text));
        toolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutParams);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        cardAdapter = new CardAdapter(this, entry);
        mRecyclerView.setAdapter(cardAdapter);

        // Let's do some background stuff
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

        switch (id) {
            case R.id.action_about:
                FragmentManager fm = getSupportFragmentManager();
                FragmentDialog overlay = new FragmentDialog();
                overlay.show(fm, "FragmentDialog");
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
					String ssid=null,psk=null,kt=null,idt=null;
					Boolean d=false;
					for (int i=0;i<suResult.size();i++){
						String line = suResult.get(i);
						// Motherfucker, wpa_supplicant.conf isn't json!
						if(line.startsWith("network={")) {
							d=true;
						} else if(line.startsWith("\tssid=") && d){
							ssid=line.split("=")[1];
							ssid=ssid.substring(1, ssid.length()-1);
							Log.v("",ssid);
						} else if(line.startsWith("\tpsk=") && d){
							psk=line.split("=")[1];
							psk=psk.substring(1, psk.length()-1);
						} else if(line.startsWith("\tkey_mgmt=") && d) {
                            kt = line.split("=")[1];
                        } else if(line.startsWith("\tidentity=") && d) {
                            idt=line.split("=")[1];
                            idt=idt.substring(1, idt.length()-1);
                        } else if(line.startsWith("\tpassword=") && d){
                            psk=line.split("=")[1];
                            psk=psk.substring(1, psk.length()-1);
						} else if(line.startsWith("}")) {
                            d = false;
							WiFiEntry test = new WiFiEntry(ssid,psk,kt,idt);
                            cardAdapter.add(test, cardAdapter.getItemCount());
                            mRecyclerView.scrollToPosition(cardAdapter.getItemCount());
                            ssid=null;psk=null;kt=null;idt=null;
						}
					}
				}
                Toast.makeText(this.context, getString(R.string.view_pass), Toast.LENGTH_LONG).show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(RootActivity.this);
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