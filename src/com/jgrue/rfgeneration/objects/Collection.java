package com.jgrue.rfgeneration.objects;

public class Collection {
	private Folder folder;
	private int qty;
	private int box;
	private int man;
	
	public void setFolder(Folder folder) {
		this.folder = folder;
	}
	public Folder getFolder() {
		return folder;
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
}
