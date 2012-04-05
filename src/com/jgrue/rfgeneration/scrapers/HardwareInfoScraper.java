package com.jgrue.rfgeneration.scrapers;

import java.net.URL;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.jgrue.rfgeneration.objects.GameInfo;

public class HardwareInfoScraper {
	private static final String TAG = "HardwareInfoScraper";
	
	public static GameInfo scrapeHardwareInfo(String rfgid) throws Exception {
		GameInfo gameInfo = new GameInfo();
		gameInfo.setRFGID(rfgid);
		
		URL url = new URL("http://www.rfgeneration.com/PHP/gethwinfo.php?ID=" + rfgid);
		Log.i(TAG, "Target URL: " + url.toString());
		Document document = Jsoup.parse(url, 30000);
		Log.i(TAG, "Retrieved URL: " + document.baseUri());
		
		Elements tables = document.select("table tr:eq(3) td:eq(0) table.bordercolor tr td table.windowbg2 tr:eq(3) td table");
		
		Element table = tables.get(0);
		Elements tableRows = table.select("tr");
		 
		Element title = document.select("div.headline").get(0);
		gameInfo.setTitle(title.text());
		
		// Check for a variation title
		/*Pattern variantRegex = Pattern.compile("\\[.*\\]$");
		Matcher matcher = variantRegex.matcher(gameInfo.getTitle());
		if(matcher.find())
		{
			gameInfo.setVariationTitle(matcher.group().substring(1, matcher.group().length() - 1));
			gameInfo.setTitle(gameInfo.getTitle().substring(0, gameInfo.getTitle().length() - gameInfo.getVariationTitle().length() - 2));
		}*/
		
		for(int i = 0; i < tableRows.size(); i++) {
			 Elements tableData = tableRows.get(i).select("td");
			 if(tableData.size() < 2)
				 break;
			 
			 String field = tableData.get(0).text();
			 String value = tableData.get(1).text();
			 
			 if(field.contains("Console"))
				 gameInfo.setConsole(value);
			 else if(field.contains("Region"))
				 gameInfo.setRegion(tableData.get(1).select("img").first().attr("src").substring(18, 19));
			 else if(field.contains("Year"))
				 gameInfo.setYear(Integer.parseInt(value));
			 else if(field.contains("Part"))
				 gameInfo.setPartNumber(value);
			 else if(field.contains("UPC"))
			 	 gameInfo.setUPC(value);
		 	 else if(field.contains("Publisher"))
		 		 gameInfo.setPublisher(value);
		 	 else if(field.contains("Developer"))
		 		 gameInfo.setDeveloper(value);
		 	 else if(field.contains("Rating"))
		 		 gameInfo.setRating(value);
		 	 else if(field.contains("Genre"))
		 		 gameInfo.setGenre(value);
		 	 else if(field.contains("Sub-genre"))
		 		 gameInfo.setSubGenre(value);
		 	 else if(field.contains("Players"))
		 		 gameInfo.setPlayers(value);
		 	 else if(field.contains("Controller"))
		 		 gameInfo.setControlScheme(value);
		 	 else if(field.contains("Media Format"))
		 		 gameInfo.setMediaFormat(value);
		 	 else if(field.contains("Alternate Title"))
		 		 gameInfo.setAlternateTitle(value);
		}
		
		table = tables.get(tables.size() - 3);
		tableRows = table.select("tr");
		 
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> credits = new ArrayList<String>();
		 
		try {
			for(int i = 0; i < tableRows.size(); i++) {
				Elements tableData = tableRows.get(i).select("td");
				names.add(tableData.get(0).text());
				credits.add(tableData.get(1).text());
			}
		} catch (Exception e)
		{
			names.add("");
			credits.add("Error while loading credits.");
			Log.e(TAG, "Error while loading credits.");
		}
		 
		gameInfo.setNameList(names);
		gameInfo.setCreditList(credits);
		
		return gameInfo;
	}
}
