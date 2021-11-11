package com.spring.ratting.domain;

import java.util.List;

public class Analytic {

	List<InnerAnalytic> data;

	public List<InnerAnalytic> getData() {
		return data;
	}

	public void setData(List<InnerAnalytic> data) {
		this.data = data;
	}

	public Analytic(List<InnerAnalytic> data) {
		super();
		this.data = data;
	}
	
	
	
}