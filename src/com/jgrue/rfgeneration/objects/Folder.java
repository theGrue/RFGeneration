package com.jgrue.rfgeneration.objects;

public class Folder {
	private long id;
	private String name;
	private int quantity;
	private boolean isOwned;
	private boolean isForSale;
	private boolean isPrivate;
	private long timestamp;
	
	public Folder() { }
	
	public Folder(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return id + ": " + name + ", " + isOwned + ", " + isForSale + ", " + isPrivate;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setOwned(boolean isOwned) {
		this.isOwned = isOwned;
	}
	public boolean isOwned() {
		return isOwned;
	}
	public void setForSale(boolean isForSale) {
		this.isForSale = isForSale;
	}
	public boolean isForSale() {
		return isForSale;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public long getTimestamp() {
		return timestamp;
	}
}
