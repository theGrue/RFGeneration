package com.jgrue.rfgeneration;

import java.util.ArrayList;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.jgrue.rfgeneration.objects.CollectionPage;
import com.jgrue.rfgeneration.objects.Game;
import com.jgrue.rfgeneration.scrapers.SearchScraper;
import com.jgrue.rfgeneration.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchListActivity extends ListActivity {
	private static final String TAG = "SearchListActivity";
	private String searchGame;
	private ArrayList<Game> gameList;
	private ArrayList<Game> items;
	private int numPages = -1;
	private int nextPage = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlist);
        
        searchGame = getIntent().getStringExtra("SEARCH_GAME");
        TextView currentPage = (TextView) findViewById(R.id.SearchHeader);
        currentPage.setText("Search results for \"" + searchGame + "\"...");
        
        gameList = new ArrayList<Game>();
        setListAdapter(new SearchAdapter(gameList));
    }

	private class SearchAdapter extends EndlessAdapter {

		private ArrayList<Game> gameListToLoad = new ArrayList<Game>();
		
		public SearchAdapter(ArrayList<Game> list) {
			super(new ArrayAdapter<Game>(SearchListActivity.this,
					R.layout.pending,
					android.R.id.text1,
					list));
					
			items = list;
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
			if(position < items.size())
				o = items.get(position);
			
            if (o != null) {
                    TextView console = (TextView) v.findViewById(R.id.console);
                    if (console != null)
                    	console.setText(o.getConsoleAbbv());
                    
                    TextView title = (TextView) v.findViewById(R.id.title);
                    if(title != null)
                    {
                    	title.setText(Html.fromHtml("<b>" + o.getTitle() + "</b>"));
                    	if(o.getVariationTitle() != null)
       	            		title.setText(Html.fromHtml("<b>" + o.getTitle() + "</b> <font color=\"#404040\">[" + o.getVariationTitle() + "]</font>"));
                    }
                    
                    TextView quantity = (TextView) v.findViewById(R.id.quantity);
                    if(quantity != null)
                    {
                    	quantity.setText("");
                    	quantity.setVisibility(2);
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
                    	
                    	if(o.getRegion().indexOf(",") == -1) {
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
                    		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(21, 15);
                    		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    		regionLayout.addView(regionAnim, layoutParams);
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
		protected View getPendingView(ViewGroup parent) {
			View row = getLayoutInflater().inflate(R.layout.pending, null);
	
			if(numPages > 1)
			{
				TextView child = (TextView)row.findViewById(android.R.id.text1);
				child.setText("Loading page " + nextPage + " of " + numPages + "...");
				findViewById(R.id.search_progress).setVisibility(View.VISIBLE);
			}
	
			return(row);
		}
		
		@Override
		protected boolean cacheInBackground() throws Exception {
			// Load the current page.
			CollectionPage searchResults = SearchScraper.getSearchPage(searchGame, nextPage);
			gameListToLoad = searchResults.getList();
			
			// If this is the first time, save the total number of pages.
			if(numPages == -1) {
				numPages = searchResults.getTotalPages();
				Log.i(TAG, "Initial load done, " + numPages + " total page(s) found, " + 
						gameListToLoad.size() + " games on page.");
			} else {
				Log.i(TAG, "Loaded " + gameListToLoad.size() + " games on page " + nextPage + ".");
			}
			
			nextPage++;
			return (nextPage <= numPages);
		}

		@Override
		protected void appendCachedData() {
			if(gameListToLoad.size() > 0) {
				@SuppressWarnings("unchecked")
				ArrayAdapter<Game> a=(ArrayAdapter<Game>)getWrappedAdapter();
				for (int i=0;i<gameListToLoad.size();i++) { a.add(gameListToLoad.get(i)); /*items.add(gameListToLoad.get(i));*/ }
			}
			
			findViewById(R.id.search_progress).setVisibility(View.GONE);
		}
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	  super.onListItemClick(l, v, position, id);
	  
	  try{
	  	Intent myIntent = new Intent(v.getContext(), GameInfoActivity.class);
		myIntent.putExtra("GAMEINFO_RFGID", items.get(position).getRFGID());
		startActivityForResult(myIntent, 0);
	  	} catch (Exception e) { }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.searchmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    case R.id.searchmenuhome:
		    	Intent myIntent = new Intent(this, RFGenerationActivity.class);
				startActivityForResult(myIntent, 0);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
    }
}
