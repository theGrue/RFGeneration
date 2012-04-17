package com.jgrue.rfgeneration;

import static android.provider.BaseColumns._ID;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.server.status.AnimatedImageView;
import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.data.RFGenerationData;
import com.jgrue.rfgeneration.data.RFGenerationProvider;
import com.jgrue.rfgeneration.objects.GameInfo;
import com.jgrue.rfgeneration.scrapers.GameInfoScraper;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GameInfoActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = "GameInfoActivity";
	private RFGenerationData rfgData;
	private GameInfo gameInfo;
	private Pattern variantRegex;
	private final String[] infoOrder = new String[] { GameInfo.ALTERNATE_TITLE, GameInfo.CONSOLE, GameInfo.REGION, GameInfo.YEAR,
			GameInfo.RFGID, GameInfo.PART_NUMBER, GameInfo.UPC, GameInfo.PUBLISHER, GameInfo.DEVELOPER, GameInfo.RATING,
			GameInfo.GENRE, GameInfo.SUB_GENRE, GameInfo.PLAYERS, GameInfo.CONTROL_SCHEME, GameInfo.MEDIA_FORMAT };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamedetail);
		
		rfgData = new RFGenerationData(this);
		variantRegex = Pattern.compile(" \\[.*\\]$");
		Long gameId = getIntent().getLongExtra(Constants.INTENT_GAME_ID, 0);
		String rfgId = getIntent().getStringExtra(Constants.INTENT_GAME_RFGID);
		
		// If we were passed an RFGID but not a game_id, look it up in the database.
		if(gameId == 0 && rfgId != null) {
			Log.i(TAG, "Only received RFGID, performing database lookup for _ID.");
			SQLiteDatabase db = rfgData.getReadableDatabase();
			Cursor gameIdCursor = db.query("games", new String[] { _ID }, "rfgid = ?", new String[] { rfgId }, null, null, null);
			startManagingCursor(gameIdCursor);
			if (gameIdCursor.moveToNext()) {
				gameId = gameIdCursor.getLong(0);
			}
		}
		
		// Get the information about the game from the local database.
		ContentResolver db = getContentResolver();
		Cursor gameInfoCursor = db.query(Uri.withAppendedPath(RFGenerationProvider.GAMES_URI, Long.toString(gameId)), 
				new String[] { "rfgid", "console_name", "region", "title", "publisher", "year", "genre", "type" }, 
				null, null, null);
		startManagingCursor(gameInfoCursor);
		
		// Read the data into an object.
		gameInfo = new GameInfo();
		if (gameInfoCursor.moveToNext()) {
			rfgId = gameInfoCursor.getString(0);
			gameInfo.setRFGID(gameInfoCursor.getString(0));
			gameInfo.setConsole(gameInfoCursor.getString(1));
			gameInfo.setRegion(gameInfoCursor.getString(2));
			gameInfo.setTitle(gameInfoCursor.getString(3));
			gameInfo.setPublisher(gameInfoCursor.getString(4));
			gameInfo.setYear(gameInfoCursor.getInt(5));
			gameInfo.setGenre(gameInfoCursor.getString(6));
			gameInfo.setType(gameInfoCursor.getString(7));
			
			displayGameInfo();
		}
		
		// Load everything else in the background.
		new GameInfoTask().execute(rfgId);
    }
    
    @Override
    public void onDestroy() {
    	if(rfgData != null)
    		rfgData.close();
    	super.onDestroy();
    }
    
    private void displayGameInfo() {
    	// Snip out any variation title.
    	Matcher matcher = variantRegex.matcher(gameInfo.getTitle());
    	String mainTitle = gameInfo.getTitle(), variationTitle = "";
    	if(matcher.find()) {
    		variationTitle = matcher.group().substring(2, matcher.group().length() - 1);
    		mainTitle = gameInfo.getTitle().substring(0, gameInfo.getTitle().length() - variationTitle.length() - 3);
    	}
    	
    	// Display the basic info.
    	((TextView)findViewById(R.id.game_header)).setText(gameInfo.getTitle() + " (" + gameInfo.getConsole() + ")");
    	((TextView)findViewById(R.id.game_title)).setText(mainTitle);
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append(variationTitle);
    	if (sb.length() > 0 && gameInfo.getPublisher().length() > 0)
    		sb.append(", ");
    	sb.append(gameInfo.getPublisher());
    	if (sb.length() > 0 && gameInfo.getPublisher().length() > 0 && gameInfo.getYear() > 0)
    		sb.append(", ");
    	if(gameInfo.getYear() > 0)
    		sb.append(gameInfo.getYear());
    	((TextView)findViewById(R.id.game_info)).setText(sb.toString());
    	
    	// Enable the image links.
    	for(String image : gameInfo.getImageTypes()) {
    		if(image.equals("bf"))
    			enableImageButton(R.id.game_front_button);
    		else if(image.equals("bb"))
    			enableImageButton(R.id.game_back_button);
    		else if(image.equals("ss"))
    			enableImageButton(R.id.game_screenshot_button);
    		else if(image.equals("gs"))
    			enableImageButton(R.id.game_game_button);
    		else if(image.equals("ms"))
    			enableImageButton(R.id.game_manual_button);
    	}
        
        // Display the extended info.
    	TableLayout detailTable = (TableLayout)findViewById(R.id.game_details_table);
    	if(detailTable.getChildCount() > 0)
    		detailTable.removeViews(0, detailTable.getChildCount());
    	Map<String, String> extendedInfo = new HashMap<String, String>(gameInfo.getExtendedInfo());
    	
    	// We always want to put these in the same order.
    	for(String key : infoOrder) {
	    	if(extendedInfo.containsKey(key))
	    	{
	    		detailTable.addView(createDetailRow(extendedInfo, key));
	    		extendedInfo.remove(key);
	    	}
    	}
    	
    	// If there's anything left in the collection, display it in whatever order.
    	for(String key : extendedInfo.keySet()) {
    		detailTable.addView(createDetailRow(extendedInfo, key));
    	}
    	
    	// Display the credits for this page.
    	LinearLayout ll = (LinearLayout)findViewById(R.id.game_credits_table);
    	if(ll.getChildCount() > 0)
    		ll.removeViews(0, ll.getChildCount());
    	for(int i = 0; i < gameInfo.getNameList().size() && i < gameInfo.getCreditList().size(); i++) {
	    	TextView name = new TextView(GameInfoActivity.this);
	    	name.setText(gameInfo.getNameList().get(i));
	    	name.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
	    	name.setTextColor(getResources().getColor(R.drawable.splash));
	    	name.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    	ll.addView(name);
	
	    	TextView credit = new TextView(GameInfoActivity.this);
	    	credit.setText(gameInfo.getCreditList().get(i));
	    	credit.setTextColor(getResources().getColor(R.drawable.text));
	    	credit.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    	credit.setPadding((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0, 0, 
	    			(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
	    	ll.addView(credit);
    	}
    }
    
    private TableRow createDetailRow(Map<String, String> extendedInfo, String detail) {
		TableRow row = new TableRow(GameInfoActivity.this);
		row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		TextView key = new TextView(GameInfoActivity.this);
		key.setText(detail + ':');
		key.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		key.setTextColor(getResources().getColor(R.drawable.splash));
		key.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		key.setPadding(0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0);
		row.addView(key);
		
		if(detail.equals(GameInfo.REGION)) {
			LinearLayout ll = new LinearLayout(GameInfoActivity.this);
			if(gameInfo.getRegion().indexOf("-") == -1 && gameInfo.getRegion().indexOf(",") == -1) {
        		ImageView region = new ImageView(GameInfoActivity.this);
        		region.setImageDrawable(gameInfo.getRegionDrawable(GameInfoActivity.this));
        		region.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        		ll.addView(region);
        	} else {                       		
        		AnimatedImageView regionAnim = new AnimatedImageView(GameInfoActivity.this);
        		regionAnim.setImageDrawable(gameInfo.getRegionAnimation(GameInfoActivity.this));
        		regionAnim.setLayoutParams(new LinearLayout.LayoutParams(
        				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 21, getResources().getDisplayMetrics()),
        				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics())));
        		ll.addView(regionAnim);
        	}
			row.addView(ll);
		} else {
			TextView value = new TextView(GameInfoActivity.this);
			value.setText(extendedInfo.get(detail));
			value.setTextColor(getResources().getColor(R.drawable.text));
			value.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
			row.addView(value);
		}
		
		return row;
    }
    
    private void enableImageButton(int id) {
    	TextView imageButton = (TextView)findViewById(id);
    	imageButton.setBackgroundColor(getResources().getColor(R.drawable.splash));
    	imageButton.setTextColor(getResources().getColor(R.drawable.body));
    	imageButton.setClickable(true);
    	imageButton.setOnClickListener(this);
    }
    
    private class GameInfoTask extends AsyncTask<String, Void, GameInfo> {

		@Override
		protected GameInfo doInBackground(String... params) {
			GameInfo gameInfo = null;
			
			try {
				gameInfo = GameInfoScraper.getGameInfo(params[0]);
			} catch (Exception e) { }
			
			return gameInfo;
		}
		
		@Override
		protected void onPostExecute(GameInfo newInfo) {
			if(newInfo != null)
			{
				gameInfo = newInfo;
				displayGameInfo();
			}
			
			findViewById(R.id.game_progress).setVisibility(View.GONE);
		}
    }

	@Override
	public void onClick(View v) {
		String title = ((TextView)findViewById(R.id.game_header)).getText().toString();
		String rfgId = gameInfo.getRFGID();
		String folder = rfgId.substring(0, 5);
		String type = null;

		if(v.getId() == R.id.game_front_button)
			type = "bf";
		else if (v.getId() == R.id.game_back_button)
			type = "bb";
		else if (v.getId() == R.id.game_screenshot_button)
			type = "ss";
		else if (v.getId() == R.id.game_game_button)
			type = "gs";
		else if (v.getId() == R.id.game_manual_button)
			type = "ms";
		
		if(type != null) {
			Intent myIntent = new Intent(GameInfoActivity.this, WebViewActivity.class);
			myIntent.putExtra(Constants.INTENT_WEB_TITLE, title);
			myIntent.putExtra(Constants.INTENT_WEB_URL, Constants.FUNCTION_IMAGE + folder + "/" + type + "/" + rfgId + ".jpg");
	        startActivityForResult(myIntent, 0);
		}
	}
}
