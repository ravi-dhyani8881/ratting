package com.spring.ratting.external;

import org.apache.http.client.HttpResponseException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;

public class SolrProcessingException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int statusCode;

    public SolrProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = extractStatusCode(cause);
    }

    public int getStatusCode() {
        return statusCode;
    }

    private int extractStatusCode(Throwable cause) {
        if (cause instanceof SolrServerException) {
            Throwable rootCause = ((SolrServerException) cause).getRootCause();
            if (rootCause instanceof HttpResponseException) {
                return ((HttpResponseException) rootCause).getStatusCode();
            }
        }
        return 500; // Default status code for internal server error
    }

}
