package com.spring.ratting.solr;
//
//import org.apache.solr.common.SolrInputDocument;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
//import org.apache.solr.client.solrj.response.UpdateResponse;
//import org.apache.solr.common.SolrException;
//import com.spring.ratting.util.Utility;
//
//import java.io.IOException;
//import java.util.Random;
//import java.util.UUID;
//public class InsertSolr5 {
//
//    public static void main(String[] args) throws SolrServerException {
//        String solrUrl = "http://localhost:8983/solr/helpFull"; // Replace with your Solr URL
//        int iterations = 10000000; // Number of iterations
//        for (int i = 1; i <= iterations; i++) {
//            SolrInputDocument document = new SolrInputDocument();
//            document.addField("ID", UUID.randomUUID().toString()); // Use unique ID for each iteration
//          
//            
//            
//            
//            document.addField("helpfulId",  UUID.randomUUID().toString());
//
//            document.addField("reviewId",  UUID.randomUUID().toString());
//            document.addField("contentId", UUID.randomUUID().toString());
//            document.addField("userId", UUID.randomUUID().toString());
//            document.addField("helpFull", "Yes");
//            
//            
//           
//             
//            
//            try {
//                UpdateResponse response = addDocumentAndExceptionByTemplate(solrUrl, document);
//             //   System.out.println("Document " + i + " added successfully. Response: " + response);
//            } catch (SolrProcessingException e) {
//                System.err.println("Error adding document " + i + " to Solr: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }
//    
//    
//    public static int generateRating() {
//        // Create an instance of Random
//        Random rand = new Random();
//
//        // Generate a random integer between 1 and 5 (inclusive)
//        int rating = rand.nextInt(5) + 1;
//
//        return rating;
//    }
//
//    public static <T> T addDocumentAndExceptionByTemplate(String solrUrl, SolrInputDocument document) throws SolrProcessingException, SolrServerException {
//        HttpSolrClient solr = Utility.getSolrClient(solrUrl);
//        try {
//            solr.add(document);
//            UpdateResponse response = solr.commit();
//            return (T) response;
//        } catch (SolrException | IOException e) {
//            e.printStackTrace();
//
//            int colonIndex = Utility.findNthIndexOf(e.getMessage(), ':', 3);
//
//            if (colonIndex != -1) {
//                throw new SolrProcessingException(e.getMessage().substring(colonIndex + 1), e);
//            } else {
//                throw new SolrProcessingException(e.getMessage(), e);
//            }
//        } finally {
//            closeSolrClient(solr);
//        }
//    }
//
//    private static void closeSolrClient(HttpSolrClient solr) {
//        if (solr != null) {
//            try {
//                solr.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//
//class SolrProcessingException extends Exception {
//    public SolrProcessingException(String message, Throwable cause) {
//        super(message, cause);
//    }
//}
