package com.spring.ratting.util;

import java.util.List;

public class FacetFieldDTO {
	private String fieldName;
    private List<FacetValueDTO> values;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public List<FacetValueDTO> getValues() {
		return values;
	}
	public void setValues(List<FacetValueDTO> values) {
		this.values = values;
	}
	public FacetFieldDTO(String fieldName, List<FacetValueDTO> values) {
		super();
		this.fieldName = fieldName;
		this.values = values;
	}
	public FacetFieldDTO() {
		super();
	}
    
    

}
