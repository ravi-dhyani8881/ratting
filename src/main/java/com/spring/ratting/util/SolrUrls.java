package com.spring.ratting.util;

public interface SolrUrls {

	static String baseUrl="http://ravi-solr-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/";
	
	static String solrAnalyticUrl = baseUrl+"analytic";
	
	static String helpFullUrl = baseUrl+"helpFull";
	
	static String likelUrl = baseUrl+"like";
	
	static String solrCommentRattingAnalyticUrl = baseUrl+"commentRattingAnalytic2";

	static String replyUrl = baseUrl+"reply";	
	
	static String userUrl = baseUrl+"users";	
	
	static String verifyUserUrl = "http://localhost:8080/users/verifyUser?userActivationKey=";	
	
	static String contentUrl = baseUrl+"content";
	
	static String reviewUrl = baseUrl+"review";	
	
	static String protocolUrl = baseUrl+"msgProtocol";
	
	static String apiKeyUrl = baseUrl+"apiKey";	
}