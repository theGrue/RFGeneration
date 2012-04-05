package com.jgrue.rfgeneration;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.blundell.tut.LoaderImageView;
import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.data.RFGenerationProvider;
import com.jgrue.rfgeneration.objects.GameInfo;
import com.jgrue.rfgeneration.scrapers.GameInfoScraper;
import com.jgrue.rfgeneration.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;

public class GameInfoActivity extends Activity implements OnClickListener {
	private static final String TAG = "GameInfoActivity";
	private GameInfo gameInfo;
	private int imageIndex = 0;
	private boolean firstLoad = true;
	private String rfgId;
	private ProgressBar bar = null;
	private GameInfoTask task = null;
	 
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamedetail);
        
        bar = (ProgressBar)findViewById(R.id.collection_progress);
        task = (GameInfoTask)getLastNonConfigurationInstance();
        
        rfgId = getIntent().getStringExtra(Constants.INTENT_GAME_RFGID);
        if(rfgId == null && getIntent().getLongExtra(Constants.INTENT_GAME_ID, 0) > 0) {
        	// Look up the RFGID from the database.
        	ContentResolver db = getContentResolver();
        	Cursor cursor = db.query(Uri.withAppendedPath(RFGenerationProvider.GAMES_URI, Long.toString(getIntent().getLongExtra(Constants.INTENT_GAME_ID, 0))),
        			new String[] { "rfgid" }, null, null, null);
        	startManagingCursor(cursor);
        	if(cursor.moveToNext()) {
        		rfgId = cursor.getString(0);
        	}
        }
        
        if (task == null) {
            task = new GameInfoTask(this);
            task.execute(getIntent().getStringExtra(rfgId));
        } else {
        	task.attach(this);
            if (task.getProgress())
            	run();
        }
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
		task.detach();
		return(task);
    }
    
    static class GameInfoTask extends AsyncTask<String, Void, GameInfo> {
    	GameInfoActivity activity = null;
    	boolean done = false;
    	
    	GameInfoTask(GameInfoActivity activity) {
    		attach(activity);
	    }
    	
		@Override
		protected GameInfo doInBackground(String... params) {
			GameInfo gameInfo = new GameInfo();
			
			try {
				gameInfo = GameInfoScraper.getGameInfo(params[0]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return gameInfo;
		}
		
		@Override
		protected void onPostExecute(GameInfo gameInfo) {
			if (activity == null) {
				Log.w(TAG, "onPostExecute() skipped -- no activity");
		    } else {
		    	done = true;
				activity.run();
		    }
		}
		
		void detach() {
			activity = null;
	    }
		    
		void attach(GameInfoActivity activity) {
	    	this.activity = activity;
	    }
		
		boolean getProgress() {
			return done;
		}
    }

	        public void run() {
	            
	            try {
	    			gameInfo = GameInfoScraper.getGameInfo(rfgId);
	    		
		    		// Detail
		    		TextView console = (TextView) findViewById(R.id.gameDetailConsole);
		            if (console != null)
		            	console.setText(gameInfo.getConsole());
		            
		            TextView title = (TextView) findViewById(R.id.gameDetailTitle);
		            if(title != null)
		            {
		            	title.setText(Html.fromHtml("<b>" + gameInfo.getTitle() + "</b>"));
		            	//if(gameInfo.getVariationTitle() != null)
		               	//	title.setText(Html.fromHtml("<b>" + gameInfo.getTitle() + "</b> [" + gameInfo.getVariationTitle() + "]"));
		            }
		            
		            TextView alternateTitle = (TextView) findViewById(R.id.gameDetailAlternateTitle);
		            if(alternateTitle != null)
		            	alternateTitle.setText(gameInfo.getAlternateTitle());
	
		            TextView year = (TextView) findViewById(R.id.gameDetailYear);
		            if(year != null)
		            	year.setText("" + gameInfo.getYear());
		            
		            TextView rfgId = (TextView) findViewById(R.id.gameDetailRFGID);
		            if(rfgId != null)
		            	rfgId.setText(gameInfo.getRFGID());
		            
		            TextView part = (TextView) findViewById(R.id.gameDetailPart);
		            if(part != null)
		            	part.setText(gameInfo.getPartNumber());
		            
		            TextView upc = (TextView) findViewById(R.id.gameDetailUPC);
		            if(upc != null)
	            		upc.setText(gameInfo.getUPC());
	
		            TextView publisher = (TextView) findViewById(R.id.gameDetailPublisher);
		            if(publisher != null)
		            	publisher.setText(gameInfo.getPublisher());
		            
		            TextView developer = (TextView) findViewById(R.id.gameDetailDeveloper);
		            if(developer != null)
		            	developer.setText(gameInfo.getDeveloper());
		            
		            TextView rating = (TextView) findViewById(R.id.gameDetailRating);
		            if(rating != null)
		            	rating.setText(gameInfo.getRating());
		            
		            TextView genre = (TextView) findViewById(R.id.gameDetailGenre);
		            if(genre != null)
		            	genre.setText(gameInfo.getGenre());
		            
		            TextView subgenre = (TextView) findViewById(R.id.gameDetailSubgenre);
		            if(subgenre != null)
		            	subgenre.setText(gameInfo.getSubGenre());
		            
		            TextView players = (TextView) findViewById(R.id.gameDetailPlayers);
		            if(players != null)
		            	players.setText(gameInfo.getPlayers());
		            
		            TextView controller = (TextView) findViewById(R.id.gameDetailController);
		            if(controller != null)
		            	controller.setText(gameInfo.getControlScheme());
		            
		            TextView mediaFormat = (TextView) findViewById(R.id.gameDetailMediaFormat);
		            if(mediaFormat != null)
		            	mediaFormat.setText(gameInfo.getMediaFormat());
		            
		            ImageView region = (ImageView) findViewById(R.id.gameDetailRegion);
		            if(region != null) {
		            	if(gameInfo.getRegion().indexOf(",") == -1) {
	                		region.setImageDrawable(gameInfo.getRegionDrawable(region.getContext()));
	                	} else {                       		
	                		region.setImageDrawable(gameInfo.getRegionAnimation(region.getContext()));
	                	}
		            }
		            
		            // Credits
		            LinearLayout ll = (LinearLayout)findViewById(R.id.gameCreditsLayout);
		            
		            for(int i = 0; i < gameInfo.getNameList().size() && i < gameInfo.getCreditList().size(); i++) {
		                TextView name = new TextView(GameInfoActivity.this);
		                name.setText(gameInfo.getNameList().get(i));
		                name.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		                name.setTextColor(0xffcc0000);
		                name.setLayoutParams(new LayoutParams(
		                		android.view.ViewGroup.LayoutParams.FILL_PARENT,
		                		android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		                ll.addView(name);
		                
		                TextView credit = new TextView(GameInfoActivity.this);
		                credit.setText(gameInfo.getCreditList().get(i));
		                credit.setTextColor(Color.BLACK);
		                credit.setLayoutParams(new LayoutParams(
		                		android.view.ViewGroup.LayoutParams.FILL_PARENT,
		                		android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		                credit.setPadding((int)getResources().getDisplayMetrics().density * 10, 0, 0, (int)getResources().getDisplayMetrics().density * 5);
		                ll.addView(credit);
		            }
		            
		            ((TextView)findViewById(R.id.DetailDetail)).setOnClickListener(GameInfoActivity.this);
		            ((TextView)findViewById(R.id.DetailImages)).setOnClickListener(GameInfoActivity.this);
		            ((TextView)findViewById(R.id.DetailCredits)).setOnClickListener(GameInfoActivity.this);
		            ((ImageView)findViewById(R.id.gameImagesLeft)).setOnClickListener(GameInfoActivity.this);
		            ((ImageView)findViewById(R.id.gameImagesRight)).setOnClickListener(GameInfoActivity.this);
		            
		            try {
	            	setCurrentLayout(findViewById(R.id.DetailDetail));
	            	} catch (Exception e) { }
		            
	            } catch (Exception e) {
	    			Log.e("GameInfoActivity", "Error occurred while loading RFGID " + rfgId);
	    			
	    			TextView errorText = (TextView)findViewById(R.id.gameDetailError);
	    			if(errorText != null)
	    				errorText.setText("\n\nSorry, an error occurred while loading this game. " +
	    						"Please e-mail android@jgrue.com and report that an issue " +
	    						"occurred with the following RFGID:\n\n");
	    			
	    			EditText errorRFGID = (EditText)findViewById(R.id.gameDetailErrorRFGID);
	    			if(errorRFGID != null)
	    				errorRFGID.setText(rfgId);
	    			
	    			((ScrollView) findViewById(R.id.gameDetailScrollView)).setVisibility(View.GONE);
	    			((LinearLayout) findViewById(R.id.gameDetailLayout)).setVisibility(View.GONE);
	    			((RelativeLayout) findViewById(R.id.gameImagesLayout)).setVisibility(View.GONE);
	    			((LinearLayout) findViewById(R.id.gameCreditsLayout)).setVisibility(View.GONE);
	    			((LinearLayout) findViewById(R.id.gameErrorLayout)).setVisibility(View.VISIBLE);
	    		}
	            
	            findViewById(R.id.game_progress).setVisibility(View.GONE);
	        }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.gameImagesLeft || v.getId() == R.id.gameImagesRight) {
			if (v.getId() == R.id.gameImagesRight)
				imageIndex++;
			else if (v.getId() == R.id.gameImagesLeft)
				imageIndex--;
			
			if(imageIndex >= gameInfo.getImageTypes().size())
				imageIndex = 0;
			else if (imageIndex < 0)
				imageIndex = gameInfo.getImageTypes().size() - 1;
			
			setImage(imageIndex);
		} else {
			try {
				setCurrentLayout(v);
			} catch (Exception e) { }
		}
	}
	
	private void setCurrentLayout(View v) throws XmlPullParserException, IOException {
		// Reset everything.
		 Resources           resources  = getResources();
		 XmlResourceParser   parser     = resources.getXml(R.drawable.text_color);
		 ColorStateList      text       = ColorStateList.createFromXml(resources, parser);
		 Drawable text_bg = resources.getDrawable(R.drawable.text_bg_color);
		 int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());

		 if(!((TextView) findViewById(R.id.DetailDetail)).isClickable()) {
			((TextView) findViewById(R.id.DetailDetail)).setTextColor(text);
			((TextView) findViewById(R.id.DetailDetail)).setBackgroundDrawable(text_bg);
			((TextView) findViewById(R.id.DetailDetail)).setPadding(padding, padding, padding, padding);
			((TextView) findViewById(R.id.DetailDetail)).setClickable(true);
		 }

		 if(!((TextView) findViewById(R.id.DetailImages)).isClickable()) {
			((TextView) findViewById(R.id.DetailImages)).setTextColor(text);
			((TextView) findViewById(R.id.DetailImages)).setBackgroundDrawable(text_bg);
			((TextView) findViewById(R.id.DetailImages)).setPadding(padding, padding, padding, padding);
			((TextView) findViewById(R.id.DetailImages)).setClickable(true);
		 }
		
		 if(!((TextView) findViewById(R.id.DetailCredits)).isClickable()) {
			((TextView) findViewById(R.id.DetailCredits)).setTextColor(text);
			((TextView) findViewById(R.id.DetailCredits)).setBackgroundDrawable(text_bg);
			((TextView) findViewById(R.id.DetailCredits)).setPadding(padding, padding, padding, padding);
			((TextView) findViewById(R.id.DetailCredits)).setClickable(true);
		 }
	
		((ScrollView) findViewById(R.id.gameDetailScrollView)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.gameDetailLayout)).setVisibility(View.GONE);
		((RelativeLayout) findViewById(R.id.gameImagesLayout)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.gameCreditsLayout)).setVisibility(View.GONE);
		
		if(v.getId() == R.id.DetailDetail) {
			((TextView) findViewById(R.id.DetailDetail)).setTextColor(0xffcc0000);
			((TextView) findViewById(R.id.DetailDetail)).setBackgroundColor(0xffeeeeee);
			((TextView) findViewById(R.id.DetailDetail)).setPadding(padding, padding, padding, padding);
			((TextView) findViewById(R.id.DetailDetail)).setClickable(false);
			((ScrollView) findViewById(R.id.gameDetailScrollView)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.gameDetailLayout)).setVisibility(View.VISIBLE);
		} else if(v.getId() == R.id.DetailImages) {
			((TextView) findViewById(R.id.DetailImages)).setTextColor(0xffcc0000);
			((TextView) findViewById(R.id.DetailImages)).setBackgroundColor(0xffeeeeee);
			((TextView) findViewById(R.id.DetailImages)).setPadding(padding, padding, padding, padding);
			((TextView) findViewById(R.id.DetailImages)).setClickable(false);
			((RelativeLayout) findViewById(R.id.gameImagesLayout)).setVisibility(View.VISIBLE);
            if(firstLoad && gameInfo.getImageTypes().size() > 0) setImage(0);
            firstLoad = false;
		} else if(v.getId() == R.id.DetailCredits) {
			((TextView) findViewById(R.id.DetailCredits)).setTextColor(0xffcc0000);
			((TextView) findViewById(R.id.DetailCredits)).setBackgroundColor(0xffeeeeee);
			((TextView) findViewById(R.id.DetailCredits)).setPadding(padding, padding, padding, padding);
			((TextView) findViewById(R.id.DetailCredits)).setClickable(false);
			((ScrollView) findViewById(R.id.gameDetailScrollView)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.gameCreditsLayout)).setVisibility(View.VISIBLE);
		}
	}
	
	private void setImage(int index) {
		String imageType = gameInfo.getImageTypes().get(index);
		String rfgId = gameInfo.getRFGID();
		String folder = rfgId.substring(0, 5);
		
		LoaderImageView image = (LoaderImageView) findViewById(R.id.gameImagesImage);
		image.setImageDrawable("http://www.rfgeneration.com/images/games/" + 
				folder + "/" + imageType + "/" + rfgId + ".jpg");
		
		TextView caption = (TextView) findViewById(R.id.gameImagesText);
		if (imageType.equals("bf"))
			caption.setText("Box Front");
		else if (imageType.equals("bb"))
			caption.setText("Box Back");
		else if (imageType.equals("gs"))
			caption.setText("Game");
		else if (imageType.equals("ms"))
			caption.setText("Manual");
		else if (imageType.equals("ss"))
			caption.setText("Screenshot");
	}
}
