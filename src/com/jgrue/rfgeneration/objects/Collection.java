package com.jgrue.rfgeneration.objects;

import java.text.DecimalFormat;

public class Collection {
	private Folder folder;
	private float qty;
	private float box;
	private float man;
	private static DecimalFormat format = new DecimalFormat("##0.##");
	
	public void setFolder(Folder folder) {
		this.folder = folder;
	}
	public Folder getFolder() {
		return folder;
	}
	public void setGameQuantity(float qty) {
		this.qty = qty;
	}
	public float getGameQuantity() {
		return qty;
	}
	public void setBoxQuantity(float box) {
		this.box = box;
	}
	public float getBoxQuantity() {
		return box;
	}
	public void setManualQuantity(float man) {
		this.man = man;
	}
	public float getManualQuantity() {
		return man;
	}
	
	public static String getQuantityString(float qty) {
		return format.format(qty);
	}
}
