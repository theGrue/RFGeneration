package com.jgrue.rfgeneration;

import static android.provider.BaseColumns._ID;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.androidquery.AQuery;
import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.data.RFGenerationData;
import com.jgrue.rfgeneration.data.RFGenerationProvider;
import com.jgrue.rfgeneration.objects.GameInfo;
import com.jgrue.rfgeneration.scrapers.GameInfoScraper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RandomGameActivity extends FragmentActivity {
	private static final String TAG = "RandomGameActivity";
	private List<Long> folderIdList;
	private List<String> folderList;
	private RFGenerationData rfgData;
	private AQuery aq;
	private long folderId = -1;
	private Pattern variantRegex;
	
	  /* put this into your activity class */
	  private SensorManager mSensorManager;
	  private float mAccel; // acceleration apart from gravity
	  private float mAccelCurrent; // current acceleration including gravity
	  private float mAccelLast; // last acceleration including gravity

	  private final SensorEventListener mSensorListener = new SensorEventListener() {

	    public void onSensorChanged(SensorEvent se) {
	      float x = se.values[0];
	      float y = se.values[1];
	      float z = se.values[2];
	      mAccelLast = mAccelCurrent;
	      mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
	      float delta = mAccelCurrent - mAccelLast;
	      mAccel = mAccel * 0.9f + delta; // perform low-cut filter
	      
	      if (mAccel > 3)
	    	  Toast.makeText(RandomGameActivity.this, "Shake!", Toast.LENGTH_SHORT).show();
	    }

	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }
	  };
	  
	  @Override
	  protected void onResume() {
	    super.onResume();
	    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	  }

	  @Override
	  protected void onPause() {
	    mSensorManager.unregisterListener(mSensorListener);
	    super.onPause();
	  }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random);
        
        aq = new AQuery(this);
        folderIdList = new ArrayList<Long>();
        folderList = new ArrayList<String>();
        rfgData = new RFGenerationData(this);
        variantRegex = Pattern.compile(" \\[.*\\]$");
        
        populateFolderSpinner();
        selectRandomGame();
        
        /* do this in onCreate */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
		
        aq.id(R.id.random_progress).gone();
    }
    
    private void populateFolderSpinner() {
    	// Get information about folders.
        if (folderIdList.size() == 0) {
	        SQLiteDatabase db = rfgData.getReadableDatabase();
	        
			Cursor folderCursor = db.rawQuery("SELECT folders._id, folder_name, is_owned, count(folder_id) " +
					"FROM folders LEFT JOIN collection ON collection.folder_id = folders._id " + 
					"GROUP BY folder_name ORDER BY is_owned DESC, folder_name", null);
			startManagingCursor(folderCursor);
			
			long totalCount = 0, ownedCount = 0;
			folderIdList.add((long)-1); folderList.add("");
			folderIdList.add((long)0); folderList.add("");
			while(folderCursor.moveToNext()) {
				String folderName;
				
				totalCount += folderCursor.getLong(3);
				if(folderCursor.getInt(2) == 1)
				{
					ownedCount += folderCursor.getLong(3);
					folderName = "    ";
				}
				else
				{
					folderName = "  ";
				}
				
				folderName += folderCursor.getString(1) + " (" + Long.toString(folderCursor.getLong(3)) + ")";
				
				if (folderCursor.getLong(3) > 0)
				{
					folderIdList.add(folderCursor.getLong(0));
					folderList.add(folderName);
				}
			}
			
			folderList.set(0, "All Folders (" + totalCount + ")");
			folderList.set(1, "  Owned Folders (" + ownedCount + ")");
        }
        
        final Spinner folderSpinner = aq.id(R.id.random_spinner).getSpinner();
    	ArrayAdapter<String> folderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, folderList);
    	folderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	folderSpinner.setAdapter(folderAdapter);
    	folderSpinner.setSelection(folderIdList.indexOf(folderId));
    }
    
    private void selectRandomGame() {
    	Uri uri;
		if(folderId == -1)
			uri = Uri.withAppendedPath(RFGenerationProvider.COLLECTION_URI, "all");
		else if(folderId == 0)
			uri = Uri.withAppendedPath(RFGenerationProvider.COLLECTION_URI, "owned");
		else
			uri = Uri.withAppendedPath(RFGenerationProvider.COLLECTION_URI, Long.toString(folderId));
		
		ContentResolver db = getContentResolver();
		Cursor gameInfoCursor = db.query(uri, 
				new String[] { "rfgid", "games.console_name", "region", "title", "publisher", "year", "genre", "type" }, 
				null, null, "random() limit 1");
		startManagingCursor(gameInfoCursor);
		
		if (gameInfoCursor.moveToNext()) {
			Log.d(TAG, "Selected " + gameInfoCursor.getString(0));
			
			String title = gameInfoCursor.getString(3);
			Matcher matcher = variantRegex.matcher(title);
			if(matcher.find()) {
				String variationTitle = matcher.group().substring(2, matcher.group().length() - 1);
				String mainTitle = title.substring(0, title.length() - variationTitle.length() - 3);
				aq.id(R.id.random_game_name).text(Html.fromHtml("<b>" + mainTitle + "</b> <font color=\"#" +
						Integer.toHexString(getResources().getColor(R.drawable.textlight)).substring(2) +
						"\">[" + variationTitle + "]</font>"));
			} else {
				aq.id(R.id.random_game_name).text(Html.fromHtml("<b>" + title + "</b>"));
			}
			
        	if(gameInfoCursor.getInt(5) > 0 && !gameInfoCursor.getString(4).equals(""))
        		aq.id(R.id.random_game_publisher).text(gameInfoCursor.getString(4) + ", " + gameInfoCursor.getInt(5));
        	else if(gameInfoCursor.getInt(5) > 0)
        		aq.id(R.id.random_game_publisher).text(gameInfoCursor.getString(4) + "" + gameInfoCursor.getInt(5));
			
			aq.id(R.id.random_game_console).text(gameInfoCursor.getString(1));
			
			new GameInfoTask().execute(gameInfoCursor.getString(0));
		}
    }
    
    private class GameInfoTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			Uri url = Uri.parse(Constants.FUNCTION_GAME_INFO_API).buildUpon()
					.appendPath(params[0]).build();
			Log.i(TAG, "Target URL: " + url);
			
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url.toString());
            
            JSONObject gameInfo = null;
			
			try {
				HttpResponse response = client.execute(httpGet);
				//Log.i(TAG, "Retrieved URL: " + response.getHeaders("Location")[0].getValue());
				
			    if (response.getStatusLine().getStatusCode() == 200) {
			    	HttpEntity entity = response.getEntity();
			        InputStream content = entity.getContent();
			        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			        
			        String line;
			        while ((line = reader.readLine()) != null) {
			        	builder.append(line);
			        }
			        
					gameInfo = new JSONObject(builder.toString());
			    }
			} catch (Exception e) { 
				Log.e(TAG, e.getMessage());
			}
			
			return gameInfo;
		}
    	
		/*@Override
		protected void onPostExecute(JSONObject newInfo) {
			try {
				JSONArray images = newInfo.getJSONArray("images");
				if (images.length() > 0) {
					String imagePath = images.getJSONArray(0).getString(1);
					Log.i(TAG, "Selected image " + imagePath);
				}
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}*/
    }
}
