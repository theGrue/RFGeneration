package com.rfgeneration;

import java.util.HashMap;
import java.util.List;

import com.rfgeneration.objects.CollectionPage;
import com.rfgeneration.objects.Game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class Collection extends Activity implements OnClickListener, Runnable {
	private int page = 1;
	private HashMap<String, CollectionPage> collectionPages;
	private ProgressDialog pd;
	private CollectionPage threadCollectionPage;
	private String threadMapKey;
	private String userName;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);
        
        ((Button)findViewById(R.id.NextPage)).setOnClickListener(this);
        ((Button)findViewById(R.id.PrevPage)).setOnClickListener(this);
        collectionPages = new HashMap<String, CollectionPage>();
        
        Intent myIntent = getIntent(); // this is just for example purpose
        userName = myIntent.getStringExtra("COLLECTION_USERNAME");
        
        try
        {
	        displayCollection(userName, "Collection", "", "", page);
        } catch (Exception e) { }
    }
    
    private void displayCollection(String userName, String folder, String console, String type, int page) throws Exception {
    	threadMapKey = userName + "|" + folder + "|" + console + "|" + type + "|" + page;
    	
    	if(!collectionPages.containsKey(threadMapKey))	{
    		
    		pd = ProgressDialog.show(this, "", "Now loading...", true,
    		
    		                                false);
    		
    		 
    		
    		                Thread thread = new Thread(this);
    		
    		                thread.start();
    		                
    	}
    	else
    	{
    		displayCollection(collectionPages.get(threadMapKey));
    	}
    }
    
    private void displayCollection(CollectionPage collectionPage) throws Exception {
    	List<Game> gameList = collectionPage.getList();
    	
    	/* Find Tablelayout defined in main.xml */
        TableLayout tl = (TableLayout)findViewById(R.id.myTableLayout);
        if(tl.getChildCount() > 0)
        	tl.removeViews(0, tl.getChildCount());
        
        try
        {
	        for(int i = 0; i < gameList.size(); i++)
	        {
	             /* Create a new row to be added. */
	             TableRow tr = new TableRow(this);
	             tr.setLayoutParams(new LayoutParams(
	                            android.view.ViewGroup.LayoutParams.FILL_PARENT,
	                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	             
	             TextView console = new TextView(this);
	             console.setText(" " + gameList.get(i).getConsoleAbbv() + " ");
	             console.setTextColor(Color.BLACK);
	             tr.addView(console);
	             
	             //TextView region = new TextView(this);
	             //region.setText(gameList.get(i).getRegion());
	             //tr.addView(region);
	             
	             //TextView type = new TextView(this);
	             //type.setText(gameList.get(i).getType());
	             //tr.addView(type);
	             
	             TextView title = new TextView(this);
	             title.setText(gameList.get(i).getTitle());
	             if(gameList.get(i).getVariationTitle() != null)
	            	 title.setText(title.getText() + " [" + gameList.get(i).getVariationTitle() + "]");
	             title.setTextColor(Color.BLACK);
	             title.setTypeface(Typeface.SANS_SERIF, 1);
	             //title.setSingleLine();
	             //title.setHorizontallyScrolling(true);
	             tr.addView(title);
	             
	             TextView qty = new TextView(this);
	             qty.setText("G:" + gameList.get(i).getGameQuantity() + " ");
	             qty.setTextColor(Color.BLACK);
	             tr.addView(qty);
	             
	             TextView box = new TextView(this);
	             box.setText("B:" + gameList.get(i).getBoxQuantity() + " ");
	             box.setTextColor(Color.BLACK);
	             tr.addView(box);
	             
	             TextView man = new TextView(this);
	             man.setText("M:" + gameList.get(i).getManualQuantity() + " ");
	             man.setTextColor(Color.BLACK);
	             tr.addView(man);
	             
	             if (i % 2 == 0)
	            	 tr.setBackgroundColor(0xffeeeeee);
	             else
	            	 tr.setBackgroundColor(0xffdddddd);
	             
	             tr.setFocusable(true);
	             
		         /* Add row to TableLayout. */
		         tl.addView(tr,new TableLayout.LayoutParams(
	                  android.view.ViewGroup.LayoutParams.FILL_PARENT,
	                  android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		         
		         tr = new TableRow(this);
	             tr.setLayoutParams(new LayoutParams(
	                            android.view.ViewGroup.LayoutParams.FILL_PARENT,
	                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	             
		         //TextView blank = new TextView(this);
		         //blank.setText("");
		         //tr.addView(blank);
		         
		         ImageView region = new ImageView(this);
		         if(gameList.get(i).getRegion().equals("U"))
		        	 region.setImageDrawable(getResources().getDrawable(R.drawable.regions_u));
		         else if (gameList.get(i).getRegion().equals("J"))
		        	 region.setImageDrawable(getResources().getDrawable(R.drawable.regions_j));
		         tr.addView(region);
		         
		         TextView publisher = new TextView(this);
		         if(gameList.get(i).getType().equals("S"))
		        	 publisher.setText(gameList.get(i).getPublisher() + ", " + gameList.get(i).getYear());
		         else
		        	 publisher.setText("[" + gameList.get(i).getType() + "] " + gameList.get(i).getPublisher() + ", " + gameList.get(i).getYear());
		         publisher.setTextColor(Color.BLACK);
		         tr.addView(publisher);
		         
		         //TextView blank2 = new TextView(this);
		         //blank2.setText("");
		         //tr.addView(blank2);
		         
		         //TextView blank3 = new TextView(this);
		         //blank3.setText("");
		         //tr.addView(blank3);
		         
		         //TextView blank4 = new TextView(this);
		         //blank4.setText("");
		         //tr.addView(blank4);
		         
		         if (i % 2 == 0)
	            	 tr.setBackgroundColor(0xffeeeeee);
	             else
	            	 tr.setBackgroundColor(0xffdddddd);
		         
		         tr.setFocusable(true);
		         
		         tl.addView(tr,new TableLayout.LayoutParams(
		                  android.view.ViewGroup.LayoutParams.FILL_PARENT,
		                  android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		         
		         tl.setColumnShrinkable(1, true);
		         tl.setColumnStretchable(1, true);
	        }
        } catch (Exception e) { }
    }

	public void onClick(View v) {

		//Android 1.6 makes this nicer.
		if (v.getId() == R.id.NextPage)
			page++;
		else if (v.getId() == R.id.PrevPage && page > 1)
			page--;
		
		try
        {
	        displayCollection(userName, "Collection", "", "", page);
        } catch (Exception e) { }
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			threadCollectionPage = CollectionScraper.getCollectionPage(userName, "Collection", "", "", page);
			handler.sendEmptyMessage(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private Handler handler = new Handler() {
	
	                @Override
	
	                public void handleMessage(Message msg) {
	
	                        
	                        collectionPages.put(threadMapKey, threadCollectionPage);
	                    try {
							displayCollection(threadCollectionPage);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}    
	                	pd.dismiss();
	
	 
	
	               }
	
	        };
}