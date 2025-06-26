package com.spring.ratting.febe;

public class Field {

	private String id;
    private String name;
    private String type;
    private String note;
    private String dbdefault;
    private boolean pk;
    private boolean increment;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getDbdefault() {
		return dbdefault;
	}
	public void setDbdefault(String dbdefault) {
		this.dbdefault = dbdefault;
	}
	public boolean isPk() {
		return pk;
	}
	public void setPk(boolean pk) {
		this.pk = pk;
	}
	public boolean isIncrement() {
		return increment;
	}
	public void setIncrement(boolean increment) {
		this.increment = increment;
	}
    
    
    
}
