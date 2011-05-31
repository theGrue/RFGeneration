package com.rfgeneration;

import com.rfgeneration.objects.GameInfo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class GameCredits extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamecredits);
        
        GameInfo gameInfo = new GameInfo();
        
        try {
			gameInfo = GameInfoScraper.getGameInfo(getIntent().getStringExtra("GAMEINFO_RFGID"));
		} catch (Exception e) { }
        
        //TableLayout tl = (TableLayout)findViewById(R.id.gameCreditsTable);
        //tl.setShrinkAllColumns(true);
        //tl.setStretchAllColumns(true);
		
		LinearLayout ll = (LinearLayout)findViewById(R.id.gameCreditsTable);
        
        for(int i = 0; i < gameInfo.getNameList().size() && i < gameInfo.getCreditList().size(); i++) {
        	
        	//TableRow tr = new TableRow(this);
            //tr.setLayoutParams(new LayoutParams(
            //               android.view.ViewGroup.LayoutParams.FILL_PARENT,
            //               android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            
            TextView name = new TextView(this);
            name.setText(gameInfo.getNameList().get(i));
            name.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            name.setTextColor(0xffcc0000);
            //tr.addView(name);
            name.setLayoutParams(new LayoutParams(
            		android.view.ViewGroup.LayoutParams.FILL_PARENT,
            		android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.addView(name);
            
            TextView credit = new TextView(this);
            credit.setText(gameInfo.getCreditList().get(i));
            credit.setTextColor(Color.BLACK);
            //tr.addView(credit);
            credit.setLayoutParams(new LayoutParams(
            		android.view.ViewGroup.LayoutParams.FILL_PARENT,
            		android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            credit.setPadding((int)getResources().getDisplayMetrics().density * 10, 0, 0, (int)getResources().getDisplayMetrics().density * 5);
            ll.addView(credit);
            
            //tl.addView(tr,new TableLayout.LayoutParams(
			//	          android.view.ViewGroup.LayoutParams.FILL_PARENT,
			//	          android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        
	}
}
