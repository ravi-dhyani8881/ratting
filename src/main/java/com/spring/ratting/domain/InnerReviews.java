package com.spring.ratting.domain;

import org.apache.solr.common.SolrDocument;



public class InnerReviews {

	public SolrDocument reviews;

	public SolrDocument getReviews() {
		return reviews;
	}

	public void setReviews(SolrDocument reviews) {
		this.reviews = reviews;
	}
	public InnerReviews(SolrDocument reviews) {
		super();
		this.reviews = reviews;
	}
}