package com.jgrue.rfgeneration.scrapers;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.objects.Game;

public class SearchScraper {
	private static final String TAG = "SearchScraper";
	
	public static List<Game> getSearchPage(Context ctx, String query, int page, boolean isUpc) throws Exception {
		List<Game> gameList = new ArrayList<Game>();
		
		try {
			Elements tableRows;
			
			if(!isUpc) {
				// Get the HTML page and parse it with jsoup.
				Uri url = Uri.parse(Constants.FUNCTION_SEARCH).buildUpon()
						.appendQueryParameter(Constants.PARAM_QUERY, query)
						.appendQueryParameter(Constants.PARAM_FIRST_RESULT, Integer.toString(getFirstResult(page)))
						.build();
				
				Log.i(TAG, "Target URL: " + url);
				
				Document document = Jsoup.connect(url.toString())
					.timeout(Constants.TIMEOUT)
					.get();
				
				Log.i(TAG, "Retrieved URL: " + document.baseUri());
			 
				// Load the results table into Game objects.
				Element table = document.select("table > tbody > tr:eq(3) > td:eq(1) > table.bordercolor > tbody > tr.windowbg2 > td.windowbg2 > table > tbody > tr:eq(1) > td.windowbg2 > table.bordercolor").get(0);
				tableRows = table.select("tr:gt(0)");
			} else {
				Uri url = Uri.parse(Constants.FUNCTION_SEARCH_UPC).buildUpon()
						.appendQueryParameter(Constants.PARAM_PAGE, Integer.toString(page + 1))
						.appendQueryParameter(Constants.PARAM_BARCODE, query)
						.build();
				
				Log.i(TAG, "Target URL: " + url);
				
				Document document = Jsoup.connect(url.toString())
					.cookie(Constants.LOGIN_COOKIE, LoginScraper.getCookie(ctx))
					.timeout(Constants.TIMEOUT)
					.get();
				
				Log.i(TAG, "Retrieved URL: " + document.baseUri());
				
				Element table = document.select("table > tbody > tr:eq(3) > td:eq(1) > table.bordercolor > tbody > tr > td > table.windowbg2 > tbody > tr:eq(1) > td > table.bordercolor").get(0);
				tableRows = table.select("tr:gt(0)");
			}
			
			for(int i = 0; i < tableRows.size(); i++) {
				Elements tableData = tableRows.get(i).select("td");
				
				Game newGame = new Game();
				String href = tableData.get(3).select("a").first().attr("href");
				newGame.setRFGID(href.substring(href.indexOf("ID=") + 3));
				newGame.setConsole(tableData.get(0).text());
				if(!isUpc)
					newGame.setRegion(tableData.get(1).select("img").first().attr("title"));
				else {
					String src = tableData.get(1).select("img").first().attr("src");
					newGame.setRegion(src.substring(src.lastIndexOf("/") + 1, src.lastIndexOf(".")));
				}
				newGame.setType(tableData.get(2).text());
				newGame.setTitle(tableData.get(3).text());
				newGame.setPublisher(tableData.get(4).text());
				try { newGame.setYear(Integer.parseInt(tableData.get(5).text())); } catch (Exception e) { }
				newGame.setGenre(tableData.get(6).text());
				gameList.add(newGame);
			}
		} catch (Exception e) { }
		
		return gameList;
	}
	
	private static int getFirstResult(int page) {
		return page * 50 + 1;
	}
	
	public static int getTotalPages(Context ctx, String query, boolean isUpc) {
		int numPages = 0;
		
		// Get the HTML page and parse it with jsoup. 
		try {
			if(!isUpc) {
				Document document = Jsoup.connect((isUpc ? Constants.FUNCTION_SEARCH_UPC : Constants.FUNCTION_SEARCH) + "&" + 
					Constants.PARAM_QUERY + "=" +  URLEncoder.encode(query))
					.timeout(Constants.TIMEOUT)
					.get();
				
				// Get the total number of pages in this folder.
				Element div = document.select("div.smalltext").get(1);
				String divText = div.text().substring(div.text().indexOf("of") + 3);
				numPages = (int) Math.ceil(Integer.parseInt(divText.substring(0, divText.indexOf(" "))) / 50.0);
			} else {
				Document document = Jsoup.connect(Constants.FUNCTION_SEARCH_UPC +
						Constants.PARAM_PAGE + "=1&" + Constants.PARAM_BARCODE + "=" + query)
					.cookie(Constants.LOGIN_COOKIE, LoginScraper.getCookie(ctx))
					.timeout(Constants.TIMEOUT)
					.get();
				
				// Get the total number of pages in this folder.
				Element div = document.select("div.smalltext").get(0);
				String divText = div.text().substring(div.text().indexOf("of") + 3);
				numPages = (int) Math.ceil(Integer.parseInt(divText.substring(0, divText.indexOf(" "))) / 30.0);
			}
		} catch (Exception e) { }
		
		return numPages;
	}
}
