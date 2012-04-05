package com.jgrue.rfgeneration.scrapers;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jgrue.rfgeneration.constants.Constants;

public class LoginScraper {
	private static final String TAG = "LoginScraper";
	
	public static String getCookie(Context ctx) {
		Log.i(TAG, "Retrieving stored cookie.");
		return ctx.getSharedPreferences(Constants.PREFS_FILE, 0).getString(Constants.PREFS_COOKIE, "");
	}
	
	private static String getCookie(Context ctx, String userName, String password) {
		String loginCookie = null;
		
		try {
			Log.i(TAG, "Retrieving new cookie for " + userName + ".");
			Connection.Response res = Jsoup.connect(Constants.FUNCTION_LOGIN)
				.data(Constants.COOKIE_USERNAME, userName, Constants.COOKIE_PASSWORD, password)
				.method(Method.POST)
				.timeout(Constants.TIMEOUT)
				.execute();
			
			loginCookie = res.cookie(Constants.LOGIN_COOKIE);
			
			SharedPreferences settings = ctx.getSharedPreferences(Constants.PREFS_FILE, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(Constants.PREFS_COOKIE, loginCookie);
			editor.commit();
		} catch (IOException e) { }
		
		return loginCookie;
	}
	
	public static boolean validateLogin(Context ctx, String userName, String password) {
		return getCookie(ctx, userName, password) != null;
	}
}
