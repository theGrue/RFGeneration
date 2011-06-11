package com.rfgeneration.objects;

import java.util.List;


public class GameInfo extends Game {
	private String alternateTitle;
	private String partNumber;
	private String upc;
	private String developer;
	private String rating;
	private String subGenre;
	private String players;
	private String controlScheme;
	private String mediaFormat;
	private List<String> nameList;
	private List<String> creditList;
	private List<String> imageTypes;
	
	public void setAlternateTitle(String alternateTitle) {
		this.alternateTitle = alternateTitle;
	}
	public String getAlternateTitle() {
		return alternateTitle;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setUPC(String upc) {
		this.upc = upc;
	}
	public String getUPC() {
		return upc;
	}
	public void setDeveloper(String developer) {
		this.developer = developer;
	}
	public String getDeveloper() {
		return developer;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getRating() {
		return rating;
	}
	public void setSubGenre(String subGenre) {
		this.subGenre = subGenre;
	}
	public String getSubGenre() {
		return subGenre;
	}
	public void setPlayers(String players) {
		this.players = players;
	}
	public String getPlayers() {
		return players;
	}
	public void setControlScheme(String controlScheme) {
		this.controlScheme = controlScheme;
	}
	public String getControlScheme() {
		return controlScheme;
	}
	public void setMediaFormat(String mediaFormat) {
		this.mediaFormat = mediaFormat;
	}
	public String getMediaFormat() {
		return mediaFormat;
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
	
}
