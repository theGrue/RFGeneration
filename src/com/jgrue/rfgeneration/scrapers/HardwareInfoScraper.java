package com.jgrue.rfgeneration.scrapers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.net.Uri;
import android.util.Log;

import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.objects.GameInfo;

public class HardwareInfoScraper {
	private static final String TAG = "HardwareInfoScraper";
	
	public static GameInfo scrapeHardwareInfo(String rfgid) throws Exception {
		Uri url = Uri.parse(Constants.FUNCTION_HARDWARE_INFO).buildUpon()
				.appendQueryParameter(Constants.PARAM_RFGID, rfgid)
				.build();
		
		Log.i(TAG, "Target URL: " + url);
		Document document = Jsoup.connect(url.toString())
			.timeout(Constants.TIMEOUT)
			.get();
		Log.i(TAG, "Retrieved URL: " + document.baseUri());
		
		Elements tableRows = document.select("table > tbody > tr:eq(3) > td > table.bordercolor > tbody > tr > td > table.windowbg2 > tbody > tr:eq(3) > td > table").first().select("tr");
		Map<String, String> properties = new HashMap<String, String>();
		
		for(int i = 0; i < tableRows.size(); i++) {
			 Elements tableData = tableRows.get(i).select("td");
			 
			 // Unlike games, the entire page is in this table. If there aren't two cells, we've read far enough.
			 if(tableData.size() < 2)
				 break;
			 
			 String field = tableData.get(0).text().trim().replace(":", "");
			 String value = tableData.get(1).text().trim();
			 
			 if(field.length() == 0)
				 continue;
			 else if(field.equals("RFG ID#"))
				 field = GameInfo.RFGID;
			 else if(field.equals("Part Number"))
				 field = GameInfo.PART_NUMBER;
			 else if(field.equals("Manufacturer"))
				 field = GameInfo.PUBLISHER;
			 
			 if(GameInfo.REGION.equals(field)) {
				 StringBuilder sb = new StringBuilder();
				 Elements regions = tableData.select("img");
				 for(int j = 0; j < regions.size(); j++) {
					 String source = regions.get(j).attr("src");
					 sb.append(source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf(".")));
					 if (j < regions.size()) sb.append(',');
				 }
				 properties.put(field, sb.toString());
			 } else if (value.length() > 0) {
				 properties.put(field, value);
			 }
		}
		
		// Set the title and details.
		GameInfo gameInfo = new GameInfo(properties);
		gameInfo.setType("H");
		gameInfo.setTitle(document.select("tr#title div.headline").first().html()
				.replace("<sup>", " ").replace("</sup>", "").replace("\r", "").replace("\n", ""));
		
		// Page Credits are stored in a table in the last row.
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> credits = new ArrayList<String>();
		 
		try {
			int startIndex = tableRows.size() - 1;
			for(int i = tableRows.size() - 1; i >= 0; i--) {
				Elements tableData = tableRows.get(i).select("td");
				if(tableData.size() == 2)
					startIndex = i;
				else
					break;
			}
			
			for(int i = startIndex; i < tableRows.size(); i++) {
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
		
		// Load the state of which images are present for this game.
		tableRows = document.select("tr#title > td:eq(1) > table.bordercolor td");
		ArrayList<String> imageTypes = new ArrayList<String>();

		if(tableRows.size() == 4) {
			if(tableRows.get(0).select("a").size() > 0)
				 imageTypes.add("bf");
			 if(tableRows.get(1).select("a").size() > 0)
				 imageTypes.add("bb");
			 if(tableRows.get(2).select("a").size() > 0)
				 imageTypes.add("ss");
			 if(tableRows.get(3).select("a").size() > 0)
				 imageTypes.add("ms");
		}
		 
		gameInfo.setImageTypes(imageTypes);
		
		return gameInfo;
	}
}
