package com.jgrue.rfgeneration.scrapers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.objects.Folder;

public class FolderScraper {
	private static final String TAG = "FolderScraper";
	
	public static List<Folder> getCollectionFolders(Context ctx) {
		List<Folder> folderList = new ArrayList<Folder>();
		
		try {
			// Get the HTML page and parse it with jsoup. Requires login!
			Log.i(TAG, "Target URL: " + Constants.FUNCTION_FOLDERS);
			Document document = Jsoup.connect(Constants.FUNCTION_FOLDERS)
				.cookie(Constants.LOGIN_COOKIE, LoginScraper.getCookie(ctx))
				.timeout(Constants.TIMEOUT)
				.get();
			Log.i(TAG, "Retrieved URL: " + document.baseUri());
			if(!Constants.FUNCTION_FOLDERS.equals(document.baseUri()))
				Log.e(TAG, "URL mismatch!");

			Element table = document.select("table > tbody > tr:eq(3) > td:eq(1) > table.bordercolor > tbody > tr:eq(0) > td.windowbg2 > table > tbody > tr:eq(2) > td > table.bordercolor").get(0);
			Elements tableRows = table.select("tr.windowbg");
			
			for(int i = 0; i < tableRows.size(); i++) {
				 Elements tableData = tableRows.get(i).select("td.normaltext");
				 Folder newFolder = new Folder();
				 
				 newFolder.setName(tableData.get(0).text());
				 newFolder.setOwned(tableData.get(4).text().equals("Yes"));
				 newFolder.setForSale(tableData.get(5).text().equals("Yes"));
				 newFolder.setPrivate(tableData.get(6).text().split(" ")[0].equals("Private"));
				 
				 folderList.add(newFolder);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return folderList;
	}
}
