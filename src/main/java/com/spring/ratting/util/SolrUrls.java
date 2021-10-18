package com.spring.ratting.util;

public interface SolrUrls {

	static String solrAnalyticUrl = "http://localhost:8983/solr/analytic";
	
	static String helpFullUrl = "http://localhost:8983/solr/helpFull";
	
	static String likelUrl = "http://localhost:8983/solr/like";
	
	static String solrCommentRattingAnalyticUrl = "http://localhost:8983/solr/commentRattingAnalytic2";

	static String replyUrl = "http://localhost:8983/solr/reply";	
	
	static String userUrl = "http://localhost:8983/solr/users";	
	
	static String verifyUserUrl = "http://localhost:8080/users/verifyUser?userActivationKey=";	
	
	static String contentUrl = "http://localhost:8983/solr/content";
	
	static String reviewUrl = "http://localhost:8983/solr/review";	
	
	static String protocolUrl = "http://localhost:8983/solr/msgProtocol";
	
	static String apiKeyUrl = "http://localhost:8983/solr/apiKey";	
}