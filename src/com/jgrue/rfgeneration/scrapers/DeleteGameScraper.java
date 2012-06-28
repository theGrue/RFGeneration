package com.jgrue.rfgeneration.scrapers;

import java.io.IOException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.util.Log;

import com.jgrue.rfgeneration.constants.Constants;

public class DeleteGameScraper {
	private static final String TAG = "DeleteGameScraper";
	
	public static boolean deleteGame(Context ctx, String rfgid, String folder) {
		try {
			Log.i(TAG, "Target URL: " + Constants.FUNCTION_DELETE_GAME + 
					"&" + Constants.PARAM_RFGID + "=" + rfgid +
					"&" + Constants.PARAM_FOLDER + "=" + URLEncoder.encode(folder));
			
			Document document = Jsoup.connect(Constants.FUNCTION_DELETE_GAME + 
					"&" + Constants.PARAM_RFGID + "=" + rfgid +
					"&" + Constants.PARAM_FOLDER + "=" + URLEncoder.encode(folder))
				.cookie(Constants.LOGIN_COOKIE, LoginScraper.getCookie(ctx))
				.timeout(Constants.TIMEOUT)
				.get();
			
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			
			return true;
		} catch (IOException e) { }
		return false;
	}
}
