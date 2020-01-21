package org.thezero.wifipv;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.thezero.wifipv.data.Password;
import org.thezero.wifipv.data.PasswordAdapter;
import eu.chainfire.libsuperuser.Shell;

public class RootActivity extends AppCompatActivity {
    private static PasswordAdapter passwordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        RecyclerView passList = findViewById(R.id.passwordList);
        passList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        passList.setLayoutManager(layoutManager);

        passwordAdapter = new PasswordAdapter(this, new ArrayList<Password>());
        passList.setAdapter(passwordAdapter);

       (new Startup()).setContext(this).execute();

    }

    private class Startup extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog dialog = null;
        private Context context = null;
        private SupplicantParser sup = null;
        private WifiParser wifi = null;

        Startup setContext(Context context) {
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
        protected Boolean doInBackground(Void... params) {
            // Let's do some SU stuff
            try {
                List<String> STDOUT = new ArrayList<>();
                List<String> STDERR = new ArrayList<>();

                int exitCode = Shell.Pool.SU.run("cat /data/misc/wifi/wpa_supplicant.conf", STDOUT, STDERR, true);
                if (exitCode != 0) {
                    return false;
                }
                sup = new SupplicantParser(STDOUT);

                exitCode = Shell.Pool.SU.run("cat /data/misc/wifi/WifiConfigStore.xml", STDOUT, STDERR, true);
                if (exitCode != 0) {
                    return false;
                }
                wifi = new WifiParser(STDOUT);
            } catch (Shell.ShellDiedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();

            if(result) {
                for (Password pass: sup.getPasswords()) {
                    passwordAdapter.add(pass);
                }
                for (Password pass: wifi.getPasswords()) {
                    passwordAdapter.add(pass);
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