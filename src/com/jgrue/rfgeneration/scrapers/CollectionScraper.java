package com.jgrue.rfgeneration.scrapers;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jgrue.rfgeneration.objects.CollectionPage;
import com.jgrue.rfgeneration.objects.Console;
import com.jgrue.rfgeneration.objects.Game;

public class CollectionScraper {
	private static HashMap<String, CollectionPage> collectionPages = null;
	
	public static CollectionPage getCollectionPage(String userName, String folder, String console, String type, int page) throws Exception {
		if(collectionPages == null) {
			collectionPages = new HashMap<String, CollectionPage>();
		}
		
		String threadMapKey = userName + "|" + folder + "|" + console + "|" + type + "|" + page;
		
		if(collectionPages.containsKey(threadMapKey)) {
			return collectionPages.get(threadMapKey);
		} else {
			CollectionPage newPage = scrapeCollectionPage(userName, folder, console, type, page);
			collectionPages.put(threadMapKey, newPage);
			return newPage;
		}	
	}
	
	private static CollectionPage scrapeCollectionPage(String userName, String folder, String console, String type, int page) throws Exception {
		 ArrayList<Game> gameList = new ArrayList<Game>();
		 Pattern variantRegex = Pattern.compile(" \\[.*\\]$");
		 
		 // Get the HTML page and parse it with jsoup.
		 URL url = new URL("http://www.rfgeneration.com/cgi-bin/collection.pl?name=" + userName + "&folder=" + URLEncoder.encode(folder, "ISO-8859-1") + "&firstresult=" + getFirstResult(page) + "&console=" + console + "&type=" + type);
		 Document document = Jsoup.parse(url, 30000);
		 
		 // Create the CollectionPage object and set some key info.
		 CollectionPage collectionPage = new CollectionPage();
		 collectionPage.setUsername(userName);
		 collectionPage.setFolder(folder);
		 collectionPage.setConsole(console);
		 collectionPage.setType(type);
		 collectionPage.setPage(page);
		 
		 // Load collection table into Game objects.
		 Element table = document.select("table > tr:eq(3) > td:eq(1) > table:eq(2) > tr:eq(1) > td > form > table").get(0);
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
				 try { newGame.setYear(Integer.parseInt(tableData.get(5).text())); } catch (Exception e) { }
				 newGame.setGenre(tableData.get(6).text());
				 newGame.setGameQuantity(Integer.parseInt(tableData.get(7).text()));
				 newGame.setBoxQuantity(Integer.parseInt(tableData.get(8).text()));
				 newGame.setManualQuantity(Integer.parseInt(tableData.get(9).text()));
				 
				 // Check for a variation title
				 Matcher matcher = variantRegex.matcher(newGame.getTitle());
				 if(matcher.find()) {
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
		 
		 // Get information from filtering select boxes. 
		 Elements selects = document.select("select");
		 Element folders = selects.get(1);
		 Element consoles = selects.get(2);
		 Element types = selects.get(3);
		 
		 ArrayList<String> folderNames = new ArrayList<String>();
		 for(int i = 0; i < folders.children().size(); i++) {
			 folderNames.add(folders.child(i).attr("value"));
		 }
		 collectionPage.setFolderList(folderNames);
		 
		 ArrayList<Console> consoleNames = new ArrayList<Console>();
		 for(int i = 0; i < consoles.children().size(); i++) {
			 Console newConsole = new Console();
			 newConsole.setName(consoles.child(i).text());
			 newConsole.setId(consoles.child(i).attr("value"));
			 consoleNames.add(newConsole);
		 }
		 collectionPage.setConsoleList(consoleNames);
		 
		 ArrayList<String> typeNames = new ArrayList<String>();
		 for(int i = 0; i < types.children().size(); i++) {
			 typeNames.add(types.child(i).text());
		 }
		 collectionPage.setTypeList(typeNames);
		 
		 return collectionPage;
	}
	
	private static int getFirstResult(int page) {
		return (page - 1) * 50 + 1;
	}
	
	private static int getTotalPages(int num) {
		return (int) Math.ceil(num / 50.0);
	}
	
	public static void refresh() {
		collectionPages = new HashMap<String, CollectionPage>();
	}
}
