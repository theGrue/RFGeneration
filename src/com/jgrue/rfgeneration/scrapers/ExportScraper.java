package com.jgrue.rfgeneration.scrapers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import com.jgrue.rfgeneration.constants.Constants;
import com.jgrue.rfgeneration.objects.Game;

import android.content.Context;
import android.content.SharedPreferences;
import au.com.bytecode.opencsv.CSVReader;

public class ExportScraper {
	private static final String TAG = "ExportScraper";
	
	public static List<Game> getGameList(Context ctx, String folderName)
	{
		List<Game> gameList = new ArrayList<Game>();
		SharedPreferences settings = ctx.getSharedPreferences(Constants.PREFS_FILE, 0);
		
		try {
			Connection.Response document = Jsoup.connect(Constants.FUNCTION_CSV + 
					"&" + Constants.PARAM_FOLDER + "=" + URLEncoder.encode(folderName) + 
					"&" + Constants.PARAM_USERNAME + "=" + settings.getString(Constants.PREFS_USERNAME, "") )
				.cookie(Constants.LOGIN_COOKIE, LoginScraper.getCookie(ctx))
				.timeout(Constants.TIMEOUT)
				.execute();
			
			CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(document.bodyAsBytes())));
			
			String[] nextLine = reader.readNext();
			
			// Empty folders return an HTML page instead of a CSV. If we don't get some columns, just get out of here.
			if(nextLine.length <= 1)
				return gameList;
			
			while ((nextLine = reader.readNext()) != null) {
                Game newGame = new Game();
                newGame.setRFGID(nextLine[0]);
                newGame.setConsole(nextLine[1]);
                newGame.setRegion(nextLine[2]);
                newGame.setType(nextLine[3]);
                newGame.setTitle(nextLine[4]);
                newGame.setPublisher(nextLine[5]);
                try { newGame.setYear(Integer.parseInt(nextLine[6])); } catch (Exception e) { }
                newGame.setGenre(nextLine[7]);
                newGame.setGameQuantity(Integer.parseInt(nextLine[8]));
                newGame.setBoxQuantity(Integer.parseInt(nextLine[9]));
                newGame.setManualQuantity(Integer.parseInt(nextLine[10]));
                gameList.add(newGame);
            }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return gameList;
	}
}
