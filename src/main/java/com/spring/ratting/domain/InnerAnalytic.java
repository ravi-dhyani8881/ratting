package com.spring.ratting.domain;

import org.apache.solr.common.SolrDocument;


import com.fasterxml.jackson.annotation.JsonView;


public class InnerAnalytic {
	
	public SolrDocument content;
	public SolrDocument reviews;
	@JsonView(InnerAnalytic.class)
	public InnerReviews innerReviews;
	public SolrDocument getContent() {
		return content;
	}
	public void setContent(SolrDocument content) {
		this.content = content;
	}
	public SolrDocument getReviews() {
		return reviews;
	}
	public void setReviews(SolrDocument reviews) {
		this.reviews = reviews;
	}
	public InnerReviews getInnerReviews() {
		return innerReviews;
	}
	public void setInnerReviews(InnerReviews innerReviews) {
		this.innerReviews = innerReviews;
	}
	public InnerAnalytic(SolrDocument content, SolrDocument reviews, InnerReviews innerReviews) {
		super();
		this.content = content;
		this.reviews = reviews;
		this.innerReviews = innerReviews;
	}
}