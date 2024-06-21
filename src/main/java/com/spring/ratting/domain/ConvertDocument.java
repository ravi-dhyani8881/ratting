package com.spring.ratting.domain;

public class ConvertDocument {

	String ID;
	
	String payload;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public ConvertDocument(String iD, String payload) {
		super();
		ID = iD;
		this.payload = payload;
	}
	
	
	
}
