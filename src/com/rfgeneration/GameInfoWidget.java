package com.rfgeneration;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class GameInfoWidget extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.gameinfo);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, GameInfoActivity.class);
	    intent.putExtra("GAMEINFO_RFGID", getIntent().getStringExtra("GAMEINFO_RFGID"));

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("details").setIndicator("Details",
	                      res.getDrawable(R.drawable.ic_tab_game))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, GameInfoActivity.class);
	    intent.putExtra("GAMEINFO_RFGID", getIntent().getStringExtra("GAMEINFO_RFGID"));
	    spec = tabHost.newTabSpec("images").setIndicator("Images",
	                      res.getDrawable(R.drawable.ic_tab_images))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, GameCredits.class);
	    intent.putExtra("GAMEINFO_RFGID", getIntent().getStringExtra("GAMEINFO_RFGID"));
	    spec = tabHost.newTabSpec("credits").setIndicator("Credits",
	                      res.getDrawable(R.drawable.ic_tab_credits))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	}
}
