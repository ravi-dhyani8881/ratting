package com.spring.ratting.util;

import java.util.UUID;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.springframework.ui.ModelMap;
//import com.main.common.response.ResponseMessage;

public class Utility {
	
	public static String getUniqueId() {
		return UUID.randomUUID().toString();
	}
	
	public static String getCurrentDateAndTime() {
		return new org.joda.time.DateTime().toString();
	}
		
	public static HttpSolrClient getSolrClient(String solrUrl) {
		// HttpSolrClient solr = new HttpSolrClient.Builder(solrUrl).build();
		
	//	HttpSolrClient solr=new HttpSolrClient(solrUrl);
	
		HttpSolrClient solr = new HttpSolrClient.Builder(solrUrl).build();
		
		
	//	SolrHttpRequestRetryHandler kk=	new SolrHttpRequestRetryHandler(2);
		
		solr.setConnectionTimeout(1000);
		
		
		
	//	SolrHttpRequestRetryHandler dd= new SolrHttpRequestRetryHandler(2);
	
		solr.setParser(new XMLResponseParser());
		return solr;
	}
	
	//not used 
	public static <T> T validateConnection(ModelMap model, Object template) {
	
		if(template instanceof Exception )
		{
			return (T) model.addAttribute("Message", new ResponseMessage("Server down, Error", 500));
		}
		return (T) model;
	}
	
	public static String getQuery(String query, String userId) {
		
		String concat="("+query+")" +" && " +"custId:"+userId;
		return concat ;
	}
	
	
public static void sendEmail(String userActivationKey, String userId) {
		
	
	}
	

public static int findNthIndexOf(String str, char ch, int n) {
    int index = -1;
    for (int i = 0; i < n; i++) {
        index = str.indexOf(ch, index + 1);
        if (index == -1) {
            break;
        }
    }
    return index;
}
	
	
//	public static void main(String hh[]) {
//		
//		Utility.getCurrentDateAndTime();
//		
//		System.out.println("------------>"+Utility.getUniqueId());
//		
//	}

}