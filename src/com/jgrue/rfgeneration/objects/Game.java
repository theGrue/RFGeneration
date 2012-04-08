package com.jgrue.rfgeneration.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jgrue.rfgeneration.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Game {
	private final static String TAG = "Game";
	private long id;
	private String rfgid;
	private Console console;
	private String regionId;
	private String region;
	private String type;
	private String title;
	private String publisher;
	private int year;
	private String genre;
	private int qty;
	private int box;
	private int man;
	private List<Collection> collections;
	private static Map<String, Drawable> regionMap;
	
	@Override
	public String toString() {
		return rfgid + ", " + console.getName() + ", " + region + ", " + type + ", " + title + ", " + 
			publisher + ", " + year + ", " + genre + ", " + qty + ", " + box + ", " + man;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setRFGID(String rfgid) {
		this.rfgid = rfgid;
		
		if(this.console == null)
			this.console = new Console();
		
		String[] splitId = rfgid.split("-");
		regionId = splitId[0];
		this.console.setId(splitId[1]);
	}
	public String getRFGID() {
		return rfgid;
	}
	public void setConsole(String console) {
		if(this.console == null)
			this.console = new Console();
		
		this.console.setName(console);
	}
	public String getConsole() {
		if(this.console == null)
			this.console = new Console();
		
		return console.getName();
	}
	public int getConsoleId() {
		if(this.console == null)
			this.console = new Console();
		
		return console.getId();
	}
	public void setConsoleAbbv(String consoleAbbv) {
		if(this.console == null)
			this.console = new Console();
		
		this.console.setAbbreviation(consoleAbbv);
	}
	public String getConsoleAbbv() {
		if(this.console == null)
			this.console = new Console();
		
		return console.getAbbreviation();
	}
	public String getRegionId() {
		return regionId;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getRegion() {
		return region;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getYear() {
		return year;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getGenre() {
		return genre;
	}
	public void setGameQuantity(int qty) {
		this.qty = qty;
	}
	public int getGameQuantity() {
		return qty;
	}
	public void setBoxQuantity(int box) {
		this.box = box;
	}
	public int getBoxQuantity() {
		return box;
	}
	public void setManualQuantity(int man) {
		this.man = man;
	}
	public int getManualQuantity() {
		return man;
	}
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}
	public List<Collection> getCollections() {
		if(this.collections == null)
			collections = new ArrayList<Collection>();
		
		return collections;
	}
	
	public boolean hasGame() {
		return qty > 0;
	}
	public boolean hasBox() {
		return box > 0;
	}
	public boolean hasManual() {
		return man > 0;
	}
	
	public Drawable getRegionDrawable(Context context) {
		return getRegionDrawable(context, region);
	}
	
	private Drawable getRegionDrawable(Context context, String region) {
		if(regionMap == null) {
			Log.v(TAG, "Creating region drawable map.");
			regionMap = new HashMap<String, Drawable>();
			regionMap.put("U", context.getResources().getDrawable(R.drawable.regions_u));
			regionMap.put("US", context.getResources().getDrawable(R.drawable.regions_us));
			regionMap.put("J", context.getResources().getDrawable(R.drawable.regions_j));
			regionMap.put("GB", context.getResources().getDrawable(R.drawable.regions_gb));
			regionMap.put("A", context.getResources().getDrawable(R.drawable.regions_a));
			regionMap.put("AR", context.getResources().getDrawable(R.drawable.regions_ar));
			regionMap.put("AT", context.getResources().getDrawable(R.drawable.regions_at));
			regionMap.put("AU", context.getResources().getDrawable(R.drawable.regions_au));
			regionMap.put("B", context.getResources().getDrawable(R.drawable.regions_b));
			regionMap.put("BE", context.getResources().getDrawable(R.drawable.regions_be));
			regionMap.put("BR", context.getResources().getDrawable(R.drawable.regions_br));
			regionMap.put("C", context.getResources().getDrawable(R.drawable.regions_c));
			regionMap.put("CA", context.getResources().getDrawable(R.drawable.regions_ca));
			regionMap.put("CH", context.getResources().getDrawable(R.drawable.regions_ch));
			regionMap.put("CN", context.getResources().getDrawable(R.drawable.regions_cn));
			regionMap.put("DE", context.getResources().getDrawable(R.drawable.regions_de));
			regionMap.put("DK", context.getResources().getDrawable(R.drawable.regions_dk));
			regionMap.put("E", context.getResources().getDrawable(R.drawable.regions_e));
			regionMap.put("ES", context.getResources().getDrawable(R.drawable.regions_es));
			regionMap.put("FI", context.getResources().getDrawable(R.drawable.regions_fi));
			regionMap.put("FR", context.getResources().getDrawable(R.drawable.regions_fr));
			regionMap.put("H", context.getResources().getDrawable(R.drawable.regions_h));
			regionMap.put("HK", context.getResources().getDrawable(R.drawable.regions_hk));
			regionMap.put("IE", context.getResources().getDrawable(R.drawable.regions_ie));
			regionMap.put("IL", context.getResources().getDrawable(R.drawable.regions_il));
			regionMap.put("IN", context.getResources().getDrawable(R.drawable.regions_in));
			regionMap.put("IT", context.getResources().getDrawable(R.drawable.regions_it));
			regionMap.put("K", context.getResources().getDrawable(R.drawable.regions_k));
			regionMap.put("KR", context.getResources().getDrawable(R.drawable.regions_kr));
			regionMap.put("LU", context.getResources().getDrawable(R.drawable.regions_lu));
			regionMap.put("M", context.getResources().getDrawable(R.drawable.regions_m));
			regionMap.put("NL", context.getResources().getDrawable(R.drawable.regions_nl));
			regionMap.put("NO", context.getResources().getDrawable(R.drawable.regions_no));
			regionMap.put("NZ", context.getResources().getDrawable(R.drawable.regions_nz));
			regionMap.put("PH", context.getResources().getDrawable(R.drawable.regions_ph));
			regionMap.put("PL", context.getResources().getDrawable(R.drawable.regions_pl));
			regionMap.put("PT", context.getResources().getDrawable(R.drawable.regions_pt));
			regionMap.put("SE", context.getResources().getDrawable(R.drawable.regions_se));
			regionMap.put("TW", context.getResources().getDrawable(R.drawable.regions_tw));
			regionMap.put("W", context.getResources().getDrawable(R.drawable.regions_w));
		}
		
		Drawable regionDrawable = regionMap.get(region);
		if(regionDrawable == null)
			regionDrawable = context.getResources().getDrawable(R.drawable.regions_unknown);
	
		return regionDrawable;
	}
	
	public AnimationDrawable getRegionAnimation(Context context) {
		AnimationDrawable animation = new AnimationDrawable();
		String[] regions;
		if(region.contains(","))
			regions = region.split(",");
		else
			regions = region.split("-");
		
		for(int i = 0; i < regions.length; i++) {
			animation.addFrame(getRegionDrawable(context, regions[i].trim()), 1000);
		}
		
		animation.setOneShot(false);
		
		return animation;
	}
}
