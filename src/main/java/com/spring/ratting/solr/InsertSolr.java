package com.spring.ratting.solr;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrException;
import com.spring.ratting.util.Utility;

import java.io.IOException;

public class InsertSolr {

    public static void main(String[] args) throws SolrServerException {
        String solrUrl = "http://localhost:8983/solr/content"; // Replace with your Solr URL
        int iterations = 10000000; // Number of iterations
        for (int i = 1; i <= iterations; i++) {
            SolrInputDocument document = new SolrInputDocument();
          //  document.addField("ID", Integer.toString(i)); // Use unique ID for each iteration
            document.addField("contentName", "contentName ");

            document.addField("contentDesc", "contentDesc");
            document.addField("contentType", "contentType ");
            document.addField("contentSubType", "contentSubType ");
            document.addField("myOwnField_s", "myOwnField_s ");
            
            document.addField("try", "try ");
            document.addField("_root_", "_root_ ");
            document.addField("logic", "logic ");
            
            
            
//            document.addField("reviewComments", "reviewComments " + i);
//
//            document.addField("contentId", "contentId" + i);
//            document.addField("reviewTittle", "reviewTittle " + i);
//            document.addField("reviewUserId", "reviewUserId " + i);
//            document.addField("reviewContentId", "reviewContentId " + i);
//            
           
             
            
            try {
                UpdateResponse response = addDocumentAndExceptionByTemplate(solrUrl, document);
             //   System.out.println("Document " + i + " added successfully. Response: " + response);
            } catch (SolrProcessingException e) {
                System.err.println("Error adding document " + i + " to Solr: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static <T> T addDocumentAndExceptionByTemplate(String solrUrl, SolrInputDocument document) throws SolrProcessingException, SolrServerException {
        HttpSolrClient solr = Utility.getSolrClient(solrUrl);
        try {
            solr.add(document);
            UpdateResponse response = solr.commit();
            return (T) response;
        } catch (SolrException | IOException e) {
            e.printStackTrace();

            int colonIndex = Utility.findNthIndexOf(e.getMessage(), ':', 3);

            if (colonIndex != -1) {
                throw new SolrProcessingException(e.getMessage().substring(colonIndex + 1), e);
            } else {
                throw new SolrProcessingException(e.getMessage(), e);
            }
        } finally {
            closeSolrClient(solr);
        }
    }

    private static void closeSolrClient(HttpSolrClient solr) {
        if (solr != null) {
            try {
                solr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class SolrProcessingException extends Exception {
    public SolrProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
