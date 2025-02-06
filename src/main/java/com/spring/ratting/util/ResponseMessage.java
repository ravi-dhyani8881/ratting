package com.spring.ratting.util;

import java.util.Date;
import java.util.List;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.SolrDocumentList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessage {

    private String responseMessage;
    
    @JsonProperty("statusCode")
    private int responseCode;
    
    @JsonProperty("totalRecords")
    private Long numFound;
    
    @JsonProperty("records")
    private SolrDocumentList document;
    @JsonProperty("ID")
    private String id;
    private String query;
    private String responseType = "error";
    private Date createdDate;
    private String start;
    private String activationCode;
    private String row;

    @JsonProperty("advanced")
    private List<FacetFieldDTO> dto;

    // ✅ Private constructor using Builder
    private ResponseMessage(Builder builder) {
        this.responseMessage = builder.responseMessage;
        this.responseCode = builder.responseCode;
        this.numFound = builder.numFound;
        this.document = builder.document;
        this.id = builder.ID;
        this.query = builder.query;
        this.responseType = builder.responseType;
        this.createdDate = builder.createdDate;
        this.start = builder.start;
        this.row = builder.row;
        this.activationCode = builder.activationCode;
        this.dto = builder.dto;
    }

    // ✅ Builder Pattern
    public static class Builder {
        private String responseMessage;
        private int responseCode;
        private Long numFound;
        private SolrDocumentList document;
        private String ID;
        private String query;
        private String responseType = "error";
        private Date createdDate;
        private String start;
        private String activationCode;
        private String row;
        private List<FacetFieldDTO> dto;

        public Builder(String responseMessage, int responseCode) {
            this.responseMessage = responseMessage;
            this.responseCode = responseCode;
        }

        public Builder withNumFound(Long numFound) {
            this.numFound = numFound;
            return this;
        }

        public Builder withDocument(SolrDocumentList document) {
            this.document = document;
            return this;
        }

        public Builder withID(String ID) {
            this.ID = ID;
            return this;
        }

        public Builder withQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder withResponseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public Builder withCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withStart(String start) {
            this.start = start;
            return this;
        }

        public Builder withRow(String row) {
            this.row = row;
            return this;
        }

        public Builder withActivationCode(String activationCode) {
            this.activationCode = activationCode;
            return this;
        }

        public Builder withDto(List<FacetFieldDTO> dto) {
            this.dto = dto;
            return this;
        }

        public ResponseMessage build() {
            return new ResponseMessage(this);
        }
    }

    // ✅ Getters
    public String getResponseMessage() { return responseMessage; }
    public int getResponseCode() { return responseCode; }
    public Long getNumFound() { return numFound; }
    public SolrDocumentList getDocument() { return document; }
    public String getID() { return id; }
    public String getQuery() { return query; }
    public String getResponseType() { return responseType; }
    public Date getCreatedDate() { return createdDate; }
    public String getStart() { return start; }
    public String getRow() { return row; }
    public String getActivationCode() { return activationCode; }
    public List<FacetFieldDTO> getDto() { return dto; }
}
