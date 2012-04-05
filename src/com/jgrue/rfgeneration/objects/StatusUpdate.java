package com.jgrue.rfgeneration.objects;

public class StatusUpdate {
	private int progress;
	private String update;

	public StatusUpdate(int progress, String update) {
		this.progress = progress;
		this.update = update;
	}
	
	public int getProgress() {
		return progress;
	}
	public String getUpdate() {
		return update;
	}
}
