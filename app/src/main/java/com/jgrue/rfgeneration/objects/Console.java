package com.jgrue.rfgeneration.objects;

public class Console {
	private int id;
	private String console;
	private String abbv = "";
	
	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		if(id.endsWith("D"))
			this.id = 1000;
		else if(id.endsWith("G"))
			this.id = 1001;
		else if(id.endsWith("H"))
			this.id = 1002;
		else if(id.endsWith("J"))
			this.id = 1003;
		else if(id.endsWith("P"))
			this.id = 1004;
		else if(id.endsWith("T"))
			this.id = 1005;
		else if(id.equals("VHS"))
			this.id = 1006;
		else
			try { this.id = Integer.parseInt(id); } catch (Exception e) { }
	}
	public int getId() {
		return id;
	}
	public void setName(String name) {
		this.console = name;
	}
	public String getName() {
		return console;
	}
	public void setAbbreviation(String abbv) {
		this.abbv = abbv;
	}
	public String getAbbreviation() {
		return abbv;
	}
}
