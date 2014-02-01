package com.jgrue.rfgeneration;

import static android.provider.BaseColumns._ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.server.status.AnimatedImageView;
import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.data.RFGenerationData;
import com.jgrue.rfgeneration.data.RFGenerationProvider;
import com.jgrue.rfgeneration.objects.Collection;
import com.jgrue.rfgeneration.objects.Folder;
import com.jgrue.rfgeneration.objects.GameInfo;
import com.jgrue.rfgeneration.scrapers.AddGameScraper;
import com.jgrue.rfgeneration.scrapers.DeleteGameScraper;
import com.jgrue.rfgeneration.scrapers.GameInfoScraper;
import com.quietlycoding.android.picker.NumberPicker;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class GameInfoActivity extends ActionBarActivity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "GameInfoActivity";
	private RFGenerationData rfgData;
	private GameInfo gameInfo;
	private List<Collection> collections;
	private Pattern variantRegex;
	private final String[] infoOrder = new String[] { GameInfo.ALTERNATE_TITLE, GameInfo.CONSOLE, GameInfo.REGION, GameInfo.YEAR,
			GameInfo.RFGID, GameInfo.PART_NUMBER, GameInfo.UPC, GameInfo.PUBLISHER, GameInfo.DEVELOPER, GameInfo.RATING,
			GameInfo.GENRE, GameInfo.SUB_GENRE, GameInfo.PLAYERS, GameInfo.CONTROL_SCHEME, GameInfo.MEDIA_FORMAT,
			GameInfo.MANUFACTURER, GameInfo.CLASS, GameInfo.SUBCLASS };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.gamedetail);
        
        final ActionBar actionBar = getSupportActionBar();
        setSupportProgressBarIndeterminateVisibility(true);
		
		rfgData = new RFGenerationData(this);
		variantRegex = Pattern.compile(" \\[.*\\]$");
		gameInfo = new GameInfo();
		
		String gameId = Long.toString(getIntent().getLongExtra(Constants.INTENT_GAME_ID, 0));
		String rfgId = getIntent().getStringExtra(Constants.INTENT_GAME_RFGID);
		if (gameId.equals("0"))
			gameId = rfgId;
		
		// Get the information about the game from the local database.
		ContentResolver db = getContentResolver();
		Cursor gameInfoCursor = db.query(Uri.withAppendedPath(RFGenerationProvider.GAMES_URI, gameId), 
				new String[] { "rfgid", "console_name", "region", "title", "publisher", "year", "genre", "type", _ID }, 
				null, null, null);
		startManagingCursor(gameInfoCursor);
		
		if (gameInfoCursor.moveToNext()) {
			// Read the data into an object.
			Log.d(TAG, "Setting gameInfo by ID.");
			
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
			
			getSupportLoaderManager().initLoader(((Long)gameInfoCursor.getLong(8)).intValue(), null, this);
		} else {
			// If we were passed an RFGID but not a game_id, set everything by intent.
			Log.d(TAG, "Setting gameInfo via Intent.");
			
			gameInfo.setRFGID(rfgId);
			gameInfo.setConsole(getIntent().getStringExtra(Constants.INTENT_GAME_CONSOLE));
			gameInfo.setRegion(getIntent().getStringExtra(Constants.INTENT_GAME_REGION));
			gameInfo.setType(getIntent().getStringExtra(Constants.INTENT_GAME_TYPE));
			gameInfo.setTitle(getIntent().getStringExtra(Constants.INTENT_GAME_TITLE));
			gameInfo.setPublisher(getIntent().getStringExtra(Constants.INTENT_GAME_PUBLISHER));
			gameInfo.setYear(getIntent().getIntExtra(Constants.INTENT_GAME_YEAR, 0));
			gameInfo.setGenre(getIntent().getStringExtra(Constants.INTENT_GAME_GENRE));
			
			displayGameInfo();
		}
		
		// Load everything else in the background.
		new GameInfoTask().execute(rfgId);
		
		findViewById(R.id.add_game_button).setOnClickListener(this);
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
    	String publisher = gameInfo.getPublisher() != null ? gameInfo.getPublisher() : "";
    	if(matcher.find()) {
    		variationTitle = matcher.group().substring(2, matcher.group().length() - 1);
    		mainTitle = gameInfo.getTitle().substring(0, gameInfo.getTitle().length() - variationTitle.length() - 3);
    	}
    	
    	// Display the basic info.
    	getSupportActionBar().setTitle(gameInfo.getTitle());
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append(variationTitle);
    	if (sb.length() > 0 && publisher.length() > 0)
    		sb.append(", ");
    	sb.append(publisher);
    	if (sb.length() > 0 && publisher.length() > 0 && gameInfo.getYear() > 0)
    		sb.append(", ");
    	if(gameInfo.getYear() > 0)
    		sb.append(gameInfo.getYear());
    	if(gameInfo.getType() != null && !gameInfo.getType().equals("S"))
    		sb.insert(0, "[" + gameInfo.getType() + "] ");
    	if(sb.toString().length() > 0) {
    		getSupportActionBar().setSubtitle(sb.toString());
    	}
    	
    	// Populate the "My Folders" section.
    	TableLayout folderTable = (TableLayout)findViewById(R.id.game_folder_table);
    	if(folderTable.getChildCount() > 0)
        	folderTable.removeViews(0, folderTable.getChildCount());
    	
    	for(Collection c : gameInfo.getCollections()) {
    		TableRow folderRow = new TableRow(this);
        	folderRow.setLayoutParams(new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        	int paddingUnit = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        	
        	// Create the layout that will hold everything.
        	LinearLayout rowLayout = new LinearLayout(this);
        	rowLayout.setLayoutParams(new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        	rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        	rowLayout.setOnClickListener(this);
        	
        	// Create the folder image.
        	ImageView folderImage = new ImageView(this);
        	if(c.getFolder().isForSale())
        		folderImage.setImageResource(R.drawable.folder_for_sale);
        	else if(c.getFolder().isPrivate())
        		folderImage.setImageResource(R.drawable.folder_private);
        	else
        		folderImage.setImageResource(R.drawable.folder);
        	TableRow.LayoutParams folderImageLayout = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        	folderImageLayout.setMargins(paddingUnit, 0, paddingUnit, 0);
        	folderImageLayout.gravity = Gravity.CENTER_VERTICAL;
        	folderImage.setLayoutParams(folderImageLayout);
        	rowLayout.addView(folderImage);
        	
        	// No timestamp = virtual folder.
    		TextView folderText = new TextView(this);
    		folderText.setLayoutParams(new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
    		folderText.setText(c.getFolder().getName());
    		folderText.setTextColor(getResources().getColor(R.drawable.text));
    		rowLayout.addView(folderText);
    		
    		TextView folderQty = new TextView(this);
	    	folderQty.setText(Html.fromHtml("<font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.qty)).substring(2) + "\">" + 
	    			"G:" + Collection.getQuantityString(c.getGameQuantity()) + "</font> <font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.box)).substring(2) + "\">" + 
	    			"B:" + Collection.getQuantityString(c.getBoxQuantity()) + "</font> <font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.man)).substring(2) + "\">" +
	    			"M:" + Collection.getQuantityString(c.getManualQuantity()) + "</font> "));
	    	folderQty.setTextColor(getResources().getColor(R.drawable.text));
	    	TableRow.LayoutParams folderQtyLayout = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	    	folderQtyLayout.setMargins(0, 0, paddingUnit * 2, 0);
	    	folderQtyLayout.gravity = Gravity.CENTER_VERTICAL;
	    	folderQty.setLayoutParams(folderQtyLayout);
	    	rowLayout.addView(folderQty);
	    	
        	// Create the edit image.
	    	/* Save this for a later release, will need to write a parser to load in rating/comments.
        	ImageView editImage = new ImageView(this);
        	editImage.setImageResource(R.drawable.pencil);
        	TableRow.LayoutParams editImageLayout = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        	editImageLayout.setMargins(paddingUnit, 0, paddingUnit, 0);
        	editImageLayout.gravity = Gravity.CENTER_VERTICAL;
        	editImage.setLayoutParams(editImageLayout);
        	editImage.setClickable(true);
        	editImage.setId((gameInfo.getCollections().indexOf(c) * 10) + 101);
        	editImage.setOnClickListener(this);
        	rowLayout.addView(editImage);
        	*/
        	
	    	// Create the delete image.
        	ImageView deleteImage = new ImageView(this);
        	deleteImage.setImageResource(R.drawable.delete);
        	TableRow.LayoutParams deleteImageLayout = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        	deleteImageLayout.setMargins(paddingUnit, 0, paddingUnit, 0);
        	deleteImageLayout.gravity = Gravity.CENTER_VERTICAL;
        	deleteImage.setLayoutParams(deleteImageLayout);
        	deleteImage.setClickable(true);
        	deleteImage.setId((gameInfo.getCollections().indexOf(c) * 10) + 102);
        	deleteImage.setOnClickListener(this);
        	rowLayout.addView(deleteImage);
    		
    		// Put a little extra padding on single line rows.
    		TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    		rowLayoutParams.setMargins(0, paddingUnit, 0, paddingUnit);
			rowLayout.setLayoutParams(rowLayoutParams);
			
			folderRow.addView(rowLayout);
			folderTable.addView(folderRow);
    	}
    	    	
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
    	
    	if("H".equals(gameInfo.getType())) {
    		findViewById(R.id.game_game_button).setVisibility(View.GONE);
    		((TextView)findViewById(R.id.game_screenshot_button)).setText("Hardware");
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
		row.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TextView key = new TextView(GameInfoActivity.this);
		key.setText(detail + ':');
		key.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		key.setTextColor(getResources().getColor(R.drawable.splash));
		key.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		key.setPadding(0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0);
		row.addView(key);
		
		if(detail.equals(GameInfo.REGION)) {
			LinearLayout ll = new LinearLayout(GameInfoActivity.this);
			if(gameInfo.getRegion().indexOf("-") == -1 && gameInfo.getRegion().indexOf(",") == -1) {
        		ImageView region = new ImageView(GameInfoActivity.this);
        		region.setImageDrawable(gameInfo.getRegionDrawable(GameInfoActivity.this));
        		region.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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
			value.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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
				List<Collection> collections = gameInfo.getCollections();
				gameInfo = newInfo;
				gameInfo.setCollections(collections);
				displayGameInfo();
			}
			
			setSupportProgressBarIndeterminateVisibility(false);
		}
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.game_front_button || v.getId() == R.id.game_back_button ||
				v.getId() == R.id.game_screenshot_button || v.getId() == R.id.game_game_button ||
				v.getId() == R.id.game_manual_button) {
			String title = getSupportActionBar().getTitle().toString();
			String subTitle = getSupportActionBar().getSubtitle().toString();
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
			
			if(type != null && !type.equals("ss")) {
				Intent myIntent = new Intent(GameInfoActivity.this, WebViewActivity.class);
				myIntent.putExtra(Constants.INTENT_WEB_TITLE, title);
				myIntent.putExtra(Constants.INTENT_WEB_SUBTITLE, subTitle);
				myIntent.putExtra(Constants.INTENT_WEB_URL, Constants.FUNCTION_IMAGE + folder + "/" + type + "/" + rfgId + ".jpg");
		        startActivityForResult(myIntent, 0);
			} else if (type.equals("ss")) {
				Intent myIntent = new Intent(GameInfoActivity.this, WebViewActivity.class);
				myIntent.putExtra(Constants.INTENT_WEB_TITLE, title);
				myIntent.putExtra(Constants.INTENT_WEB_SUBTITLE, subTitle);
				myIntent.putExtra(Constants.INTENT_WEB_URL, Constants.FUNCTION_SCREENSHOT + "?" + Constants.PARAM_RFGID + "=" + rfgId);
		        startActivityForResult(myIntent, 0);
			}
		} else if(v.getId() == R.id.add_game_button) {
			Log.v(TAG, "Clicked add game button.");
			
			// Get the layout to insert in the pop-up.
			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View addGameView = vi.inflate(R.layout.add_game, null);
			
			// Get the list of all available folders and set it to display in the spinner.
			ContentResolver db = getContentResolver();
			Cursor folderCursor = db.query(Uri.withAppendedPath(RFGenerationProvider.FOLDERS_URI, "all"), 
					new String[] { _ID, "folder_name" }, null, null, "folder_name ASC");
			startManagingCursor(folderCursor);
			SimpleCursorAdapter folderAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
					folderCursor, new String[] { "folder_name" }, new int[] { android.R.id.text1 }, 0);
			folderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			final Spinner folderSpinner = (Spinner) addGameView.findViewById(R.id.add_to_folder_spinner);
			folderSpinner.setAdapter(folderAdapter);
			
			// Set up all the number pickers.
			final NumberPicker gamePicker = (NumberPicker) addGameView.findViewById(R.id.game_quantity_picker);
			gamePicker.setRange(0, 999);
			gamePicker.setCurrent(0);
			
			final NumberPicker boxPicker = (NumberPicker) addGameView.findViewById(R.id.box_quantity_picker);
			boxPicker.setRange(0, 999);
			boxPicker.setCurrent(0);
			
			final NumberPicker manualPicker = (NumberPicker) addGameView.findViewById(R.id.manual_quantity_picker);
			manualPicker.setRange(0, 999);
			manualPicker.setCurrent(0);
			
			// Show the pop-up.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(addGameView)
				.setTitle("Add to Collection")
				.setPositiveButton("Add Game", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Collection c = new Collection();
						c.setGameQuantity(gamePicker.getCurrent());
						c.setBoxQuantity(boxPicker.getCurrent());
						c.setManualQuantity(manualPicker.getCurrent());
						c.setFolder(new Folder(((TextView)folderSpinner.getSelectedView()).getText().toString()));
						
						Cursor cursor = ((SimpleCursorAdapter)folderSpinner.getAdapter()).getCursor();
						cursor.moveToPosition(folderSpinner.getSelectedItemPosition());
						c.getFolder().setId(cursor.getLong(0));
						
						new AddGameTask().execute(c);
						dialog.cancel();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
			
			builder.create().show();
			
		} else if(v.getId() >= 100) {
			int buttonType = v.getId() % 10;
			final int index = (v.getId() - 100) / 10;
			Log.v(TAG, "Clicked folder " + collections.get(index).getFolder().toString() + ", button " + buttonType);
			
			if(buttonType == 2) { // Delete
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Are you sure you want to remove this game from " + collections.get(index).getFolder().getName() + "?")
				       .setCancelable(false)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           @Override
						public void onClick(DialogInterface dialog, int id) {
				                new DeleteGameTask().execute(gameInfo.getRFGID(), collections.get(index).getFolder().getName(),
				                		Long.toString(gameInfo.getId()), Long.toString(collections.get(index).getFolder().getId()));
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           @Override
						public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}
	
	private class AddGameTask extends AsyncTask<Collection, Void, Long> {

		@Override
		protected Long doInBackground(Collection... params) {
			if (AddGameScraper.addGame(GameInfoActivity.this, gameInfo.getRFGID(), params[0].getFolder().getName(),
					params[0].getGameQuantity(), params[0].getBoxQuantity(), params[0].getManualQuantity())) {
				// Add the game to the local database as well.
                ContentValues gameInsert = new ContentValues();
                
                // "games" table values.
                gameInsert.put("rfgid", gameInfo.getRFGID());
                gameInsert.put("console_id", gameInfo.getConsoleId());
                gameInsert.put("console_name", gameInfo.getConsole());
                gameInsert.put("region_id", gameInfo.getRegionId());
                gameInsert.put("region", gameInfo.getRegion().replaceAll(",+$", ""));
                gameInsert.put("type", gameInfo.getType());
                gameInsert.put("title", gameInfo.getTitle());
                gameInsert.put("publisher", gameInfo.getPublisher());
                if(gameInfo.getYear() > 0)
                	gameInsert.put("year", gameInfo.getYear());
                gameInsert.put("genre", gameInfo.getGenre());
                
                Uri game = GameInfoActivity.this.getContentResolver().insert(RFGenerationProvider.GAMES_URI, gameInsert);
                Long gameId = Long.parseLong(game.getLastPathSegment());
                gameInfo.setId(gameId);
                
                //GameInfoActivity.this.getSupportLoaderManager().initLoader(gameId.intValue(), null, GameInfoActivity.this);
                
                // Add game to collection
                ContentValues quantities = new ContentValues();
                quantities.put("qty", params[0].getGameQuantity());
    			quantities.put("box", params[0].getBoxQuantity());
    			quantities.put("man", params[0].getManualQuantity());
    			quantities.put("folder_id", params[0].getFolder().getId());
				quantities.put("game_id", gameId);
				
				Uri collection = GameInfoActivity.this.getContentResolver().insert(RFGenerationProvider.COLLECTION_URI, quantities);
                
				return gameId;
			}
			
			return 0L;
		}
		
		@Override
		protected void onPostExecute(Long gameId) {
			if(gameId > 0)
			{
				GameInfoActivity.this.getSupportLoaderManager().initLoader(gameId.intValue(), null, GameInfoActivity.this);
				Toast.makeText(GameInfoActivity.this, "Game Added", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class DeleteGameTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			if (DeleteGameScraper.deleteGame(GameInfoActivity.this, params[0], params[1])) {
				int numRows = GameInfoActivity.this.getContentResolver().delete(
						Uri.withAppendedPath(RFGenerationProvider.FOLDERS_URI, params[3] + "/games/" + params[0]), null, null);
				return numRows > 0;
			}
			
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean gameAdded) {
			Toast.makeText(GameInfoActivity.this, "Game Deleted", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(this, Uri.withAppendedPath(RFGenerationProvider.COLLECTION_URI, "games/" + Integer.toString(arg0)),
				new String[] { "folder_name", "is_owned", "is_for_sale", "is_private", "qty", "box", "man", "folder_id" },
				null, null, "is_owned DESC, folder_name ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor collectionInfoCursor) {
		// Read the data into a list.
		collections = new ArrayList<Collection>();
		while (collectionInfoCursor.moveToNext()) {
			Folder newFolder = new Folder(collectionInfoCursor.getString(0));
			newFolder.setId(collectionInfoCursor.getLong(7));
			newFolder.setOwned(collectionInfoCursor.getInt(1) > 0);
			newFolder.setForSale(collectionInfoCursor.getInt(2) > 0);
			newFolder.setPrivate(collectionInfoCursor.getInt(3) > 0);
			
			Collection newCollection = new Collection();
			newCollection.setFolder(newFolder);
			newCollection.setGameQuantity(collectionInfoCursor.getFloat(4));
			newCollection.setBoxQuantity(collectionInfoCursor.getFloat(5));
			newCollection.setManualQuantity(collectionInfoCursor.getFloat(6));
			
			collections.add(newCollection);
		}
		
		gameInfo.setCollections(collections);
		
		displayGameInfo();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// I don't think I have to do anything here, do I?
	}
	
	public void populateFolderTable(Cursor cursor) {
		
	}
}
