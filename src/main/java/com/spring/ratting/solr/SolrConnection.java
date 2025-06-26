package com.spring.ratting.solr;

import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public interface SolrConnection {
	
//	public String addSolrRattingDoc(ReviewRatting reviewRatting, String solrUrl);
	
//	public String addhelpFullReview(ReviewRatting reviewRatting, String solrUrl);
	
//	public String addLikeOnReview(ReviewRatting reviewRatting, String solrUrl);
	@Deprecated
	public QueryResponse serachDocument(String solrUrl, String query);
	
	@Deprecated
	public UpdateResponse deleteByQuery(String solrUrl, String query);
	
	@Deprecated
	public UpdateResponse addDocument(String solrUrl,SolrInputDocument document)  ;
	
	@Deprecated
	public UpdateResponse updateDocument(String solrUrl,SolrInputDocument document);
	
	@Deprecated
	public QueryResponse advanceSerach(String solrUrl, Map<String, String> searchCriteria);
	

	
	// Advance search by template 
	public <T> T advanceSerachByTemplate(String solrUrl, Map<String, String> searchCriteria);
	
	public <T> T addDocumentByTemplate(String solrUrl, SolrInputDocument document);
	
	public <T> T updateDocumentByTemplate(String solrUrl, SolrInputDocument document);
	
	public <T> T deleteByTemplate(String solrUrl, String query);

	public <T> T  serachQueryByTemplate(String solrUrl, String query);
	
	
	
	
	// Advance search by template with exception
		public <T> T advanceSerachAndExceptionByTemplate(String solrUrl, Map<String, String> searchCriteria, SolrQuery solrQuery);
		
		public <T> T addDocumentAndExceptionByTemplate(String solrUrl, SolrInputDocument document);
		
		public <T> T updateDocumentAndExceptionByTemplate(String solrUrl, SolrInputDocument document);
		
		public <T> T deleteByAndExceptionTemplate(String solrUrl, String query);

		public <T> T  serachQueryAndExceptionByTemplate(String solrUrl, String query);
	
}
