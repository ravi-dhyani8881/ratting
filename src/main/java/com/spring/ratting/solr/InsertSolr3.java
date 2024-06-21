package com.spring.ratting.solr;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrException;
import com.spring.ratting.util.Utility;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
public class InsertSolr3 {

    public static void main(String[] args) throws SolrServerException {
        String solrUrl = "http://localhost:8983/solr/reply"; // Replace with your Solr URL
        int iterations = 10000000; // Number of iterations
            SolrInputDocument document = new SolrInputDocument();
            document.addField("ID", UUID.randomUUID().toString()); // Use unique ID for each iteration
          
            
            
            
            document.addField("contentId",  70);

            document.addField("reviewId",  "3883e025-3931-4e72-b2f7-bbd7c9775298");
            document.addField("userId",  "cecea004-1b91-41ae-a21b-176f4254b31e");
            document.addField("replyHeading", "contentid 70");
            document.addField("replyBody", "replybody");
            
            
           
             
            
            try {
            	
            
                UpdateResponse response = addDocumentAndExceptionByTemplate(solrUrl, document);
             //   System.out.println("Document " + i + " added successfully. Response: " + response);
            } catch (SolrProcessingException e) {
                System.err.println("Error adding document  " + e.getMessage());
                e.printStackTrace();
            }
        
    }
    
    
    public static int generateRating() {
        // Create an instance of Random
        Random rand = new Random();

        // Generate a random integer between 1 and 5 (inclusive)
        int rating = rand.nextInt(5) + 1;

        return rating;
    }

    public static <T> T addDocumentAndExceptionByTemplate(String solrUrl, SolrInputDocument document) throws SolrProcessingException, SolrServerException {
        HttpSolrClient solr = Utility.getSolrClient(solrUrl);
        try {
        	  UpdateResponse response =  solr.add(document);
           solr.commit();
           NamedList<Object> responseValues = response.getResponse();
           if (responseValues != null) {
            String   generatedId = (String) responseValues.get("ID"); // Extract ID from the response
            
            System.out.println("----------->"+generatedId);
           }
           
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
