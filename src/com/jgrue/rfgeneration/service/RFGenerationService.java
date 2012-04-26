package com.jgrue.rfgeneration.service;

import static android.provider.BaseColumns._ID;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.data.RFGenerationProvider;
import com.jgrue.rfgeneration.objects.Console;
import com.jgrue.rfgeneration.objects.Folder;
import com.jgrue.rfgeneration.scrapers.FolderScraper;
import com.jgrue.rfgeneration.scrapers.LoginScraper;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

public class RFGenerationService extends Service {
	private static final String TAG = "RFGenerationService";
	private boolean loggedIn;
	private ExportScraperTask exportScraper;
	
    @Override
    public void onStart(Intent intent, int startId) {
        String folderIdName = intent.getDataString();
        if (folderIdName != null && (folderIdName.length() > 0)) {
        	String[] folderSplit = folderIdName.split("\\|");
            exportScraper = new ExportScraperTask();
            exportScraper.execute(folderSplit[0], folderSplit[1]);
        }
    }
	 
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate(){
		//loggedIn = LoginScraper.validateLogin(this);
		Log.v(TAG, "Service started.");
	}
	
	private class ExportScraperTask extends AsyncTask<String, Void, Boolean> {	 
        private static final String TAG = "RFGenerationService$ExportScraperTask";
 
        @Override
        protected Boolean doInBackground(String... params) {
        	boolean succeeded = false;
        	Long folderId = Long.parseLong(params[0]);
            String folderName = params[1];

            if (folderId > 0) {
                succeeded = getGameList(folderId, folderName);
            } else {
            	succeeded = getFolderList(folderId == 0);
            	
            	Uri uri;
            	if(folderId == 0)
            		uri = Uri.withAppendedPath(RFGenerationProvider.FOLDERS_URI, "owned");
            	else
            		uri = Uri.withAppendedPath(RFGenerationProvider.FOLDERS_URI, "all");
            	
            	Cursor folderCursor = getContentResolver().query(uri, new String[] { _ID, "folder_name" }, 
        			null, null, "is_owned DESC, folder_name ASC");
            	
            	try {
	            	while (folderCursor.moveToNext()) {
	            		succeeded &= getGameList(folderCursor.getLong(0), folderCursor.getString(1));
	            	}
            	} finally {
            		folderCursor.close();
            	}
            }
            
            return succeeded;
        }
        
        private boolean getFolderList(boolean ownedOnly) {
        	Context ctx = RFGenerationService.this.getApplicationContext();
        	List<Folder> folderList = FolderScraper.getCollectionFolders(ctx);
        	List<ContentValues> folderInsertList = new ArrayList<ContentValues>();
        	
        	for(Folder folder : folderList) {
        		if(!ownedOnly || (ownedOnly && folder.isOwned())) {
	        		// Create an object to hold all the values we need to insert.
	                ContentValues folderInsert = new ContentValues();
	                
	                // "folder" table values.
	                folderInsert.put("folder_name", folder.getName());
	                folderInsert.put("is_owned", folder.isOwned());
	                folderInsert.put("is_for_sale", folder.isForSale());
	                folderInsert.put("is_private", folder.isPrivate());
	                folderInsert.put("last_load", 0);
	                
	                folderInsertList.add(folderInsert);
                }
        	}
        	
        	// Insert or update the folders.
        	if(ownedOnly)
        		getContentResolver().bulkInsert(Uri.withAppendedPath(RFGenerationProvider.FOLDERS_URI, "owned"), 
        				folderInsertList.toArray(new ContentValues[]{}));
        	else
        		getContentResolver().bulkInsert(Uri.withAppendedPath(RFGenerationProvider.FOLDERS_URI, "all"), 
        				folderInsertList.toArray(new ContentValues[]{}));
        	
        	return true;
        }
 
        private boolean getGameList(long folderId, String folderName) {
        	Context ctx = RFGenerationService.this.getApplicationContext();
        	SharedPreferences settings = ctx.getSharedPreferences(Constants.PREFS_FILE, 0);
    		
    		try {
    			Connection.Response document = Jsoup.connect(Constants.FUNCTION_CSV + 
    					"&" + Constants.PARAM_FOLDER + "=" + URLEncoder.encode(folderName) + 
    					"&" + Constants.PARAM_USERNAME + "=" + settings.getString(Constants.PREFS_USERNAME, "") )
    				.cookie(Constants.LOGIN_COOKIE, LoginScraper.getCookie(ctx))
    				.timeout(Constants.TIMEOUT)
    				.execute();
    			
    			CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(document.bodyAsBytes())));
    			String[] nextLine = reader.readNext();
    			
    			// Empty folders return an HTML page instead of a CSV. If we don't get some columns, just get out of here.
    			if(nextLine.length <= 1)
    				return true;
    			
    			// Create some variables to use in the loop below.
    			int year = 0;
    			Console console = new Console();
    			String[] splitId;
    			List<ContentValues> gameList = new ArrayList<ContentValues>();
    			
    			while ((nextLine = reader.readNext()) != null) {
    				// Reset a few variables.
    				year = 0;
    				console.setId(0);
    				splitId = nextLine[0].split("-");
    				
    				// Parse out some special values.
    				console.setId(splitId[1]);
    				try { year = Integer.parseInt(nextLine[6]); } catch (Exception e) { }    
    				
    				// Make sure we got a valid console_id value. If not, skip this game.
    				// This should only happen if they add a new console with a non-numeric ID.
    				if(console.getId() == 0)
    					continue;
                    
                    // Create an object to hold all the values we need to insert.
                    ContentValues gameInsert = new ContentValues();
                    
                    // "games" table values.
                    gameInsert.put("rfgid", nextLine[0]);
                    gameInsert.put("console_id", console.getId());
                    gameInsert.put("console_name", nextLine[1]);
                    gameInsert.put("region_id", splitId[0]);
                    gameInsert.put("region", nextLine[2]);
                    gameInsert.put("type", nextLine[3]);
                    gameInsert.put("title", nextLine[4]);
                    gameInsert.put("publisher", nextLine[5]);
                    if(year > 0)
                    	gameInsert.put("year", year);
                    gameInsert.put("genre", nextLine[7]);
                    
                    // "collection" table values.
                    gameInsert.put("folder_name", folderName);
                    gameInsert.put("qty", Float.parseFloat(nextLine[8]));
                    gameInsert.put("box", Float.parseFloat(nextLine[9]));
                    gameInsert.put("man", Float.parseFloat(nextLine[10]));
                    
                    gameList.add(gameInsert);
                }
    			
    			getContentResolver().bulkInsert(Uri.withAppendedPath(RFGenerationProvider.COLLECTION_URI, Long.toString(folderId)), 
					gameList.toArray(new ContentValues[]{}));
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			return false;
    		}

        	return true;
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
        	stopSelf();
        }
    }
}
