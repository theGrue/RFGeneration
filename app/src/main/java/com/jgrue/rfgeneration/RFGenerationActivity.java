package com.jgrue.rfgeneration;

import static android.provider.BaseColumns._ID;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.data.RFGenerationProvider;
import com.jgrue.rfgeneration.objects.Folder;
import com.jgrue.rfgeneration.service.RFGenerationService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class RFGenerationActivity extends ActionBarActivity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "RFGenerationActivity";
	private List<Folder> folderList;
	private int selectedFolder;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        
        // Set this to fix some weirdness with jsoup and cookies. Mmm, cookie soup.
        System.setProperty("http.keepAlive", "false"); 
        
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_FILE, 0);
        if(settings.getString(Constants.PREFS_COOKIE, "").equals("")) {
        	// If we don't have a cookie stored, throw to the Login screen.
	        Intent myIntent = new Intent(RFGenerationActivity.this, LoginActivity.class);
	        startActivityForResult(myIntent, 0);
	        finish();
	        return;
        } else if(!settings.getBoolean(Constants.PREFS_LOADCOMPLETE, false)) {
        	// If we have a cookie but the collection load hasn't finished, kick that off.
        	Log.v(TAG, "Starting initial collection load.");
			Intent intent = new Intent(RFGenerationActivity.this, RFGenerationService.class);
			intent.setData(Uri.parse("-1| "));
			startService(intent);
			
			// Make sure we don't get here again.
			SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS_FILE, 0).edit();
			editor.putBoolean(Constants.PREFS_LOADCOMPLETE, true);
			editor.commit();
        } 
        
        // We're still here! Let's get the collection home set up.
        getSupportActionBar().setSubtitle(settings.getString(Constants.PREFS_USERNAME, "") + "'s Collection");
        ((EditText)findViewById(R.id.quick_search_text)).setText(settings.getString(Constants.PREFS_LAST_SEARCH, ""));
        findViewById(R.id.quick_search_button).setOnClickListener(this);
        findViewById(R.id.barcode_reader_button).setOnClickListener(this);
        findViewById(R.id.logout_button).setOnClickListener(this);
                
        getSupportLoaderManager().initLoader(0, null, this);
    }
    
    public void populateFolderTable(Cursor cursor) {
        // Load the list of folders from the database and add them to the TableLayout.
        TableLayout folderTable = (TableLayout)findViewById(R.id.folder_table);
        if(folderTable.getChildCount() > 0)
        	folderTable.removeViews(0, folderTable.getChildCount());
        
        List<Folder> folderList = getFolderList(cursor);
        if (folderList.size() <= selectedFolder)
        	selectedFolder = 0;
        
        folderTable.addView(createFolderRow(folderList.get(0), 0, 0, selectedFolder == 0));
        folderTable.addView(createFolderRow(folderList.get(1), 1, 1, selectedFolder == 1));
        for(int i = 2; i < folderList.size(); i++)
        	folderTable.addView(createFolderRow(folderList.get(i), i, folderList.get(i).isOwned() ? 2 : 1, selectedFolder == i));
        
        setSupportProgressBarIndeterminateVisibility(false);
    }
    
    public List<Folder> getFolderList(Cursor cursor) {
    	folderList = new ArrayList<Folder>();
    	int totalCount = 0, ownedCount = 0;
    	
    	// Add the static folders to the top of the list.
    	folderList.add(new Folder(getResources().getString(R.string.all_folders_label)));
    	folderList.add(new Folder(getResources().getString(R.string.owned_folders_label)));
    	folderList.get(0).setId(-1);
		
    	cursor.moveToPosition(-1);
		while(cursor.moveToNext()) {
			// Create an object from this row.
			Folder newFolder = new Folder();
			newFolder.setId(cursor.getLong(0));
			newFolder.setName(cursor.getString(1));
			newFolder.setOwned(cursor.getInt(2) == 1);
			newFolder.setForSale(cursor.getInt(3) == 1);
			newFolder.setPrivate(cursor.getInt(4) == 1);
			newFolder.setTimestamp(cursor.getLong(5));
			newFolder.setQuantity(cursor.getInt(6));
			folderList.add(newFolder);
			
			// Update the total counts.
			totalCount += newFolder.getQuantity();
			if(newFolder.isOwned())
				ownedCount += newFolder.getQuantity();
		}
		
		// Set quantities for the virtual folders at the top
		folderList.get(0).setQuantity(totalCount);
		folderList.get(1).setQuantity(ownedCount);
    	
    	return folderList;
    }
    
    public TableRow createFolderRow(Folder folder, int index, int depth, boolean selected) {
    	TableRow folderRow = new TableRow(this);
    	folderRow.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    	int paddingUnit = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
    	
    	// Create the layout that will hold everything.
    	LinearLayout rowLayout = new LinearLayout(this);
    	rowLayout.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    	rowLayout.setOrientation(LinearLayout.HORIZONTAL);
    	rowLayout.setId((index * 10) + 100);
    	rowLayout.setClickable(true);
    	rowLayout.setOnClickListener(this);
    	
    	// Create the folder image, setting the margin based on depth.
    	ImageView folderImage = new ImageView(this);
    	if(folder.isForSale())
    		folderImage.setImageResource(R.drawable.folder_for_sale);
    	else if(folder.isPrivate())
    		folderImage.setImageResource(R.drawable.folder_private);
    	else
    		folderImage.setImageResource(R.drawable.folder);
    	LayoutParams folderImageLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    	folderImageLayout.setMargins((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5 + (depth * 20), getResources().getDisplayMetrics()), 0, paddingUnit, 0);
    	folderImageLayout.gravity = Gravity.CENTER_VERTICAL;
    	folderImage.setLayoutParams(folderImageLayout);
    	rowLayout.addView(folderImage);
    	
    	// Check whether this folder has a timestamp on it.
    	if(folder.getTimestamp() > 0) {
    		// Timestamp means this is a synced folder, display some extra info.
    		LinearLayout nameLayout = new LinearLayout(this);
    		nameLayout.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    		nameLayout.setOrientation(LinearLayout.VERTICAL);
    		
    		TextView folderNameText = new TextView(this);
    		folderNameText.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    		folderNameText.setText(Html.fromHtml(folder.getName() + " <font color=\"#" + 
    				Integer.toHexString(getResources().getColor(R.drawable.textlight)).substring(2) + 
    				"\"> (" + folder.getQuantity() + ")</font>"));
    		folderNameText.setTextColor(getResources().getColor(R.drawable.text));
    		nameLayout.addView(folderNameText);
    		
    		TextView folderSyncText = new TextView(this);
    		folderSyncText.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    		folderSyncText.setText("Last synced on " +
    				DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(folder.getTimestamp() * 1000L)) + " at " +
    				DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(folder.getTimestamp() * 1000L)));
    		folderSyncText.setTextColor(getResources().getColor(R.drawable.text));
    		folderSyncText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
    		folderSyncText.setTypeface(null, Typeface.ITALIC);
    		nameLayout.addView(folderSyncText);
    		
    		rowLayout.addView(nameLayout);
    	} else {
    		// No timestamp = virtual folder.
    		TextView folderText = new TextView(this);
    		folderText.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
    		folderText.setText(Html.fromHtml(folder.getName() + " <font color=\"#" + 
    				Integer.toHexString(getResources().getColor(R.drawable.textlight)).substring(2) + 
    				"\"> (" + folder.getQuantity() + ")</font>"));
    		folderText.setTextColor(getResources().getColor(R.drawable.text));
    		rowLayout.addView(folderText);
    		
    		// Put a little extra padding on single line rows.
    		LayoutParams rowLayoutParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    		rowLayoutParams.setMargins(0, paddingUnit, 0, paddingUnit);
			rowLayout.setLayoutParams(rowLayoutParams);
    	}
    	
    	folderRow.addView(rowLayout);
    	
    	if(selected) {
    		selectedFolder = index;
    		
    		// Highlight the selected row with a special color.
    		folderRow.setBackgroundResource(R.drawable.selected);
    		
    		// Add magnifying glass and refresh icons.
    		ImageView refreshImage = new ImageView(this);
    		refreshImage.setImageResource(R.drawable.refresh);
    		LayoutParams refreshImageLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    		refreshImageLayout.setMargins(paddingUnit, 0, paddingUnit, 0);
    		refreshImageLayout.gravity = Gravity.CENTER_VERTICAL;
    		refreshImage.setLayoutParams(refreshImageLayout);
    		refreshImage.setId((index * 10) + 102);
    		refreshImage.setClickable(true);
    		refreshImage.setOnClickListener(this);
    		folderRow.addView(refreshImage);
    		
    		ImageView viewImage = new ImageView(this);
    		viewImage.setImageResource(R.drawable.view);
    		LayoutParams viewImageLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    		viewImageLayout.setMargins(paddingUnit, 0, paddingUnit, 0);
    		viewImageLayout.gravity = Gravity.CENTER_VERTICAL;
    		viewImage.setLayoutParams(viewImageLayout);
    		viewImage.setId((index * 10) + 101);
    		viewImage.setClickable(true);
    		viewImage.setOnClickListener(this);
    		folderRow.addView(viewImage);
    	}
    	
    	return folderRow;
    }
    
    public void selectFolder(int index) {
    	TableLayout folderTable = (TableLayout)findViewById(R.id.folder_table);
    	int paddingUnit = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
    	
    	// Deselect the currently selected row.
    	TableRow folderRow = (TableRow)folderTable.getChildAt(selectedFolder);
    	folderRow.setBackgroundResource(R.drawable.body);
    	folderRow.removeViews(1, 2);
    	
    	// Select the new row.
    	folderRow = (TableRow)folderTable.getChildAt(index);
    	folderRow.setBackgroundResource(R.drawable.selected);
    	
    	// Add magnifying glass and refresh icons.
		ImageView refreshImage = new ImageView(this);
		refreshImage.setImageResource(R.drawable.refresh);
		LayoutParams refreshImageLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		refreshImageLayout.setMargins(paddingUnit, 0, paddingUnit, 0);
		refreshImageLayout.gravity = Gravity.CENTER_VERTICAL;
		refreshImage.setLayoutParams(refreshImageLayout);
		refreshImage.setId((index * 10) + 102);
		refreshImage.setClickable(true);
		refreshImage.setOnClickListener(this);
		folderRow.addView(refreshImage);
    	
		ImageView viewImage = new ImageView(this);
		viewImage.setImageResource(R.drawable.view);
		LayoutParams viewImageLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		viewImageLayout.setMargins(paddingUnit, 0, paddingUnit, 0);
		viewImageLayout.gravity = Gravity.CENTER_VERTICAL;
		viewImage.setLayoutParams(viewImageLayout);
		viewImage.setId((index * 10) + 101);
		viewImage.setClickable(true);
		viewImage.setOnClickListener(this);
		folderRow.addView(viewImage);
		
		selectedFolder = index;
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.quick_search_button) {
			String searchGame = ((EditText)findViewById(R.id.quick_search_text)).getText().toString().trim();
			if(searchGame.length() == 0)
				return;
			
			SharedPreferences settings = getSharedPreferences(Constants.PREFS_FILE, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString(Constants.PREFS_LAST_SEARCH, searchGame);
		    editor.commit();
		    
			Intent myIntent = new Intent(RFGenerationActivity.this, SearchListActivity.class);
			myIntent.putExtra(Constants.INTENT_SEARCH, searchGame);
	        startActivityForResult(myIntent, 0);
		} else if(v.getId() == R.id.barcode_reader_button) {
			IntentIntegrator integrator = new IntentIntegrator(RFGenerationActivity.this);
			integrator.initiateScan();
		} else if(v.getId() == R.id.logout_button) {
			SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS_FILE, 0).edit();
	    	editor.putString(Constants.PREFS_COOKIE, "");
			editor.putBoolean(Constants.PREFS_LOADCOMPLETE, false);
			editor.commit();
			
			Intent myIntent = new Intent(RFGenerationActivity.this, LoginActivity.class);
	        startActivityForResult(myIntent, 0);
	        finish();
		} else if(v.getId() >= 100) {
			int buttonType = v.getId() % 10;
			int index = (v.getId() - 100) / 10;
			Log.v(TAG, "Clicked folder " + folderList.get(index).toString() + ", button " + buttonType);
			
			if (index != selectedFolder)
				selectFolder(index);
			else {
				if(buttonType == 0 || buttonType == 1) {
					Log.v(TAG, "Opening folder " + folderList.get(index).toString());
					Intent myIntent = new Intent(RFGenerationActivity.this, CollectionListActivity.class);
					myIntent.putExtra(Constants.INTENT_FOLDER, folderList.get(index).getId());
			        startActivityForResult(myIntent, 0);
				} else if (buttonType == 2) {
					Log.v(TAG, "Refreshing folder " + folderList.get(index).toString());
					setSupportProgressBarIndeterminateVisibility(true);
					Intent intent = new Intent(RFGenerationActivity.this, RFGenerationService.class);
					intent.setData(Uri.parse(Long.toString(folderList.get(index).getId()) + "|" + folderList.get(index).getName()));
					startService(intent);
				}
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		final String[] select = { _ID, "folder_name", "is_owned", "is_for_sale", "is_private", "last_load", 
    			"(SELECT Count(1) FROM collection WHERE collection.folder_id = folders." + _ID + ")" };
		
		return new CursorLoader(this, Uri.withAppendedPath(RFGenerationProvider.FOLDERS_URI, "all"),
    			select, null, null, "is_owned DESC, folder_name");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		populateFolderTable(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// I don't think I have to do anything here, do I?
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && resultCode != RESULT_CANCELED) {
			// handle scan result
			Intent myIntent = new Intent(RFGenerationActivity.this, SearchListActivity.class);
			myIntent.putExtra(Constants.INTENT_SEARCH, scanResult.getContents());
			myIntent.putExtra(Constants.INTENT_TYPE, "UPC");
	        startActivityForResult(myIntent, 0);
		}
	}
}
