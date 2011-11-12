package com.jgrue.rfgeneration.objects;

import com.jgrue.rfgeneration.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

public class Game {
	private String rfgid;
	private Console console;
	private String region;
	private String type;
	private String title;
	private String variationTitle;
	private String publisher;
	private int year;
	private String genre;
	private int qty;
	private int box;
	private int man;
	
	public void setRFGID(String rfgid) {
		this.rfgid = rfgid;
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
	public void setVariationTitle(String variationTitle) {
		this.variationTitle = variationTitle;
	}
	public String getVariationTitle() {
		return variationTitle;
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
	
	public boolean hasGame() {
		return qty > 0;
	}
	public boolean hasBox() {
		return box > 0;
	}
	public boolean hasManual() {
		return man > 0;
	}
	public String getConsoleAbbv() {
		if(this.console == null)
			this.console = new Console();
		
		return console.getAbbreviation();
	}
	
	public Drawable getRegionDrawable(Context context) {
		return getRegionDrawable(context, region);
	}
	
	private Drawable getRegionDrawable(Context context, String region) {
		if(region.toUpperCase().equals("U"))
			return context.getResources().getDrawable(R.drawable.regions_u);
		else if(region.toUpperCase().equals("US"))
			return context.getResources().getDrawable(R.drawable.regions_us);
		else if(region.toUpperCase().equals("J"))
			return context.getResources().getDrawable(R.drawable.regions_j);
		else if(region.toUpperCase().equals("GB"))
			return context.getResources().getDrawable(R.drawable.regions_gb);
		else if(region.toUpperCase().equals("A"))
			return context.getResources().getDrawable(R.drawable.regions_a);
		else if(region.toUpperCase().equals("AR"))
			return context.getResources().getDrawable(R.drawable.regions_ar);
		else if(region.toUpperCase().equals("AT"))
			return context.getResources().getDrawable(R.drawable.regions_at);
		else if(region.toUpperCase().equals("AU"))
			return context.getResources().getDrawable(R.drawable.regions_au);
		else if(region.toUpperCase().equals("B"))
			return context.getResources().getDrawable(R.drawable.regions_b);
		else if(region.toUpperCase().equals("BE"))
			return context.getResources().getDrawable(R.drawable.regions_be);
		else if(region.toUpperCase().equals("BR"))
			return context.getResources().getDrawable(R.drawable.regions_br);
		else if(region.toUpperCase().equals("C"))
			return context.getResources().getDrawable(R.drawable.regions_c);
		else if(region.toUpperCase().equals("CA"))
			return context.getResources().getDrawable(R.drawable.regions_ca);
		else if(region.toUpperCase().equals("CH"))
			return context.getResources().getDrawable(R.drawable.regions_ch);
		else if(region.toUpperCase().equals("CN"))
			return context.getResources().getDrawable(R.drawable.regions_cn);
		else if(region.toUpperCase().equals("DE"))
			return context.getResources().getDrawable(R.drawable.regions_de);
		else if(region.toUpperCase().equals("DK"))
			return context.getResources().getDrawable(R.drawable.regions_dk);
		else if(region.toUpperCase().equals("E"))
			return context.getResources().getDrawable(R.drawable.regions_e);
		else if(region.toUpperCase().equals("ES"))
			return context.getResources().getDrawable(R.drawable.regions_es);
		else if(region.toUpperCase().equals("FI"))
			return context.getResources().getDrawable(R.drawable.regions_fi);
		else if(region.toUpperCase().equals("FR"))
			return context.getResources().getDrawable(R.drawable.regions_fr);
		else if(region.toUpperCase().equals("H"))
			return context.getResources().getDrawable(R.drawable.regions_h);
		else if(region.toUpperCase().equals("HK"))
			return context.getResources().getDrawable(R.drawable.regions_hk);
		else if(region.toUpperCase().equals("IE"))
			return context.getResources().getDrawable(R.drawable.regions_ie);
		else if(region.toUpperCase().equals("IL"))
			return context.getResources().getDrawable(R.drawable.regions_il);
		else if(region.toUpperCase().equals("IN"))
			return context.getResources().getDrawable(R.drawable.regions_in);
		else if(region.toUpperCase().equals("IT"))
			return context.getResources().getDrawable(R.drawable.regions_it);
		else if(region.toUpperCase().equals("K"))
			return context.getResources().getDrawable(R.drawable.regions_k);
		else if(region.toUpperCase().equals("KR"))
			return context.getResources().getDrawable(R.drawable.regions_kr);
		else if(region.toUpperCase().equals("LU"))
			return context.getResources().getDrawable(R.drawable.regions_lu);
		else if(region.toUpperCase().equals("M"))
			return context.getResources().getDrawable(R.drawable.regions_m);
		else if(region.toUpperCase().equals("NL"))
			return context.getResources().getDrawable(R.drawable.regions_nl);
		else if(region.toUpperCase().equals("NO"))
			return context.getResources().getDrawable(R.drawable.regions_no);
		else if(region.toUpperCase().equals("NZ"))
			return context.getResources().getDrawable(R.drawable.regions_nz);
		else if(region.toUpperCase().equals("PH"))
			return context.getResources().getDrawable(R.drawable.regions_ph);
		else if(region.toUpperCase().equals("PL"))
			return context.getResources().getDrawable(R.drawable.regions_pl);
		else if(region.toUpperCase().equals("PT"))
			return context.getResources().getDrawable(R.drawable.regions_pt);
		else if(region.toUpperCase().equals("SE"))
			return context.getResources().getDrawable(R.drawable.regions_se);
		else if(region.toUpperCase().equals("TW"))
			return context.getResources().getDrawable(R.drawable.regions_tw);
		else if(region.toUpperCase().equals("W"))
			return context.getResources().getDrawable(R.drawable.regions_w);
	
		return context.getResources().getDrawable(R.drawable.regions_unknown);
	}
	
	public AnimationDrawable getRegionAnimation(Context context) {
		AnimationDrawable animation = new AnimationDrawable();
		String[] regions = region.split(", ");
		
		for(int i = 0; i < regions.length; i++) {
			animation.addFrame(getRegionDrawable(context, regions[i]), 1000);
		}
		
		animation.setOneShot(false);
		
		return animation;
	}
}
