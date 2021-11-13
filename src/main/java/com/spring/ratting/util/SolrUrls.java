package com.spring.ratting.util;

public interface SolrUrls {

	static String solrAnalyticUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/analytic";
	
	static String helpFullUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/helpFull";
	
	static String likelUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/like";
	
	static String solrCommentRattingAnalyticUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/commentRattingAnalytic2";

	static String replyUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/reply";	
	
	static String userUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/users";	
	
	static String verifyUserUrl = "http://localhost:8080/users/verifyUser?userActivationKey=";	
	
	static String contentUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/content";
	
	static String reviewUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/review";	
	
	static String protocolUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/msgProtocol";
	
	static String apiKeyUrl = "http://solr-docker-help-ravi-happy28-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/solr/apiKey";	
}