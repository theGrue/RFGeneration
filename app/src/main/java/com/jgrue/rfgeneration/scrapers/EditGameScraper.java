package com.jgrue.rfgeneration.scrapers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;

import android.content.Context;

import com.jgrue.rfgeneration.constants.Constants;

public class EditGameScraper {
	private static final String TAG = "EditGameScraper";
	
	public static boolean editGame(Context ctx, String rfgid, String folder, float qty, float box, float man) {
		try {
			Map<String, String> postData = new HashMap<String, String>();
			
			// Tell collection.pl to update this game.
			postData.put("update", "Update");
			postData.put(Constants.PARAM_FOLDER, folder);
			postData.put(Constants.PARAM_RFGID, rfgid);
			
			// Set quantities.
			postData.put("QUANTITY", Float.toString(qty));
			postData.put("BOXES", Float.toString(box));
			postData.put("MANUALS", Float.toString(man));
			postData.put("COMMENTS", "");
			postData.put("RATING", "");
			
			/*Document document =*/ Jsoup.connect(Constants.FUNCTION_EDIT_GAME)
				.cookie(Constants.LOGIN_COOKIE, LoginScraper.getCookie(ctx))
				.data(postData)
				.method(Method.POST)
				.timeout(Constants.TIMEOUT)
				.post();
			
			//Element addText = document.select("table > tbody > tr:eq(3) > td:eq(1) > table.bordercolor > tbody > tr:eq(0) > td.windowbg2 > table > tbody > tr > td").get(2);
			//String message = addText.text().substring(0, addText.text().indexOf(" Back"));
			return true;
		} catch (IOException e) { }
		return false;
	}
}