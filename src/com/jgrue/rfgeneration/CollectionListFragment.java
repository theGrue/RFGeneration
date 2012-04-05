package com.jgrue.rfgeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jgrue.rfgeneration.GameInfoActivity;
import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.data.RFGenerationProvider;
import com.jgrue.rfgeneration.data.RFGenerationData;
import com.jgrue.rfgeneration.objects.Collection;
import com.jgrue.rfgeneration.objects.Folder;
import com.jgrue.rfgeneration.objects.Game;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;


public class CollectionListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "CollectionListFragment";
	private SimpleCursorAdapter adapter;
	private Pattern variantRegex;
	private RFGenerationData rfgData;
	private long folderId = -1;
	private long consoleId = -1;
	private String type = "";
	private SQLiteDatabase db;
	private int paddingUnit;
	private Map<Long, List<Collection>> collections;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // Initialize class variables
	    variantRegex = Pattern.compile(" \\[.*\\]$");
	    paddingUnit = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
		collections = new HashMap<Long, List<Collection>>();
		rfgData = new RFGenerationData(getActivity());
		db = rfgData.getReadableDatabase();
		
		// If we're looking at multiple folders, cache the collection data.
		if(folderId <= 0) {
			StringBuilder query = new StringBuilder();
			query.append("SELECT game_id, folder_name, is_private, is_for_sale, qty, box, man ");
			query.append("FROM collection INNER JOIN folders ON collection.folder_id = folders._id ");
			if(folderId == 0)
				query.append("WHERE is_owned = 1 ");
			query.append("ORDER BY game_id, folder_name");
			
			Cursor collection = db.rawQuery(query.toString(), null);
			getActivity().startManagingCursor(collection);
			
			long previousGameId = -1;
			List<Collection> gameCollections = new ArrayList<Collection>();
			while(collection.moveToNext()) {
				// Set up previousGameId on the first run.
				if(previousGameId == -1) {
					previousGameId = collection.getLong(0);
				} else if(previousGameId != collection.getLong(0)) {
					collections.put(previousGameId, gameCollections);
					previousGameId = collection.getLong(0);
					gameCollections = new ArrayList<Collection>();
				}
				
				Folder newFolder = new Folder(collection.getString(1));
				newFolder.setForSale(collection.getInt(3) == 1);
				newFolder.setPrivate(collection.getInt(2) == 1);
				
				Collection newCollection = new Collection();
				newCollection.setGameQuantity(collection.getInt(4));
				newCollection.setBoxQuantity(collection.getInt(5));
				newCollection.setManualQuantity(collection.getInt(6));
				newCollection.setFolder(newFolder);
				
				gameCollections.add(newCollection);
			}
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

		getListView().setFastScrollEnabled(true);
	    getListView().setCacheColorHint(getResources().getColor(R.drawable.transparent));
	    getListView().setDivider(getResources().getDrawable(R.drawable.border));
	    getListView().setDividerHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
	    
	    // Set up data binding
	    Object[] tag = (Object[])getActivity().findViewById(R.id.collection_text).getTag();
	    folderId = Long.parseLong(tag[0].toString());
	    consoleId = Long.parseLong(tag[1].toString());
	    type = tag[2].toString();
	    Log.w(TAG, "onActivityCreated, folderId = " + folderId);
	    getLoaderManager().initLoader(0, null, this);
	 
	    adapter = new CollectionCursorAdapter(getActivity().getApplicationContext(), R.layout.gamerow, null, 
    		new String[] { "_id", "console_abbv", "title", "region", "publisher", "qty" }, 
    		new int[] { R.id.folders, R.id.console, R.id.title, R.id.regionLayout, R.id.publisher, R.id.quantity }, 0);
	    adapter.setViewBinder(new CollectionViewBinder());
	    setListAdapter(adapter);
	}
	
	@Override
    public void onDestroy() {
    	if(rfgData != null)
    		rfgData.close();
    	super.onDestroy();
    }
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.w(TAG, "onCreateLoader, folderId = " + folderId);
		
		Uri uri;
		if(folderId == -1)
			uri = Uri.withAppendedPath(RFGenerationProvider.COLLECTION_URI, "all");
		else if(folderId == 0)
			uri = Uri.withAppendedPath(RFGenerationProvider.COLLECTION_URI, "owned");
		else
			uri = Uri.withAppendedPath(RFGenerationProvider.COLLECTION_URI, Long.toString(folderId));
		
		String[] projection;
		if(folderId <= 0)
			projection = new String[] { "games._id as _id", "rfgid", "console_abbv", "title", 
		    	"region", "type", "publisher", "year", "0 as qty", "0 as box", "0 as man" };
		else
			projection = new String[] { "games._id as _id", "rfgid", "console_abbv", "title", 
		    	"region", "type", "publisher", "year", "qty", "box", "man" };
		
		// Create the selection clause.
		String selection = "";
		List<String> selectionArgs = new ArrayList<String>();
		if(consoleId > -1) {
			if(selection.length() > 0)
				selection += " AND ";
			selection += "console_id = ?";
			selectionArgs.add(Long.toString(consoleId));
		}
		if(type.length() > 0) {
			if(selection.length() > 0)
				selection += " AND ";
			selection += "type = ? ";
			selectionArgs.add(type);
		}
		
	    return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs.toArray(new String[]{}), "title ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
		getActivity().findViewById(R.id.collection_progress).setVisibility(View.GONE);	   
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
		getActivity().findViewById(R.id.collection_progress).setVisibility(View.VISIBLE);	  
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
	  	Intent myIntent = new Intent(v.getContext(), GameInfoActivity.class);
		myIntent.putExtra(Constants.INTENT_GAME_ID, id);
		startActivityForResult(myIntent, 0);
	}
	
	private class CollectionCursorAdapter extends SimpleCursorAdapter implements SectionIndexer {

		private AlphabetIndexer alphaIndexer;
		
		public CollectionCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}
		
		@Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final View row = super.getView(position, convertView, parent);
            
            if (position % 2 == 0){
                row.setBackgroundResource(R.drawable.alterselector1);
            } else {
                row.setBackgroundResource(R.drawable.alterselector2);
            }
            
            return row;
        }
		
		@Override
		public Cursor swapCursor(Cursor c) {
		    // Create our indexer
		    if (c != null)
		    	alphaIndexer = new AlphabetIndexer(c, c.getColumnIndex("title"), " \"#$'.0123456789@ABCDEFGHIJKLMNOPQRSTUVWXYZ");

		    return super.swapCursor(c);
		}

		@Override
		public int getPositionForSection(int section) {
			return alphaIndexer.getPositionForSection(section);
		}

		@Override
		public int getSectionForPosition(int position) {
			return alphaIndexer.getSectionForPosition(position);
		}

		@Override
		public Object[] getSections() {
			return alphaIndexer.getSections();
		}
	}

	private class CollectionViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() == R.id.title) {
				String title = cursor.getString(columnIndex);
				Matcher matcher = variantRegex.matcher(title);
   				if(matcher.find()) {
   					String variationTitle = matcher.group().substring(2, matcher.group().length() - 1);
   					String mainTitle = title.substring(0, title.length() - variationTitle.length() - 3);
   					((TextView)view).setText(Html.fromHtml("<b>" + mainTitle + "</b> <font color=\"#" +
   							Integer.toHexString(getResources().getColor(R.drawable.textlight)).substring(2) +
   							"\">[" + variationTitle + "]</font>"));
   				} else {
   					((TextView)view).setText(Html.fromHtml("<b>" + title + "</b>"));
   				}
   				return true;
			} else if (view.getId() == R.id.publisher) {
				if(cursor.getString(columnIndex - 1).equals("S"))
					((TextView)view).setText(cursor.getString(columnIndex));
            	else
            		((TextView)view).setText("[" + cursor.getString(columnIndex - 1) + "] " + cursor.getString(columnIndex));
            	
            	if(cursor.getInt(columnIndex + 1) > 0 && !cursor.getString(columnIndex).equals(""))
            		((TextView)view).setText(((TextView)view).getText() + ", " + cursor.getInt(columnIndex + 1));
            	else if(cursor.getInt(columnIndex + 1) > 0)
            		((TextView)view).setText(((TextView)view).getText() + "" + cursor.getInt(columnIndex + 1));
            	
            	return true;
			} else if (view.getId() == R.id.regionLayout) {
				Game o = new Game();
				o.setRegion(cursor.getString(columnIndex));
				RelativeLayout regionLayout = (RelativeLayout)view;
				if(regionLayout.getChildCount() > 0)
        			regionLayout.removeViews(0, regionLayout.getChildCount());
            	
            	if(o.getRegion().indexOf("-") == -1) {
            		ImageView region = new ImageView(view.getContext());
            		region.setImageDrawable(o.getRegionDrawable(view.getContext()));
            		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
    	                      android.view.ViewGroup.LayoutParams.FILL_PARENT,
    	                      android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            		regionLayout.addView(region, layoutParams);
            	} else {                       		
            		AnimatedImageView regionAnim = new AnimatedImageView(view.getContext());
            		regionAnim.setImageDrawable(o.getRegionAnimation(view.getContext()));
            		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(21, 15);
            		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            		regionLayout.addView(regionAnim, layoutParams);
            	}
				return true;
			} else if (view.getId() == R.id.quantity) {
				if(folderId > 0) {
					view.setVisibility(View.VISIBLE);
					((TextView)view).setText(Html.fromHtml("<font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.qty)).substring(2) + "\">" + 
			    			"G:" + cursor.getInt(columnIndex) + "</font> <font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.box)).substring(2) + "\">" + 
			    			"B:" + cursor.getInt(columnIndex + 1) + "</font> <font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.man)).substring(2) + "\">" +
			    			"M:" + cursor.getInt(columnIndex + 2) + "</font>"));
				}
				return true;
			} else if (view.getId() == R.id.folders) {
				if(folderId <= 0) {
					TableLayout folderLayout = (TableLayout)view;
                	if(folderLayout.getChildCount() > 0)
                		folderLayout.removeViews(0, folderLayout.getChildCount());
                	
					List<Collection> folders = collections.get(cursor.getLong(columnIndex));
					if(folders == null) 
						return true;

    				for(int i = 0; i < folders.size(); i++) {
    					TableRow folderRow = new TableRow(view.getContext());
    					folderRow.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
    							android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    					
    					ImageView folderImage = new ImageView(view.getContext());
    			    	if(folders.get(i).getFolder().isForSale())
    			    		folderImage.setImageResource(R.drawable.folder_for_sale);
    			    	else if(folders.get(i).getFolder().isPrivate())
    			    		folderImage.setImageResource(R.drawable.folder_private);
    			    	else
    			    		folderImage.setImageResource(R.drawable.folder);
    			    	LayoutParams folderImageLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
    			    			android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    			    	folderImageLayout.setMargins(0, 0, paddingUnit, 0);
    			    	folderImageLayout.gravity = Gravity.CENTER_VERTICAL;
    			    	folderImage.setLayoutParams(folderImageLayout);
    			    	folderRow.addView(folderImage);
    			    	
    			    	TextView folderName = new TextView(view.getContext());
    			    	folderName.setText(folders.get(i).getFolder().getName());
    			    	folderName.setTextColor(getResources().getColor(R.drawable.text));
    			    	LayoutParams folderNameLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
    			    			android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    			    	folderNameLayout.gravity = Gravity.CENTER_VERTICAL;
    			    	folderName.setLayoutParams(folderNameLayout);
    			    	folderRow.addView(folderName);
    			    	
    			    	TextView folderQty = new TextView(view.getContext());
    			    	folderQty.setText(Html.fromHtml("<font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.qty)).substring(2) + "\">" + 
    			    			"G:" + folders.get(i).getGameQuantity() + "</font> <font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.box)).substring(2) + "\">" + 
    			    			"B:" + folders.get(i).getBoxQuantity() + "</font> <font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.man)).substring(2) + "\">" +
    			    			"M:" + folders.get(i).getManualQuantity() + "</font> "));
    			    	folderQty.setTextColor(getResources().getColor(R.drawable.text));
    			    	LayoutParams folderQtyLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
    			    			android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    			    	folderQtyLayout.setMargins(0, 0, paddingUnit * 2, 0);
    			    	folderQtyLayout.gravity = Gravity.CENTER_VERTICAL;
    			    	folderQty.setLayoutParams(folderQtyLayout);
    			    	folderRow.addView(folderQty);
    			    	
    			    	folderLayout.addView(folderRow);
    				}
				}
				return true;
			}
			
			return false;
		}
	}
}
