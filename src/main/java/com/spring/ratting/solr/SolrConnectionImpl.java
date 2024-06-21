package com.spring.ratting.solr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.impl.SolrHttpRequestRetryHandler;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.main.external.exception.user.CustomException5xx;
import com.spring.ratting.external.SolrProcessingException;
import com.spring.ratting.util.Utility;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;

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
		HttpSolrClient solr = new HttpSolrClient(solrUrl);
		
		solr.setParser(new XMLResponseParser());
	
		try {
			solr.add(document);
			updateResponse=solr.commit();
			System.out.println("Inside SolrRattingImpl end");
	//	SolrResponseParser ff = new SolrResponseParser("Sucess", "Added Sucesfully");
		
	//	System.out.println("Inside SolrRattingImpl end"+ff.getMessage());
		}catch (SolrServerException e) {
			
			e.printStackTrace();
	//		SolrResponseParser ff = 	new SolrResponseParser("Failure",e.getMessage(),e);
	//		System.out.println("Inside SolrRattingImpl end"+ff.getMessage());
		} 
		catch (Exception e) {
			e.printStackTrace();
	//		SolrResponseParser ff = 	new SolrResponseParser("Failure",e.getMessage(),e);
	//		System.out.println("Inside SolrRattingImpl end"+ff.getMessage());
		}
		return updateResponse;
	}
	
	@Override
	public UpdateResponse updateDocument(String solrUrl,SolrInputDocument document) {
	      SolrClient Solr = new HttpSolrClient(solrUrl);   
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
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			
			 
			throw new CustomException5xx("Internal server Issue" , e.getCause()); 
			
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
		}catch (SolrServerException | IOException e ) {
			e.printStackTrace();
			return (T) e;
		} 		
		return (T) response;
	}
	
	@Override
	public <T> T updateDocumentByTemplate(String solrUrl,SolrInputDocument document) {
	      SolrClient Solr = new HttpSolrClient(solrUrl);   
	      UpdateRequest updateRequest = new UpdateRequest();  
	      updateRequest.setAction( UpdateRequest.ACTION.COMMIT, false, false);    	      
	      UpdateResponse response = null;
		try {
				updateRequest.add( document);  
				response = updateRequest.process(Solr); 
		} catch (SolrServerException | IOException  e) {
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
		}catch (SolrServerException | IOException e) {
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

	@Override
	public <T> T advanceSerachAndExceptionByTemplate(String solrUrl, Map<String, String> searchCriteria, SolrQuery solrQuery) {
		QueryResponse response=null;
		HttpSolrClient solr=Utility.getSolrClient(solrUrl);
		solr.setParser(new XMLResponseParser());
		
		ModifiableSolrParams searchParams = new ModifiableSolrParams();

        // Add parameters to the ModifiableSolrParams object
        searchParams.add("q", "*:*");  // Your query
        searchParams.add("rows", "10"); // Number of rows
        searchParams.add("start", "0"); // Start index for pagination
       // searchParams.add("fl", "field1,field2"); // List of fields to retrieve
        searchParams.add("facet.query", "contentType:TV"); // Filter query
        searchParams.add("facet", "true"); // Filter query
        
		
		
		SolrQuery solrQuery2 = new SolrQuery();
		solrQuery2.addFacetField("try");
		solrQuery2.addFacetField("contentType");
		solrQuery2.addFacetField("ID");
		
		solrQuery2.add(searchParams);
	//	final Map<String, String> queryParamMap = new HashMap<String, String>();
		// queryParamMap.put("q", query);
		// queryParamMap.put("fl", "id, name");
//		ModelMap  f=new ModelMap();
//		
//		f.put("q", "*:*");
//		
		MapSolrParams queryParams = new MapSolrParams(searchCriteria);
		
		try {
			
		//	response=solr.query(solrQuery2);
			response=solr.query(solrQuery);
			
			
			
		} catch (SolrServerException | IOException | RemoteSolrException e) {
			
			if(((RemoteSolrException) e).code()==404)
        	{
        		throw new SolrProcessingException("Invalid URL", e);
        	}
			  int colonIndex = Utility.findNthIndexOf(e.getMessage(), ':', 3);
	            
	            if (colonIndex != -1) {
	            	
	            	throw new SolrProcessingException(e.getMessage().substring(colonIndex+1, e.getMessage().length()), e);
	            } else {
	            	throw new SolrProcessingException( e.getMessage(), e);
	            }
		}
		return (T) response;
	}

	@Override
	public <T> T addDocumentAndExceptionByTemplate(String solrUrl, SolrInputDocument document) {
		HttpSolrClient solr = Utility.getSolrClient(solrUrl);
        try {
            solr.add(document);
            UpdateResponse response = solr.commit();
            return (T) response;
        } catch (SolrServerException | IOException | RemoteSolrException e) {
            e.printStackTrace();
            
            int colonIndex = Utility.findNthIndexOf(e.getMessage(), ':', 3);
            
            if (colonIndex != -1) {
            	throw new SolrProcessingException(e.getMessage().substring(colonIndex+1, e.getMessage().length()), e);
            } else {
            	throw new SolrProcessingException( e.getMessage(), e);
            }
        } finally {
            closeSolrClient(solr);
        }
	}

	@Override
	public <T> T updateDocumentAndExceptionByTemplate(String solrUrl, SolrInputDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T deleteByAndExceptionTemplate(String solrUrl, String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T serachQueryAndExceptionByTemplate(String solrUrl, String query) {
		// TODO Auto-generated method stub
		return null;
	}

	private void closeSolrClient(HttpSolrClient solr) {
        if (solr != null) {
            try {
                solr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	
	 private static int findNthIndexOf(String str, char ch, int n) {
	        int index = -1;
	        for (int i = 0; i < n; i++) {
	            index = str.indexOf(ch, index + 1);
	            if (index == -1) {
	                break;
	            }
	        }
	        return index;
	    }
	
}