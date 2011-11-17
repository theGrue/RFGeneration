package com.jgrue.rfgeneration.scrapers;

import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jgrue.rfgeneration.objects.GameInfo;

public class HardwareInfoScraper {
	public static GameInfo scrapeHardwareInfo(String rfgid) throws Exception {
		GameInfo gameInfo = new GameInfo();
		gameInfo.setRFGID(rfgid);
		
		URL url = new URL("http://www.rfgeneration.com/PHP/gethwinfo.php?ID=" + rfgid);
		Document document = Jsoup.parse(url, 30000);
		
		///html/body/table/tbody/tr[4]/td/table/tbody/tr/td/table/tbody/tr[4]/td/table
		//html body.bodybg table tbody tr td table.bordercolor tbody tr td table.windowbg2 tbody tr td table
		Elements tables = document.select("table tr:eq(5) td:eq(1) table.bordercolor tr td table.windowbg2 tr:eq(5) td table");
		
		return gameInfo;
	}
}
