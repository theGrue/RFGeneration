package com.jgrue.rfgeneration.objects;

import java.util.ArrayList;


public class CollectionPage {
	private ArrayList<Game> gameList;
	private String userName;
	private String folder;
	private String console;
	private String type;
	private int page;
	private int totalPages;
	private ArrayList<String> folderList;
	private ArrayList<Console> consoleList;
	private ArrayList<String> typeList;
	
	public void setList(ArrayList<Game> gameList) {
		this.gameList = gameList;
	}
	public ArrayList<Game> getList() {
		return gameList;
	}
	public void setUsername(String userName) {
		this.userName = userName;
	}
	public String getUsername() {
		return userName;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public String getFolder() {
		return folder;
	}
	public void setConsole(String console) {
		this.console = console;
	}
	public String getConsole() {
		return console;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPage() {
		return page;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setFolderList(ArrayList<String> folderList) {
		this.folderList = folderList;
	}
	public ArrayList<String> getFolderList() {
		return folderList;
	}
	public void setConsoleList(ArrayList<Console> consoleList) {
		this.consoleList = consoleList;
	}
	public ArrayList<Console> getConsoleList() {
		return consoleList;
	}
	public void setTypeList(ArrayList<String> typeList) {
		this.typeList = typeList;
	}
	public ArrayList<String> getTypeList() {
		return typeList;
	}
}
