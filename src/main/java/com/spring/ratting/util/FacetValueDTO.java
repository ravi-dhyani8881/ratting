package com.spring.ratting.util;

public class FacetValueDTO {
	private String name;
    private long count;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public FacetValueDTO(String name, long count) {
		super();
		this.name = name;
		this.count = count;
	}
    
    

}
