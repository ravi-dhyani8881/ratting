package com.spring.ratting.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseData {
	 public ArrayList<DataItem> data;
}

@Data
class DataItem {
    
    
    public String updatedAt;
    public String createdAt;
    public String id;
    public String name;
    public String box;
    @JsonProperty("linkDict")
    public String linkDict;
    @JsonProperty("tableDict")
    public String tableDict;
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
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
	public String getBox() {
		return box;
	}
	public void setBox(String box) {
		this.box = box;
	}
	public String getLinkDict() {
		return linkDict;
	}
	public void setLinkDict(String linkDict) {
		this.linkDict = linkDict;
	}
	public String getTableDict() {
		return tableDict;
	}
	public void setTableDict(String tableDict) {
		this.tableDict = tableDict;
	}
    
    
    
}
