package com.rfgeneration;

import java.util.ArrayList;

import com.rfgeneration.objects.CollectionPage;
import com.rfgeneration.objects.Game;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CollectionList extends ListActivity implements OnClickListener {
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<Game> gameList = null;
    private GameAdapter gameAdapter;
    private Runnable viewOrders;
    private String userName;
    private int page = 1;
    private int totalPages = 0;
    private PopupWindow pw;
    private ArrayList<String> folderList = null;
    private ArrayList<String> consoleList = null;
    private ArrayList<String> typeList = null;
    private ArrayList<String> consoleIdList = null;
    private String folder = "Collection";
    private String type = "";
    private String console = "";
    private boolean refresh = false;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collectionlist);
        
        Intent myIntent = getIntent(); // this is just for example purpose
        userName = myIntent.getStringExtra("COLLECTION_USERNAME");
        page = myIntent.getIntExtra("COLLECTION_PAGE", 1);
        if (myIntent.getStringExtra("COLLECTION_FOLDER") != null)
        	folder = myIntent.getStringExtra("COLLECTION_FOLDER");
        if(myIntent.getStringExtra("COLLECTION_TYPE") != null)
        	type = myIntent.getStringExtra("COLLECTION_TYPE");
        if(myIntent.getStringExtra("COLLECTION_CONSOLE") != null)
        	console = myIntent.getStringExtra("COLLECTION_CONSOLE");
        refresh = myIntent.getBooleanExtra("COLLECTION_REFRESH", false);
        
        ((TextView)findViewById(R.id.NextPage2)).setOnClickListener(this);
        ((TextView)findViewById(R.id.PrevPage2)).setOnClickListener(this);
        ((TextView)findViewById(R.id.CurrentPage)).setOnClickListener(this);
        
        gameList = new ArrayList<Game>();
        this.gameAdapter = new GameAdapter(this, R.layout.gamerow, gameList);
        setListAdapter(this.gameAdapter);

		viewOrders = new Runnable(){
		    public void run() {
		        getOrders();
		    }
		};
		Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(CollectionList.this,    
		      "", "Now loading...", true);
    }
    
    private void getOrders(){
        try{
        	if (refresh)
        		CollectionScraper.refresh();
        	CollectionPage collectionPage = CollectionScraper.getCollectionPage(userName, folder, console, type, page);
        	gameList = collectionPage.getList();
        	totalPages = collectionPage.getTotalPages();
        	folderList = collectionPage.getFolderList();
        	typeList = collectionPage.getTypeList();
        	consoleList = new ArrayList<String>();
        	for(int i = 0; i < collectionPage.getConsoleList().size(); i++)
        		consoleList.add(collectionPage.getConsoleList().get(i).getName());
        	consoleIdList = new ArrayList<String>();
        	for(int i = 0; i < collectionPage.getConsoleList().size(); i++)
        		consoleIdList.add(collectionPage.getConsoleList().get(i).getId());
               //Thread.sleep(2000);
            //Log.i("ARRAY", ""+ gameList.size());
          } catch (Exception e) {
            Log.e("BACKGROUND_PROC", e.getMessage());
          }
          runOnUiThread(returnRes);
      }
    
    private Runnable returnRes = new Runnable() {
        public void run() {
            if(gameList != null && gameList.size() > 0){
                gameAdapter.notifyDataSetChanged();
                for(int i=0;i<gameList.size();i++)
                gameAdapter.add(gameList.get(i));
            }
            m_ProgressDialog.dismiss();
            gameAdapter.notifyDataSetChanged();
            
            TextView currentPage = (TextView) findViewById(R.id.CurrentPage);
            currentPage.setText("Page " + page + "/" + totalPages);
            if(totalPages <= 1) {
            	currentPage.setEnabled(false);
            	((TextView)findViewById(R.id.NextPage2)).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.PrevPage2)).setVisibility(View.GONE);
            } else {
            	currentPage.setEnabled(true);
            	((TextView)findViewById(R.id.NextPage2)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.PrevPage2)).setVisibility(View.VISIBLE);
            }
        }
      };
    
    private class GameAdapter extends ArrayAdapter<Game> {

        private ArrayList<Game> items;

        public GameAdapter(Context context, int textViewResourceId, ArrayList<Game> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.gamerow, null);
                }
                Game o = items.get(position);
                if (o != null) {
                        TextView console = (TextView) v.findViewById(R.id.console);
                        if (console != null)
                        	console.setText(o.getConsoleAbbv());
                        
                        TextView title = (TextView) v.findViewById(R.id.title);
                        if(title != null)
                        {
                        	title.setText(o.getTitle());
                        	if(o.getVariationTitle() != null)
           	            		title.setText(title.getText() + " [" + o.getVariationTitle() + "]");
                        }
                        
                        TextView quantity = (TextView) v.findViewById(R.id.quantity);
                        if(quantity != null)
                        	quantity.setText("G:" + o.getGameQuantity() + " M:" + o.getManualQuantity() + " B:" + o.getBoxQuantity() + " ");
                        	
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
                
                // http://stackoverflow.com/questions/2050533/list-items-with-alternating-colors
                if (position % 2 == 0){
                    v.setBackgroundResource(R.drawable.alterselector1);
                } else {
                    v.setBackgroundResource(R.drawable.alterselector2);
                }
                
                return v;
        }
    }

	public void onClick(View v) {
		if(v.getId() == R.id.CurrentPage) {
			/*Dialog dialog = new Dialog(v.getContext());
			
            dialog.setContentView(R.layout.jumptopage);

            dialog.setTitle("Jump to Page");
            
            dialog.setCancelable(true);

            dialog.show();*/
			
			final NumberPicker numPicker = new NumberPicker(v.getContext());
			numPicker.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			numPicker.setRange(1, totalPages);
			numPicker.setCurrent(page);
			numPicker.setSpeed(100);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(numPicker)
				   .setTitle("Jump to Page")
			       .setCancelable(true)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                //MyActivity.this.finish();
			        	   Intent myIntent = new Intent(getBaseContext(), CollectionList.class);
							myIntent.putExtra("COLLECTION_USERNAME", userName);
							myIntent.putExtra("COLLECTION_PAGE", numPicker.getCurrent());
							myIntent.putExtra("COLLECTION_FOLDER", folder);
							myIntent.putExtra("COLLECTION_CONSOLE", console);
							myIntent.putExtra("COLLECTION_TYPE", type);
							startActivityForResult(myIntent, 0);
			           }
			       })
			       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			int prevPage = page;

			if (v.getId() == R.id.NextPage2 && page < totalPages)
				page++;
			else if (v.getId() == R.id.PrevPage2 && page > 1)
				page--;
			
			if(page != prevPage) {
				Intent myIntent = new Intent(v.getContext(), CollectionList.class);
				myIntent.putExtra("COLLECTION_USERNAME", userName);
				myIntent.putExtra("COLLECTION_PAGE", page);
				myIntent.putExtra("COLLECTION_FOLDER", folder);
				myIntent.putExtra("COLLECTION_CONSOLE", console);
				myIntent.putExtra("COLLECTION_TYPE", type);
				startActivityForResult(myIntent, 0);
			}
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	  super.onListItemClick(l, v, position, id);
	  
	  try{
	  	Intent myIntent = new Intent(v.getContext(), GameInfoActivity.class);
		myIntent.putExtra("GAMEINFO_RFGID", CollectionScraper.getCollectionPage(userName, folder, console, type, page).getList().get(position).getRFGID());
		startActivityForResult(myIntent, 0);
	  	} catch (Exception e) { }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.collectionmenu, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.filters:
	    	
	    	LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	View collectionFilter = vi.inflate(R.layout.collection_filter, null);

	    	// Set up spinners
	    	final Spinner folderSpinner = (Spinner) collectionFilter.findViewById(R.id.folderSpinner);
	    	ArrayAdapter<String> folderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, folderList);
	    	folderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	folderSpinner.setAdapter(folderAdapter);
	    	folderSpinner.setSelection(folderAdapter.getPosition(folder));
	    	
	    	/*
	    	for(int i = 0; i < folderList.size(); i++) {
	    		if (folderList.get(i).equals(folder)) {
	    			folderSpinner.setSelection(i);
	    			break;
	    		}
	    	}
	    	*/
	    	
	    	final Spinner consoleSpinner = (Spinner) collectionFilter.findViewById(R.id.consoleSpinner);
	    	ArrayAdapter<String> consoleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, consoleList);
	    	consoleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	consoleSpinner.setAdapter(consoleAdapter);
	    	consoleSpinner.setSelection(consoleIdList.indexOf(console));
	    	
	    	final Spinner typeSpinner = (Spinner) collectionFilter.findViewById(R.id.typeSpinner);
	    	ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeList);
	    	typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	typeSpinner.setAdapter(typeAdapter);
	    	if (type.equals("S"))
	    		typeSpinner.setSelection(1);
	    	else if (type.equals("H"))
	    		typeSpinner.setSelection(2);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(collectionFilter)
				   .setTitle("Collection Filters")
			       .setCancelable(true)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                //MyActivity.this.finish();			        	   
			        	   Intent myIntent = new Intent(getBaseContext(), CollectionList.class);
							myIntent.putExtra("COLLECTION_USERNAME", userName);
							myIntent.putExtra("COLLECTION_PAGE", 1);
							myIntent.putExtra("COLLECTION_FOLDER", folderList.get(folderSpinner.getSelectedItemPosition()));
							if(consoleSpinner.getSelectedItemPosition() > 0)
								myIntent.putExtra("COLLECTION_CONSOLE", consoleIdList.get(consoleSpinner.getSelectedItemPosition()));
							if(typeSpinner.getSelectedItemPosition() == 1)
								myIntent.putExtra("COLLECTION_TYPE", "S");
							else if (typeSpinner.getSelectedItemPosition() == 2)
								myIntent.putExtra("COLLECTION_TYPE", "H");
							startActivityForResult(myIntent, 0);
			           }
			       })
			       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
			
	        return true;
	    case R.id.home:
	    	Intent myIntent = new Intent(this, RFGeneration.class);
			startActivityForResult(myIntent, 0);
	        return true;
	    case R.id.refresh:
	    	Intent myIntent2 = new Intent(this, CollectionList.class);
			myIntent2.putExtra("COLLECTION_USERNAME", userName);
			myIntent2.putExtra("COLLECTION_PAGE", page);
			myIntent2.putExtra("COLLECTION_FOLDER", folder);
			myIntent2.putExtra("COLLECTION_CONSOLE", console);
			myIntent2.putExtra("COLLECTION_TYPE", type);
			myIntent2.putExtra("COLLECTION_REFRESH", true);
			startActivityForResult(myIntent2, 0);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

