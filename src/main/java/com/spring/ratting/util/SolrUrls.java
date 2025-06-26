package com.spring.ratting.util;

public interface SolrUrls {

	static String solrAnalyticUrl = "http://solr-service:80/solr/analytic";
	
	static String helpFullUrl = "http://solr-service:8983/solr/helpFull";
	
	static String likelUrl = "http://solr-service:8983/solr/like";
	
	static String solrCommentRattingAnalyticUrl = "http://solr-service:8983/solr/commentRattingAnalytic2";

	static String replyUrl = "http://solr-service:8983/solr/reply";	
	
	static String userUrl = "http://solr-service:8983/solr/users";	
	
	static String verifyUserUrl = "http://localhost:8080/users/verifyUser?userActivationKey=";	
	
	static String contentUrl = "http://solr-service:8983/solr/content";
	
	static String reviewUrl = "http://solr-service:8983/solr/review";	
	
	static String protocolUrl = "http://solr-service:8983/solr/msgProtocol";
	
	static String apiKeyUrl = "http://solr-service:8983/solr/apiKey";	
}
