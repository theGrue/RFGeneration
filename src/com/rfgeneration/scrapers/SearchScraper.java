package com.rfgeneration.scrapers;

import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rfgeneration.objects.CollectionPage;
import com.rfgeneration.objects.Game;

public class SearchScraper {
	
	public static CollectionPage getSearchPage(String query, int page) throws Exception {
		ArrayList<Game> gameList = new ArrayList<Game>();
		 Pattern variantRegex = Pattern.compile(" \\[.*\\]$");
		 
		 // Get the HTML page and parse it with jsoup.
		 URL url = new URL("http://www.rfgeneration.com/cgi-bin/search.pl?search=true&inputtype=title&query=" + query + "&firstresult=" + getFirstResult(page));
		 Document document = Jsoup.parse(url, 3000);
		 
		 // Create the CollectionPage object and set some key info.
		 CollectionPage collectionPage = new CollectionPage();
		 collectionPage.setPage(page);
		 
		 // Load the results table into Game objects.
		 Element table = document.select("table > tr:eq(3) > td:eq(1) > table.bordercolor > tr.windowbg2 > td.windowbg2 > table > tr:eq(1) > td.windowbg2 > table.bordercolor").get(0);
		 Elements tableRows = table.select("tr:gt(0)");
		 
		 try {
			 for(int i = 0; i < tableRows.size(); i++) {
				 Elements tableData = tableRows.get(i).select("td");
				 Game newGame = new Game();
				 
				 newGame.setConsole(tableData.get(0).text());
				 newGame.setRegion(tableData.get(1).select("img").first().attr("title"));
				 newGame.setType(tableData.get(2).text());
				 newGame.setRFGID(tableData.get(3).select("a").first().attr("href").substring(14));
				 newGame.setTitle(tableData.get(3).text());
				 newGame.setPublisher(tableData.get(4).text());
				 newGame.setYear(Integer.parseInt(tableData.get(5).text()));
				 newGame.setGenre(tableData.get(6).text());
				 
				 // Check for a variation title
				 Matcher matcher = variantRegex.matcher(newGame.getTitle());
				 if(matcher.find())
				 {
					 newGame.setVariationTitle(matcher.group().substring(2, matcher.group().length() - 1));
					 newGame.setTitle(newGame.getTitle().substring(0, newGame.getTitle().length() - newGame.getVariationTitle().length() - 3));
				 }
				 
				 gameList.add(newGame);
			 }
		 } catch (Exception e) {}
		 collectionPage.setList(gameList);
		 
		 // Get the total number of pages in this folder.
		 try {
			 Element div = document.select("div.smalltext").get(1);
			 String divText = div.text().substring(div.text().indexOf("of") + 3);
			 collectionPage.setTotalPages(getTotalPages(Integer.parseInt(divText.substring(0, divText.indexOf(" ")))));
		 } catch (Exception e) {
			 collectionPage.setTotalPages(1);
		 }
		 
		 return collectionPage;
	}
	
	private static int getFirstResult(int page) {
		return (page - 1) * 50 + 1;
	}
	
	private static int getTotalPages(int num) {
		return (int) Math.ceil(num / 50.0);
	}
}
