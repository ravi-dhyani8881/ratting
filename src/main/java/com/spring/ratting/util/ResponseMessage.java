package com.spring.ratting.util;

import org.apache.solr.common.SolrDocumentList;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessage {

	private String responseMessage;
	
	private String responseCode;
	
	private Long numFound;
	
	private SolrDocumentList document;
	
	private String ID;
	
	private String query;
	
	
	
	public ResponseMessage(String responseMessage, String responseCode, Long numFound, String query) {
		super();
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
		this.numFound = numFound;
		this.query = query;
	}
	public ResponseMessage(String responseMessage, String responseCode, String iD) {
		super();
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
		this.ID = iD;
	}
	public ResponseMessage(String responseMessage, String responseCode, Long numFound, SolrDocumentList document) {
		super();
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
		this.numFound = numFound;
		this.document = document;
	}
	public ResponseMessage(String responseMessage, String responseCode) {
		super();
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
	}
	public ResponseMessage(Long recordFound, SolrDocumentList document) {
		super();
		this.numFound = recordFound;
		this.document = document;
	}
	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public Long getRecordFound() {
		return numFound;
	}

	public void setRecordFound(Long recordFound) {
		this.numFound = recordFound;
	}

	public SolrDocumentList getDocument() {
		return document;
	}

	public void setDocument(SolrDocumentList document) {
		this.document = document;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public Long getNumFound() {
		return numFound;
	}
	public void setNumFound(Long numFound) {
		this.numFound = numFound;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}	
}