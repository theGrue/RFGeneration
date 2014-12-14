package com.jgrue.rfgeneration.scrapers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jgrue.rfgeneration.constants.Constants;

public class DeleteGameScraper {
	private static final String TAG = "DeleteGameScraper";
	
	public static boolean deleteGame(Context ctx, String rfgid, String folder) {
		try {
			Uri url = Uri.parse(Constants.FUNCTION_DELETE_GAME).buildUpon()
					.appendQueryParameter(Constants.PARAM_RFGID, rfgid)
					.appendQueryParameter(Constants.PARAM_FOLDER, folder)
					.build();
			
			Log.i(TAG, "Target URL: " + url);
			
			Document document = Jsoup.connect(url.toString())
				.cookie(Constants.LOGIN_COOKIE, LoginScraper.getCookie(ctx))
				.timeout(Constants.TIMEOUT)
				.get();
			
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			
			return true;
		} catch (IOException e) { }
		return false;
	}
}
