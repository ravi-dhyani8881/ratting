package com.spring.ratting.solr;

import org.apache.solr.client.solrj.response.UpdateResponse;

public class SolrResponseParser extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private UpdateResponse updateResponse;

    public SolrResponseParser(String code, String message) {
        super(message);
        this.setCode(code);
    }

    public SolrResponseParser(String code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    public String getCode() {
        return code;
    }

    
    public SolrResponseParser(String code, UpdateResponse updateResponse) {
		super();
		this.code = code;
		this.updateResponse = updateResponse;
	}

	public void setCode(String code) {
        this.code = code;
    }

	public UpdateResponse getUpdateResponse() {
		return updateResponse;
	}

	public void setUpdateResponse(UpdateResponse updateResponse) {
		this.updateResponse = updateResponse;
	}
}