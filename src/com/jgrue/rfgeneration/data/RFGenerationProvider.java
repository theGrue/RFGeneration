package com.jgrue.rfgeneration.data;

import static android.provider.BaseColumns._ID;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class RFGenerationProvider extends ContentProvider {
	private static final String TAG = "RFGenerationProvider";
	private static final String AUTHORITY = "com.jgrue.rfgeneration.data.RFGenerationProvider";
	private static final String COLLECTION_BASE_PATH = "collection";
	private static final String FOLDERS_BASE_PATH = "folders";
	private static final String GAMES_BASE_PATH = "games";
    public static final Uri COLLECTION_URI = Uri.parse("content://" + AUTHORITY + "/" + COLLECTION_BASE_PATH);
    public static final Uri FOLDERS_URI = Uri.parse("content://" + AUTHORITY + "/" + FOLDERS_BASE_PATH);
    public static final Uri GAMES_URI = Uri.parse("content://" + AUTHORITY + "/" + GAMES_BASE_PATH);
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, COLLECTION_BASE_PATH, 4);
        sURIMatcher.addURI(AUTHORITY, COLLECTION_BASE_PATH + "/all", 1);
        sURIMatcher.addURI(AUTHORITY, COLLECTION_BASE_PATH + "/owned", 2);
        sURIMatcher.addURI(AUTHORITY, COLLECTION_BASE_PATH + "/#", 3);
        sURIMatcher.addURI(AUTHORITY, FOLDERS_BASE_PATH + "/all", 5);
        sURIMatcher.addURI(AUTHORITY, FOLDERS_BASE_PATH + "/owned", 6);
        sURIMatcher.addURI(AUTHORITY, FOLDERS_BASE_PATH + "/#", 8);
        sURIMatcher.addURI(AUTHORITY, GAMES_BASE_PATH + "/#", 7);
    }
	
	private RFGenerationData rfgData;
	
	@Override
	public boolean onCreate() {
		rfgData = new RFGenerationData(getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		int uriType = sURIMatcher.match(uri);
		
	    switch (uriType) {
	    case 1: // Collection from All Folders
	    	queryBuilder.setTables("collection INNER JOIN games ON collection.game_id = games._id " +
	    			"LEFT JOIN consoles ON games.console_id = consoles._id");
	    	queryBuilder.setDistinct(true);
	    	break;
	    case 2: // Collection from Owned Folders
	    	queryBuilder.setTables("collection INNER JOIN games ON collection.game_id = games._id " +
    			"INNER JOIN folders ON collection.folder_id = folders._id " +
				"LEFT JOIN consoles ON games.console_id = consoles._id");
    		queryBuilder.appendWhere("is_owned = 1");
    		queryBuilder.setDistinct(true);
	    	break;
	    case 3: // Collection from Specific Folder
	    	queryBuilder.setTables("collection INNER JOIN games ON collection.game_id = games._id " +
				"LEFT JOIN consoles ON games.console_id = consoles._id");
    		queryBuilder.appendWhere("folder_id = " + uri.getLastPathSegment());
	    	break;
	    case 5: // All Folders
	    	queryBuilder.setTables("folders");
	    	break;
	    case 6: // Owned Folders
	    	queryBuilder.setTables("folders");
	    	queryBuilder.appendWhere("is_owned = 1");
	    	break;
	    case 8: // Individual Folder
	    	queryBuilder.setTables("folders");
	    	queryBuilder.appendWhere(_ID + " = " + uri.getLastPathSegment());
	    	break;
	    case 7: // Individual Game
	    	queryBuilder.setTables("games");
	    	queryBuilder.appendWhere(_ID + " = " + uri.getLastPathSegment());
	    	break;
	    default:
	        throw new IllegalArgumentException("Unknown URI");
	    }
		
	    Cursor cursor = queryBuilder.query(rfgData.getReadableDatabase(),
	            projection, selection, selectionArgs, null, null, sortOrder);
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    return cursor;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int uriType = sURIMatcher.match(uri);
	    if (uriType != 3 && uriType != 5 && uriType != 6) {
	        throw new IllegalArgumentException("Invalid URI for insert");
	    }
	    
	    int insUpdCount = 0;
	    SQLiteDatabase db = rfgData.getWritableDatabase();
	    db.beginTransaction();
	    
	    try {
	    	if (uriType == 3) {
	    		long folderId = Long.parseLong(uri.getLastPathSegment());
	    		
		    	// If this is folderId = 0, insert a new folder record.
		    	
		    	
		    	// Wipe all collection data for this folder.
		    	int deleteCount = db.delete("collection", "folder_id = ?", new String[] { Long.toString(folderId) });
		    	Log.i(TAG, "Removed " + deleteCount + " records from collection table.");
		    	
		    	for(int i = 0; i < values.length; i++) {
	    			// Save collection quantities in a separate array, then trim down values[i].
	    			ContentValues quantities = new ContentValues();
	    			quantities.put("qty", values[i].getAsInteger("qty"));
	    			quantities.put("box", values[i].getAsInteger("box"));
	    			quantities.put("man", values[i].getAsInteger("man"));
	    			values[i].remove("folder_name");
	    			values[i].remove("qty");
	    			values[i].remove("box");
	    			values[i].remove("man");
	    			long gameId = 0;
	    			
	    			// Check whether the game already exists in the database. Chances are, it does.
					Cursor gameCursor = db.query("games", new String[] { _ID }, "rfgid = ?", 
						new String[] { values[i].getAsString("rfgid") }, null, null, null);
					if (gameCursor.moveToNext()) {
						gameId = gameCursor.getLong(0);
						gameCursor.close();
					} else {
						gameCursor.close();
						gameId = db.insert("games", null, values[i]);
						if (gameId != -1) {
							// Inserted the game successfully!
							Log.i(TAG, "Inserted " + values[i].getAsString("rfgid") + " / " + values[i].getAsString("title"));
						} else {
							// So we couldn't insert it, but it doesn't exist either. Uh oh.
							Log.e(TAG, "RFGID " + values[i].getAsString("rfgid") + " was rejected, but doesn't exist in the games table.");
						}
					}
	    			
	    			// If we've got an ID number for this game, go ahead and insert a collection record.
	    			if (gameId > 0) {
	    				Log.i(TAG, "Inserting collection record for " + values[i].getAsString("rfgid"));
	    				quantities.put("folder_id", folderId);
	    				quantities.put("game_id", gameId);
	    				db.insert("collection", null, quantities);
	    				insUpdCount++;
	    			}
		    	}
		    	
		    	// Update the timestamp on this folder.
		    	ContentValues timeStamp = new ContentValues();
		    	timeStamp.put("last_load", System.currentTimeMillis() / 1000L);
		    	Log.i(TAG, "Updating timestamp on folder " + folderId + " to " + timeStamp.getAsLong("last_load"));
		    	db.update("folders", timeStamp, _ID + " = ?", new String[] { Long.toString(folderId) });
		    } else if (uriType == 5 || uriType == 6) {
		    	Cursor existingFolders = query(uri, new String[] { _ID, "folder_name" }, 
		    			null, null, "folder_name ASC");
		    	
		    	try {
			    	for(int i = 0; i < values.length; i++) {
			    		if(existingFolders.moveToNext()) {
			    			// A folder exists, decide what to do.
			    			if(existingFolders.getString(1).compareTo(values[i].getAsString("folder_name")) > 0) {
			    				// Insert this folder.
			    				Log.i(TAG, "Inserting folder \"" + values[i].getAsString("folder_name") + "\"");
			    				db.insert("folders", null, values[i]);
			    				insUpdCount++;
			    			} else if(existingFolders.getString(1).compareTo(values[i].getAsString("folder_name")) < 0) {
			    				// Delete this folder.
			    				Log.i(TAG, "Deleting folder \"" + existingFolders.getString(1) + "\"");
			    				db.delete("collection", "folder_id = ?", new String[] { Long.toString(existingFolders.getLong(0)) });
			    				db.delete("folders", _ID + " = ?", new String[] { Long.toString(existingFolders.getLong(0)) });
			    			} else {
			    				// This folder exists already, just update the flags.
			    				Log.i(TAG, "Updating folder \"" + values[i].getAsString("folder_name") + "\"");
			    				values[i].remove("last_load");
			    				db.update("folders", values[i], _ID + " = ?", new String[] { Long.toString(existingFolders.getLong(0)) });
			    				insUpdCount++;
			    			}
			    		} else {
			    			// No more folders exist in the database, just insert what we've got.
			    			Log.i(TAG, "Inserting folder \"" + values[i].getAsString("folder_name") + "\"");
			    			db.insert("folders", null, values[i]);
			    		}
			    	}
			    	
			    	// If there's anything left in the cursor but not the value collection, delete it.
			    	while(existingFolders.moveToNext()) {
	    				Log.i(TAG, "Deleting folder \"" + existingFolders.getString(1) + "\"");
	    				db.delete("collection", "folder_id = ?", new String[] { Long.toString(existingFolders.getLong(0)) });
	    				db.delete("folders", _ID + " = ?", new String[] { Long.toString(existingFolders.getLong(0)) });
			    	}
		    	} finally {
		    		existingFolders.close();
		    	}
		    }
	    	
	    	db.setTransactionSuccessful();
	    	
	    	// Always notify folders/all, the home screen is keeping track of this.
	    	getContext().getContentResolver().notifyChange(Uri.withAppendedPath(FOLDERS_URI, "all"), null);
	    	if(uriType != 5)
	    		getContext().getContentResolver().notifyChange(uri, null);
	    } finally {
	    	db.endTransaction();
	    }
	    
		return insUpdCount;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
