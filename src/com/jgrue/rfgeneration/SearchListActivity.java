package com.jgrue.rfgeneration;

import static android.provider.BaseColumns._ID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.data.RFGenerationData;
import com.jgrue.rfgeneration.objects.Collection;
import com.jgrue.rfgeneration.objects.Folder;
import com.jgrue.rfgeneration.objects.Game;
import com.jgrue.rfgeneration.scrapers.SearchScraper;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class SearchListActivity extends ListActivity {
	private static final String TAG = "SearchListActivity";
	private String searchGame;
	private boolean quickSearch = true;
	private List<Game> gameList;
	private RFGenerationData rfgData;
	private Pattern variantRegex;
	private int paddingUnit;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		searchGame = getIntent().getStringExtra(Constants.INTENT_SEARCH);
		rfgData = new RFGenerationData(this);
		gameList = new ArrayList<Game>();
		variantRegex = Pattern.compile(" \\[.*\\]$");
		paddingUnit = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
		
		TextView currentPage = (TextView) findViewById(R.id.search_text);
        currentPage.setText("Search results for \"" + searchGame + "\"...");
		
		setListAdapter(new SearchAdapter(gameList));
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	  
	  	Intent myIntent = new Intent(v.getContext(), GameInfoActivity.class);
		myIntent.putExtra(Constants.INTENT_GAME_RFGID, gameList.get(position).getRFGID());
		startActivityForResult(myIntent, 0);
	}

    private class SearchAdapter extends EndlessAdapter {
    	
    	private List<Game> gameListToLoad = new ArrayList<Game>();
    	private Set<String> collectionGames = new HashSet<String>();
    	private boolean collectionLoaded = false;
    	private boolean searchResultsLoaded = false;
    	private SQLiteDatabase db;
    	private int numPages = -1;
    	private int nextPage = 0;
		
		public SearchAdapter(List<Game> gameList) {
			super(new ArrayAdapter<Game>(SearchListActivity.this, R.layout.pending, R.id.pending_text, gameList));
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v = convertView;
			
			if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.gamerow, null);
            }
			
			Game o = null;
			if(position < gameList.size())
				o = gameList.get(position);
			
            if (o != null) {
                    TextView console = (TextView) v.findViewById(R.id.console);
                    if (console != null)
                    {
                    	if(o.getConsoleAbbv().length() > 0)
                    		console.setText(o.getConsoleAbbv());
                    	else
                    	{
                    		Log.w(TAG, o.getRFGID() + " is missing a console abbreviation, performing lookup.");
                    		Cursor cursor = db.query("consoles", new String[] { "console_abbv" }, 
                    				_ID + " = ?", new String[] { Integer.toString(o.getConsoleId()) }, 
                    				null, null, null);
                    		startManagingCursor(cursor);
                    		
                    		while(cursor.moveToNext()) {
                    			o.setConsoleAbbv(cursor.getString(0));
                    			console.setText(cursor.getString(0));
                    		}
                    	}
                    }
                    
                    TextView title = (TextView) v.findViewById(R.id.title);
                    if(title != null)
                    {
                    	Matcher matcher = variantRegex.matcher(o.getTitle());
	       				if(matcher.find()) {
	       					String variationTitle = matcher.group().substring(2, matcher.group().length() - 1);
	       					String mainTitle = o.getTitle().substring(0, o.getTitle().length() - variationTitle.length() - 3);
	       					title.setText(Html.fromHtml("<b>" + mainTitle + "</b> <font color=\"#" +
	       							Integer.toHexString(getResources().getColor(R.drawable.textlight)).substring(2) +
	       							"\">[" + variationTitle + "]</font>"));
	       				} else {
	       					title.setText(Html.fromHtml("<b>" + o.getTitle() + "</b>"));
	       				}
                    }
                    	
                    TextView publisher = (TextView) v.findViewById(R.id.publisher);
                    if(publisher != null){
                    	if(o.getType().equals("S"))
                    		publisher.setText(o.getPublisher());
                    	else
                    		publisher.setText("[" + o.getType() + "] " + o.getPublisher());
                    	
                    	if(o.getYear() > 0 && !o.getPublisher().equals(""))
                    		publisher.setText(publisher.getText() + ", " + o.getYear());
                    	else if(o.getYear() > 0)
                    		publisher.setText(publisher.getText() + "" + o.getYear());
                    }
                    
                    RelativeLayout regionLayout = (RelativeLayout) v.findViewById(R.id.regionLayout);
                    if(regionLayout != null) {
                    	if(regionLayout.getChildCount() > 0)
                			regionLayout.removeViews(0, regionLayout.getChildCount());
                    	
                    	if(!o.getRegion().contains(",") &&  !o.getRegion().contains("-")) {
                    		ImageView region = new ImageView(v.getContext());
                    		region.setImageDrawable(o.getRegionDrawable(v.getContext()));
                    		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
            	                      android.view.ViewGroup.LayoutParams.FILL_PARENT,
            	                      android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    		regionLayout.addView(region, layoutParams);
                    	} else {                       		
                    		AnimatedImageView regionAnim = new AnimatedImageView(v.getContext());
                    		regionAnim.setImageDrawable(o.getRegionAnimation(v.getContext()));
                    		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams( 
                    				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 21, getResources().getDisplayMetrics()),
                    				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));
                    		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    		regionLayout.addView(regionAnim, layoutParams);
                    	}
                    }
                    
                    TableLayout folderLayout = (TableLayout) v.findViewById(R.id.folders);
                	if(folderLayout.getChildCount() > 0)
                		folderLayout.removeViews(0, folderLayout.getChildCount());
                	
                    if (position < collectionGames.size()) {
                    	if(o.getCollections().size() == 0) {
                    		Log.w(TAG, o.getRFGID() + " is missing collection data, performing lookup.");
	                    	Cursor cursor = db.rawQuery("SELECT folder_name, is_private, is_for_sale, qty, box, man " +
	                    			"FROM collection INNER JOIN folders ON collection.folder_id = folders._id " +
	                    			"WHERE game_id = ? ORDER BY is_owned DESC, folder_name", 
	                    			new String[] { Long.toString(o.getId()) });
	                    	startManagingCursor(cursor);
	        				
	                    	List<Collection> collections = new ArrayList<Collection>();
	        				while(cursor.moveToNext()) {
	        					Folder newFolder = new Folder(cursor.getString(0));
	        					newFolder.setForSale(cursor.getInt(2) == 1);
	        					newFolder.setPrivate(cursor.getInt(1) == 1);
	        					
	        					Collection newCollection = new Collection();
	        					newCollection.setGameQuantity(cursor.getInt(3));
	        					newCollection.setBoxQuantity(cursor.getInt(4));
	        					newCollection.setManualQuantity(cursor.getInt(5));
	        					newCollection.setFolder(newFolder);
	        					
	        					collections.add(newCollection);
	        				}
	        				
	        				o.setCollections(collections);
                    	}
                    	
                    	for (int i = 0; i < o.getCollections().size(); i++) {
	                    	TableRow folderRow = new TableRow(v.getContext());
	    					folderRow.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	    					
	    					ImageView folderImage = new ImageView(v.getContext());
	    			    	if(o.getCollections().get(i).getFolder().isForSale())
	    			    		folderImage.setImageResource(R.drawable.folder_for_sale);
	    			    	else if(o.getCollections().get(i).getFolder().isPrivate())
	    			    		folderImage.setImageResource(R.drawable.folder_private);
	    			    	else
	    			    		folderImage.setImageResource(R.drawable.folder);
	    			    	LayoutParams folderImageLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	    			    	folderImageLayout.setMargins(0, 0, paddingUnit, 0);
	    			    	folderImageLayout.gravity = Gravity.CENTER_VERTICAL;
	    			    	folderImage.setLayoutParams(folderImageLayout);
	    			    	folderRow.addView(folderImage);
	    			    	
	    			    	TextView folderName = new TextView(v.getContext());
	    			    	folderName.setText(o.getCollections().get(i).getFolder().getName());
	    			    	folderName.setTextColor(getResources().getColor(R.drawable.text));
	    			    	LayoutParams folderNameLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	    			    	folderNameLayout.gravity = Gravity.CENTER_VERTICAL;
	    			    	folderName.setLayoutParams(folderNameLayout);
	    			    	folderRow.addView(folderName);
	    			    	
	    			    	TextView folderQty = new TextView(v.getContext());
	    			    	folderQty.setText(Html.fromHtml("<font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.qty)).substring(2) + "\">" + 
	    			    			"G:" + o.getCollections().get(i).getGameQuantity() + "</font> <font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.box)).substring(2) + "\">" + 
	    			    			"B:" + o.getCollections().get(i).getBoxQuantity() + "</font> <font color=\"#" + Integer.toHexString(getResources().getColor(R.drawable.man)).substring(2) + "\">" +
	    			    			"M:" + o.getCollections().get(i).getManualQuantity() + "</font> "));
	    			    	folderQty.setTextColor(getResources().getColor(R.drawable.text));
	    			    	LayoutParams folderQtyLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	    			    	folderQtyLayout.setMargins(0, 0, paddingUnit * 2, 0);
	    			    	folderQtyLayout.gravity = Gravity.CENTER_VERTICAL;
	    			    	folderQty.setLayoutParams(folderQtyLayout);
	    			    	folderRow.addView(folderQty);
	    			    	
	    			    	folderLayout.addView(folderRow);
                    	}
                    }
            }
            else
            {
            	v = super.getView(position, convertView, parent);
            }
            
            // http://stackoverflow.com/questions/2050533/list-items-with-alternating-colors
            if (position % 2 == 0){
                v.setBackgroundResource(R.drawable.alterselector1);
            } else {
                v.setBackgroundResource(R.drawable.alterselector2);
            }
            
			return v;
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			boolean returnMore = false;
			
			if(!collectionLoaded) {
				StringBuilder query = new StringBuilder();
				query.append("SELECT DISTINCT games._id, rfgid, console_abbv, title, region, type, publisher, year ");
				query.append("FROM collection INNER JOIN games ON collection.game_id = games._id ");
				query.append("LEFT JOIN consoles ON games.console_id = consoles._id WHERE ");
				String[] params = searchGame.split(" ");
				
				for(int i = 0; i < params.length; i++) {
					query.append("title LIKE ? ");
					if(i < params.length - 1)
						query.append("AND ");
					
					params[i] = "%" + params[i] + "%";
				}
				
				query.append("ORDER BY title");
				
				db = rfgData.getReadableDatabase();
				Cursor cursor = db.rawQuery(query.toString(), params);
				startManagingCursor(cursor);
				
				while(cursor.moveToNext()) {
					Game newGame = new Game();
					newGame.setId(cursor.getLong(0));
					newGame.setRFGID(cursor.getString(1));
					newGame.setConsoleAbbv(cursor.getString(2));
					newGame.setTitle(cursor.getString(3));
					newGame.setRegion(cursor.getString(4));
					newGame.setType(cursor.getString(5));
					newGame.setPublisher(cursor.getString(6));
					newGame.setYear(cursor.getInt(7));
					collectionGames.add(newGame.getRFGID());
					gameListToLoad.add(newGame);
				}
				
				collectionLoaded = true;
				returnMore = true;
			} else {
				if(numPages == -1)
					numPages = SearchScraper.getTotalPages(searchGame);
				gameListToLoad = SearchScraper.getSearchPage(searchGame, nextPage);
				
				// Remove games that are already displayed.
				int i = 0;
				while(i < gameListToLoad.size()) {
					if (collectionGames.contains(gameListToLoad.get(i).getRFGID()))
					{
						Log.i(TAG, "Search result exists in collection results, removing: " + gameListToLoad.get(i).toString());
						gameListToLoad.remove(i);
					}
					else
					{
						i++;
					}
				}
				
				nextPage++;
				searchResultsLoaded = true;
				returnMore = nextPage < numPages;
			}
			
			return returnMore;
		}

		@Override
		protected void appendCachedData() {
			@SuppressWarnings("unchecked")
			ArrayAdapter<Game> wrappedAdapter = (ArrayAdapter<Game>)getWrappedAdapter();
			
			if(gameListToLoad.size() > 0) {
				for (int i = 0; i < gameListToLoad.size(); i++) { 
					wrappedAdapter.add(gameListToLoad.get(i));
				}
			} else if (searchResultsLoaded && wrappedAdapter.getCount() == 0) {
				AlertDialog alertDialog = new AlertDialog.Builder(SearchListActivity.this).create();
	        	alertDialog.setMessage("No results found.");
	        	alertDialog.setButton("OK", new DialogInterface.OnClickListener() { @Override
					public void onClick(DialogInterface dialog, int which) { finish(); return; } });
	        	alertDialog.show();
			}
			
			if (nextPage >= numPages && numPages != -1)
				findViewById(R.id.search_progress).setVisibility(View.GONE);
		}
    	
		@Override
		protected View getPendingView(ViewGroup parent) {
			View row = getLayoutInflater().inflate(R.layout.pending, null);
			return row;
		}
    }
    
    @Override
    public void onDestroy() {
    	if(rfgData != null)
    		rfgData.close();
    	super.onDestroy();
    }
}
