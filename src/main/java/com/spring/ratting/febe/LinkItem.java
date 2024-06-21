package com.spring.ratting.febe;

import java.util.List;

public class LinkItem {

	private String id;
    private String name;
    private List<Endpoint> endpoints;
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
	public List<Endpoint> getEndpoints() {
		return endpoints;
	}
	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}
    
    
    
}
