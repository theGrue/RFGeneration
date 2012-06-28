package com.jgrue.rfgeneration.scrapers;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.objects.Game;

public class SearchScraper {
	
	public static List<Game> getSearchPage(String query, int page) throws Exception {
		List<Game> gameList = new ArrayList<Game>();
		
		try {
			// Get the HTML page and parse it with jsoup.
			Document document = Jsoup.connect(Constants.FUNCTION_SEARCH + 
					"&" + Constants.PARAM_QUERY + "=" +  URLEncoder.encode(query) +
					"&" + Constants.PARAM_FIRST_RESULT + "=" + getFirstResult(page))
				.timeout(Constants.TIMEOUT)
				.get();
		 
			// Load the results table into Game objects.
			Element table = document.select("table > tbody > tr:eq(3) > td:eq(1) > table.bordercolor > tbody > tr.windowbg2 > td.windowbg2 > table > tbody > tr:eq(1) > td.windowbg2 > table.bordercolor").get(0);
			Elements tableRows = table.select("tr:gt(0)");

			for(int i = 0; i < tableRows.size(); i++) {
				Elements tableData = tableRows.get(i).select("td");
				
				Game newGame = new Game();
				String href = tableData.get(3).select("a").first().attr("href");
				newGame.setRFGID(href.substring(href.indexOf("=") + 1));
				newGame.setConsole(tableData.get(0).text());
				newGame.setRegion(tableData.get(1).select("img").first().attr("title"));
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
	
	public static int getTotalPages(String query) {
		int numPages = 0;
		
		// Get the HTML page and parse it with jsoup. 
		try {
			Document document = Jsoup.connect(Constants.FUNCTION_SEARCH + "&" + Constants.PARAM_QUERY + "=" +  URLEncoder.encode(query))
				.timeout(Constants.TIMEOUT)
				.get();
			
			// Get the total number of pages in this folder.
			Element div = document.select("div.smalltext").get(1);
			String divText = div.text().substring(div.text().indexOf("of") + 3);
			numPages = (int) Math.ceil(Integer.parseInt(divText.substring(0, divText.indexOf(" "))) / 50.0);
		} catch (Exception e) { }
		
		return numPages;
	}
}
