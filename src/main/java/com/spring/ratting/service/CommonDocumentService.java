package com.spring.ratting.service;

import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.spring.ratting.util.ResponseMessage;

public interface CommonDocumentService {
	@Deprecated
	public String addDocument(Map<String, Object> payload , String url);
	@Deprecated
	public String updateDocument(Map<String, Object> payload , String url);

	@Deprecated
	public QueryResponse advanceSearchDocument(Map<String, String> searchCriteria , String url);
	
	@Deprecated
	public QueryResponse SearchByQuery(String query , String url);
	
	@Deprecated
	public ResponseMessage addDocumentWithError(Map<String, Object> payload , String url);
	
	
	//same method By Template
	
	public <T> T advanceSearchDocumentByTemplate(Map<String, String> searchCriteria , String url);
	
	public <T> T addDocumentByTemplate(Map<String, Object> payload , String url);
	
	public <T> T updateDocumentByTemplate(Map<String, Object> payload , String url);
	
	public <T> T deleteDocumentByTemplate(String query, String url);
	
	public <T> T advanceQueryByTemplate(String query , String url);
	
}