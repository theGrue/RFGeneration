package com.rfgeneration;

import java.util.ArrayList;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.rfgeneration.objects.CollectionPage;
import com.rfgeneration.objects.Game;
import com.rfgeneration.scrapers.SearchScraper;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchList extends ListActivity implements OnClickListener {

	private String searchGame;
	private ArrayList<Game> gameList = new ArrayList<Game>();
	private int numPages = 1;
	private int lastLoadedPage = 1;
	private ArrayList<Game> items;
	private Runnable getData;
	private ProgressDialog m_ProgressDialog = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
        setContentView(R.layout.searchlist);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getData = new Runnable(){
		    public void run() {
		        getData();
		    }
		};
		Thread thread = new Thread(getData);
		thread.start();
		m_ProgressDialog = ProgressDialog.show(SearchList.this,    
		      "", "Now loading...", true);
    }    
	
    private void getData() {
		Intent myIntent = getIntent(); // this is just for example purpose
		searchGame = myIntent.getStringExtra("SEARCH_GAME");
		
		try {
			CollectionPage searchResults = SearchScraper.getSearchPage(searchGame, 1);
			gameList = searchResults.getList();
			numPages = searchResults.getTotalPages();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		runOnUiThread(returnRes);
    }
    
    private Runnable returnRes = new Runnable() {
        public void run() {	
        	m_ProgressDialog.dismiss();
        	
			TextView currentPage = (TextView) findViewById(R.id.SearchHeader);
	        currentPage.setText("Search results for \"" + searchGame + "\"...");

			setListAdapter(new SearchAdapter(gameList));
        }
    };
    
	public void onClick(View v) {

	}

	private class SearchAdapter extends EndlessAdapter {

		private RotateAnimation rotate=null;
		private ArrayList<Game> gameListToLoad = new ArrayList<Game>();
		
		public SearchAdapter(ArrayList<Game> list) {
			// TODO Auto-generated constructor stub
			super(new ArrayAdapter<Game>(SearchList.this,
					R.layout.pending,
					android.R.id.text1,
					list));

					rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
					0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
					rotate.setDuration(600);
					rotate.setRepeatMode(Animation.RESTART);
					rotate.setRepeatCount(Animation.INFINITE);
					
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
			View row=getLayoutInflater().inflate(R.layout.pending, null);
	
			if(numPages > 1)
			{
				TextView child = (TextView)row.findViewById(android.R.id.text1);
				child.setText("Loading page " + (lastLoadedPage + 1) + " of " + numPages + "...");
			}
			//child.setVisibility(View.GONE);
	
			//child=row.findViewById(R.id.throbber);
			//child.setVisibility(View.VISIBLE);
			//child.startAnimation(rotate);
	
			return(row);
		}
		
		@Override
		protected boolean cacheInBackground() throws Exception {
			if(lastLoadedPage < numPages)
			{
				try {
					gameListToLoad = SearchScraper.getSearchPage(searchGame, lastLoadedPage + 1).getList();
					lastLoadedPage++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			
			return (lastLoadedPage < numPages);
		}

		@Override
		protected void appendCachedData() {
			// TODO Auto-generated method stub
			if(gameListToLoad.size() > 0) {
				@SuppressWarnings("unchecked")
				ArrayAdapter<Game> a=(ArrayAdapter<Game>)getWrappedAdapter();
				for (int i=0;i<gameListToLoad.size();i++) { a.add(gameListToLoad.get(i)); /*items.add(gameListToLoad.get(i));*/ }
			}
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
		    	Intent myIntent = new Intent(this, RFGeneration.class);
				startActivityForResult(myIntent, 0);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
    }
}
