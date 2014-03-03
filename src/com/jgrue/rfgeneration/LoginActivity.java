package com.jgrue.rfgeneration;

import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.scrapers.LoginScraper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity implements OnClickListener {
	private static final String TAG = "LoginActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.login);
        
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("The Classic and Modern Gaming Databases");
        
        // Restore username from preferences if upgrading from v1.
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_FILE, 0);
        String userName = settings.getString(Constants.PREFS_USERNAME, "");
        ((EditText)findViewById(R.id.login_username)).setText(userName);
        
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.about_text).setOnClickListener(this);
        findViewById(R.id.register_text).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.login_button) {
			String userName = ((EditText)findViewById(R.id.login_username)).getText().toString().trim();
			String password = ((EditText)findViewById(R.id.login_password)).getText().toString().trim();
			
			if (userName.length() == 0 || password.length() == 0)
				return;
			
			// Store the username.
		    SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS_FILE, 0).edit();
		    editor.putString(Constants.PREFS_USERNAME, userName);
		    editor.commit();
			
		    // Try logging in with this information.
		    ((Button)v).setEnabled(false);
		    setSupportProgressBarIndeterminateVisibility(true);
			new LoginTask().execute(v.getContext(), userName, password);
		} else if(v.getId() == R.id.about_text) {
			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setTitle("What is RF Generation?");
			ad.setView(LayoutInflater.from(this).inflate(R.layout.about, null));
			ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				} 
			});
			ad.show();
		} else if(v.getId() == R.id.register_text) {
			Uri uri = Uri.parse("http://www.rfgeneration.com/forum/index.php?action=register");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}
	
	private class LoginTask extends AsyncTask<Object, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Object... arg0) {
			return LoginScraper.validateLogin((Context)arg0[0], (String)arg0[1], (String)arg0[2]);
		}
		
		@Override
		protected void onPostExecute(Boolean validLogin) {
			if(validLogin) {
				// Throw info to RFGenerationActivity.
				Intent myIntent = new Intent(LoginActivity.this, RFGenerationActivity.class);
				startActivityForResult(myIntent, 0);
		        finish();
			} else {
				// Invalid login, blank out the login cookie.
				((EditText)findViewById(R.id.login_password)).setText("");
				SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS_FILE, 0).edit();
				editor.putString(Constants.PREFS_COOKIE, "");
			    editor.commit();
			    
			    Toast.makeText(LoginActivity.this, "Login failed, please try again.", Toast.LENGTH_SHORT).show();
			    ((Button)findViewById(R.id.login_button)).setEnabled(true);
			    setSupportProgressBarIndeterminateVisibility(false);
			}
		}
	}
}