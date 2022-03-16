package com.spring.ratting.solr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.stereotype.Component;

import com.spring.ratting.util.Utility;

@Component
public class SolrConnectionImpl implements SolrConnection {

	/**
	
	@Override
	public String addSolrRattingDoc(ReviewRatting reviewRatting, String solrUrl) {
	
		HttpSolrClient solr=Utility.getSolrClient(solrUrl);
		SolrInputDocument document = new SolrInputDocument();
		document.addField("reviewId", reviewRatting.getId());
		document.addField("contentId", reviewRatting.getReviewContentId());
		document.addField("userId", reviewRatting.getReviewUserId());
		document.addField("ratting", reviewRatting.getReviewRatting());
		try {
			solr.add(document);
			solr.commit();
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			return "failed";
		}		
		return "sucess";
	}
	
	**/
	
	@Override
	public QueryResponse serachDocument(String solrUrl, String query) {
		
		QueryResponse response=null;
		HttpSolrClient solr=Utility.getSolrClient(solrUrl);
		solr.setParser(new XMLResponseParser());
		
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", query);
		// queryParamMap.put("fl", "id, name");
		MapSolrParams queryParams = new MapSolrParams(queryParamMap);
		
		try {
			response=solr.query(queryParams);
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			
		}
		return response;
	}

	@Override
	public UpdateResponse deleteByQuery(String solrUrl, String query) {
		HttpSolrClient solr=Utility.getSolrClient(solrUrl);
		
		UpdateResponse response = null;
		try {
			response = solr.deleteByQuery(query);
			solr.commit();
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return response;
	}

	@Override
	public UpdateResponse addDocument(String solrUrl, SolrInputDocument document) {
		UpdateResponse updateResponse=null;
		HttpSolrClient solr = new HttpSolrClient.Builder(solrUrl).build();
		
		solr.setParser(new XMLResponseParser());
	
		try {
			solr.add(document);
			updateResponse=solr.commit();
			System.out.println("Inside SolrRattingImpl end");
		SolrResponseParser ff = new SolrResponseParser("Sucess", "Added Sucesfully");
		
		System.out.println("Inside SolrRattingImpl end"+ff.getMessage());
		}catch (SolrServerException e) {
			
			e.printStackTrace();
			SolrResponseParser ff = 	new SolrResponseParser("Failure",e.getMessage(),e);
			System.out.println("Inside SolrRattingImpl end"+ff.getMessage());
		} 
		catch (Exception e) {
			e.printStackTrace();
			SolrResponseParser ff = 	new SolrResponseParser("Failure",e.getMessage(),e);
			System.out.println("Inside SolrRattingImpl end"+ff.getMessage());
		}
		return updateResponse;
	}
	
	@Override
	public UpdateResponse updateDocument(String solrUrl,SolrInputDocument document) {
	      SolrClient Solr = new HttpSolrClient.Builder(solrUrl).build();   
	      UpdateRequest updateRequest = new UpdateRequest();  
	      updateRequest.setAction( UpdateRequest.ACTION.COMMIT, false, false);    	      
	      UpdateResponse responce = null;
		try {
				updateRequest.add( document);  
				responce = updateRequest.process(Solr); 
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	      return responce;
	}

	@Override
	public QueryResponse advanceSerach(String solrUrl,Map<String, String> searchCriteria) {
		
		QueryResponse response=null;
		HttpSolrClient solr=Utility.getSolrClient(solrUrl);
		solr.setParser(new XMLResponseParser());
		
		
	//	final Map<String, String> queryParamMap = new HashMap<String, String>();
		// queryParamMap.put("q", query);
		// queryParamMap.put("fl", "id, name");
		MapSolrParams queryParams = new MapSolrParams(searchCriteria);
		
		try {
			response=solr.query(queryParams);
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public <T> T advanceSerachByTemplate(String solrUrl, Map<String, String> searchCriteria) {
		QueryResponse response=null;
		HttpSolrClient solr=Utility.getSolrClient(solrUrl);
		
	//	final Map<String, String> queryParamMap = new HashMap<String, String>();
		// queryParamMap.put("q", query);
		// queryParamMap.put("fl", "id, name");
		MapSolrParams queryParams = new MapSolrParams(searchCriteria);
		
		try {
			response=solr.query(queryParams);
		} catch (SolrServerException | IOException | RemoteSolrException e) {
			e.printStackTrace();
			return (T) e;
		}
		return (T) response;
	}
	
	@Override
	public <T> T addDocumentByTemplate(String solrUrl, SolrInputDocument document) {
		UpdateResponse response=null;
		HttpSolrClient solr = Utility.getSolrClient(solrUrl);
		try {
			solr.add(document);
			response=solr.commit();		
		}catch (SolrServerException | IOException | RemoteSolrException e) {
			e.printStackTrace();
			return (T) e;
		} 		
		return (T) response;
	}
	
	@Override
	public <T> T updateDocumentByTemplate(String solrUrl,SolrInputDocument document) {
	      SolrClient Solr = new HttpSolrClient.Builder(solrUrl).build();   
	      UpdateRequest updateRequest = new UpdateRequest();  
	      updateRequest.setAction( UpdateRequest.ACTION.COMMIT, false, false);    	      
	      UpdateResponse response = null;
		try {
				updateRequest.add( document);  
				response = updateRequest.process(Solr); 
		} catch (SolrServerException | IOException  | RemoteSolrException e) {
			e.printStackTrace();
			return (T) e;
		} 
	      return (T) response;
	}
	
	
	@Override
	public <T> T deleteByTemplate(String solrUrl, String query) {
		UpdateResponse response = null;
		HttpSolrClient solr=Utility.getSolrClient(solrUrl);
		
		try {
			response = solr.deleteByQuery(query);
			solr.commit();
		}catch (SolrServerException | IOException | RemoteSolrException e) {
			e.printStackTrace();
			return (T) e;
		} 		
		return (T) response;
	}

	@Override
	public <T> T serachQueryByTemplate(String solrUrl, String query) {
		QueryResponse response=null;
		HttpSolrClient solr=Utility.getSolrClient(solrUrl);
		
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", query);
		// queryParamMap.put("fl", "id, name");
		MapSolrParams queryParams = new MapSolrParams(queryParamMap);
		
		try {
			response=solr.query(queryParams);
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			return (T) e;
		}
		return (T) response;
	}

	
	
}