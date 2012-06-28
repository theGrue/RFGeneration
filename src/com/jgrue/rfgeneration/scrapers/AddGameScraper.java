package com.jgrue.rfgeneration.scrapers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.util.Log;

import com.jgrue.rfgeneration.constants.Constants;

public class AddGameScraper {
	private static final String TAG = "AddGameScraper";
	
	public static boolean addGame(Context ctx, String rfgid, String folder, float qty, float box, float man) {
		try {
			Log.i(TAG, "Attempting to add " + rfgid + " to folder " + folder + ".");
			
			Map<String, String> postData = new HashMap<String, String>();
			postData.put(Constants.PARAM_USERNAME, ctx.getSharedPreferences(Constants.PREFS_FILE, 0).getString(Constants.PREFS_USERNAME, ""));
			
			// Tell collection.pl to add this game.
			postData.put("addaction", "Add " + folder);
			postData.put("addgamedetails", "Add Games");
			postData.put("adddetails", "added");
			postData.put("box", "|" + rfgid + "|");
			
			// Set quantities.
			postData.put(rfgid + " quantity", Float.toString(qty));
			postData.put(rfgid + " boxes", Float.toString(box));
			postData.put(rfgid + " manuals", Float.toString(man));
			
			
			Log.i(TAG, "Target URL: " + Constants.FUNCTION_ADD_GAME);
			Document document = Jsoup.connect(Constants.FUNCTION_ADD_GAME)
				.cookie(Constants.LOGIN_COOKIE, LoginScraper.getCookie(ctx))
				.data(postData)
				.method(Method.POST)
				.timeout(Constants.TIMEOUT)
				.post();
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			
			//Element addText = document.select("table > tbody > tr:eq(3) > td:eq(1) > table.bordercolor > tbody > tr:eq(0) > td.windowbg2 > table > tbody > tr > td").get(2);
			//String message = addText.text().substring(0, addText.text().indexOf(" Back"));
			return true;
		} catch (IOException e) { }
		return false;
	}
}
