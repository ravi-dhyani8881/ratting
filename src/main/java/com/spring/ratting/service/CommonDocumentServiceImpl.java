package com.spring.ratting.service;

import java.util.Map;


import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.ratting.solr.SolrConnection;
import com.spring.ratting.util.ResponseMessage;
import com.spring.ratting.util.Utility;

@Component
public class CommonDocumentServiceImpl implements CommonDocumentService{
	
	@Autowired
	SolrConnection solrConnection;

	public String addDocument(Map<String, Object> payload,String url) {
		String ID=Utility.getUniqueId();
			SolrInputDocument document = new SolrInputDocument();
			document.addField("ID",ID);
			try {
		 	solrConnection.addDocument(url, this.createDoc2(payload, document));	
		}catch (Exception  e ) {
			e.printStackTrace();
		}
		
		return ID;
	}
	
	@Override
	public String updateDocument(Map<String, Object> payload, String url) {
		SolrInputDocument document = new SolrInputDocument();
		 solrConnection.updateDocument(url, this.createDoc2(payload, document));
		return "sucess";
	}
	
	public QueryResponse advanceSearchDocument(Map<String, String> searchCriteria,String url) {
		
		return solrConnection.advanceSerach(url, searchCriteria);
	}

	@Override
	public QueryResponse SearchByQuery(String query, String url) {
		return solrConnection.serachDocument(url, query);
	}

	@Override
	public ResponseMessage addDocumentWithError(Map<String, Object> payload, String url) {
		String ID=Utility.getUniqueId();
		SolrInputDocument document = new SolrInputDocument();
		document.addField("ID",ID);
	 	solrConnection.addDocument(url, this.createDoc2(payload, document));
		return null;
	}

	@Override
	public <T> T advanceSearchDocumentByTemplate(Map<String, String> searchCriteria, String url) {
		return solrConnection.advanceSerachByTemplate(url, searchCriteria);
	}
	@Override
	public <T> T addDocumentByTemplate(Map<String, Object> payload,String url) {	 	
		return (T) solrConnection.addDocumentByTemplate(url, this.createDoc(payload));
	}
	
	
	@Override
	public <T> T updateDocumentByTemplate(Map<String, Object> payload, String url) {
		return (T)  solrConnection.updateDocumentByTemplate(url, this.createDoc(payload));
	}
	
	
	@Override
	public <T> T deleteDocumentByTemplate(String query, String url) {
		return (T)  solrConnection.deleteByTemplate(url, query);
	}
	
	
	@Override
	public <T> T advanceQueryByTemplate(String query, String url) {
		return (T) solrConnection.serachQueryByTemplate(url, query);
	}

	
	public SolrInputDocument createDoc(Map<String, Object> payload) {
		SolrInputDocument document = new SolrInputDocument();
		payload.forEach((k,v) ->{
	 		document.addField(k, v);
	 	});
		return document;
	}
	
	public SolrInputDocument createDoc2(Map<String, Object> payload, SolrInputDocument document) {
		payload.forEach((k,v) ->{
	 		document.addField(k, v);
	 	});
		return document;
	}

	
}