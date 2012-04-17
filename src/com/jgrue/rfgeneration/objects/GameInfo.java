package com.jgrue.rfgeneration.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameInfo extends Game {
	private Map<String, String> extendedInfo;
	public static final String RFGID = "RFG ID #";
	public static final String CONSOLE = "Console";
	public static final String REGION = "Region";
	public static final String PUBLISHER = "Publisher";
	public static final String YEAR = "Year";
	public static final String GENRE = "Genre";
	public static final String ALTERNATE_TITLE = "Alternate Title";
	public static final String PART_NUMBER = "Part #";
	public static final String UPC = "UPC";
	public static final String DEVELOPER = "Developer";
	public static final String RATING = "Rating";
	public static final String SUB_GENRE = "Sub-genre";
	public static final String PLAYERS = "Players";
	public static final String CONTROL_SCHEME = "Controller";
	public static final String MEDIA_FORMAT = "Media Format";
	private List<String> nameList;
	private List<String> creditList;
	private List<String> imageTypes;
	
	public GameInfo() {
		this(new HashMap<String, String>());
	}
	
	public GameInfo(Map<String, String> extendedInfo) {
		this.extendedInfo = extendedInfo;
		nameList = new ArrayList<String>();
		creditList = new ArrayList<String>();
		imageTypes = new ArrayList<String>();
	}
	
	public void setAlternateTitle(String alternateTitle) {
		put(ALTERNATE_TITLE, alternateTitle);
	}
	public String getAlternateTitle() {
		return extendedInfo.get(ALTERNATE_TITLE);
	}
	public void setPartNumber(String partNumber) {
		put(PART_NUMBER, partNumber);
	}
	public String getPartNumber() {
		return extendedInfo.get(PART_NUMBER);
	}
	public void setUPC(String upc) {
		put(UPC, upc);
	}
	public String getUPC() {
		return extendedInfo.get(UPC);
	}
	public void setDeveloper(String developer) {
		put(DEVELOPER, developer);
	}
	public String getDeveloper() {
		return extendedInfo.get(DEVELOPER);
	}
	public void setRating(String rating) {
		put(RATING, rating);
	}
	public String getRating() {
		return extendedInfo.get(RATING);
	}
	public void setSubGenre(String subGenre) {
		put(SUB_GENRE, subGenre);
	}
	public String getSubGenre() {
		return extendedInfo.get(SUB_GENRE);
	}
	public void setPlayers(String players) {
		put(PLAYERS, players);
	}
	public String getPlayers() {
		return extendedInfo.get(PLAYERS);
	}
	public void setControlScheme(String controlScheme) {
		put(CONTROL_SCHEME, controlScheme);
	}
	public String getControlScheme() {
		return extendedInfo.get(CONTROL_SCHEME);
	}
	public void setMediaFormat(String mediaFormat) {
		put(MEDIA_FORMAT, mediaFormat);
	}
	public String getMediaFormat() {
		return extendedInfo.get(MEDIA_FORMAT);
	}
	public void setNameList(List<String> nameList) {
		this.nameList = nameList;
	}
	public List<String> getNameList() {
		return nameList;
	}
	public void setCreditList(List<String> creditList) {
		this.creditList = creditList;
	}
	public List<String> getCreditList() {
		return creditList;
	}
	public void setImageTypes(List<String> imageTypes) {
		this.imageTypes = imageTypes;
	}
	public List<String> getImageTypes() {
		return imageTypes;
	}
	
	@Override
	public void setRFGID(String rfgid) {
		super.setRFGID(rfgid);
		put(RFGID, rfgid);
	}
	@Override
	public void setConsole(String console) {
		super.setConsole(console);
		put(CONSOLE, console);
	}
	@Override
	public void setRegion(String region) {
		super.setRegion(region);
		put(REGION, region);
	}
	@Override
	public void setPublisher(String publisher) {
		super.setPublisher(publisher);
		put(PUBLISHER, publisher);
	}
	@Override
	public void setYear(int year) {
		super.setYear(year);
		put(YEAR, year);
	}
	@Override
	public void setGenre(String genre) {
		super.setGenre(genre);
		put(GENRE, genre);
	}


	public Map<String, String> getExtendedInfo() {
		return extendedInfo;
	}
	
	private void put(String key, String value) {
		if(value.trim().length() > 0)
			extendedInfo.put(key, value.trim());
	}
	private void put(String key, int value) {
		if(value > 0)
			extendedInfo.put(key, Integer.toString(value));
	}
}
