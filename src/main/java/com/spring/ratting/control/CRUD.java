package com.spring.ratting.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ratting.febe.LinkItem;
import com.spring.ratting.service.CommonDocumentService;
import com.spring.ratting.util.FacetFieldDTO;
import com.spring.ratting.util.FacetValueDTO;
import com.spring.ratting.util.ResponseMessage;
import com.spring.ratting.util.SolrUrls;
import com.spring.ratting.util.Utility;

import io.swagger.annotations.Api;
import com.fasterxml.jackson.databind.ObjectMapper;


@Api(value = "Content Mangment System" , description = "Service used to perform CRUD operation on Core.", tags = "CRUD")
@RestController
@ExposesResourceFor(ContentController.class)
@RequestMapping("*/")
public class CRUD {
	
	

	@Autowired
	CommonDocumentService commonDocumentService;
	
	
	//need to dynamic pick
	String url=SolrUrls.contentUrl;
	
	String baseUrl=SolrUrls.baseUrl;
	
	
	
	@PostMapping("/add")
    public <T> ResponseEntity<T> add(@RequestBody Map<String, Object> payload,HttpServletResponse response, HttpServletRequest request) {
       
		String requestUri = request.getRequestURI();
		System.out.println("--->"+requestUri.substring(0));
		
		int colonIndex = Utility.findNthIndexOf(requestUri, '/', 2);
				
		Object apiResponse = commonDocumentService.addDocumentAndExceptionByTemplate(payload, baseUrl+requestUri.substring(1, colonIndex));  
		
	//	  ResponseMessage successResponse = new ResponseMessage("Content added successfully", 201,null,null,payload.get("ID").toString(),null,null,null);
          
		  ResponseMessage successResponse= new ResponseMessage.Builder("Content added successfully", 201)
			.withID(payload.get("ID").toString())
			.withResponseType("created")
			.build();
		  
		 return new ResponseEntity<T>((T) successResponse,HttpStatus.OK);
    }
	
	
	@GetMapping("/findByQuery")
	public  ResponseEntity<ResponseMessage>  advanceSearch(@RequestParam(name = "query", required = true) String query,
			@RequestParam(name = "rows",  defaultValue = "8", required = false) String rows ,
			@RequestParam(name = "start",defaultValue = "0", required = false) String start,
			@RequestParam(name = "filterField" ,defaultValue = "" , required = false) String fl ,
			@RequestParam(name = "filterQuery" ,defaultValue = "" , required = false) String fq ,
			//default value asc|desc
			@RequestParam(name = "advanceField" ,defaultValue = "" , required = false) String[] facetField ,
			@RequestParam(name = "advanceQuery" ,defaultValue = "" , required = false) String facetQuery ,
			@RequestParam(name = "advance" ,defaultValue = "false" , required = false) String facet ,
			
			@RequestParam(name = "sort" ,defaultValue = "" , required = false) String sort,
			HttpServletRequest request, HttpServletResponse response
			) {
		
		List<FacetFieldDTO> advance=null;
		Map<String, String> searchCriteria=new HashMap<String,String>(); 
		SolrQuery solrQuery = new SolrQuery();
		ModifiableSolrParams searchParams = new ModifiableSolrParams();
	
		if (facetField != null) {
            for (String facetFields : facetField) {
                
                solrQuery.addFacetField(facetFields);
              //  searchCriteria.put("facet.field", facetFields);
            }
        }		   
		searchParams.add("q", query);
		searchParams.add("rows", rows);
		searchParams.add("start", start);
		searchParams.add("fl", fl);
		searchParams.add("fq", fq);
		searchParams.add("sort", sort);
	//	searchParams.add("facet.pivot", facetField);
		
		searchParams.add("facet.query", facetQuery);
		searchParams.add("facet", facet);
	
	//	solrQuery.addFacetField("try");
	//	solrQuery.addFacetField("contentType");
	//	solrQuery.addFacetField("ID");
		solrQuery.add(searchParams);
		
		var requestUri=request.getRequestURI();
		
	//	System.out.println("Reuquested Url------>"+requestUri+"------->"+query);
		
		int colonIndex = Utility.findNthIndexOf(request.getRequestURI(), '/', 2);
		
		var  apiResponse = ((QueryResponse)commonDocumentService.advanceSearchDocumentAndExceptionByTemplate(searchCriteria, baseUrl+requestUri.substring(1, colonIndex),solrQuery));

        List<FacetField> facetFieldsResponse =  apiResponse.getFacetFields();
		
      //  printFacetResults(facetFieldsResponse);
        
        if(facet.equals("true")) {
          advance=facetFieldsResponse.stream().map(this::mapToFacetFieldDTO).collect(Collectors.toList());
        }
		//  ResponseMessage successResponse = new ResponseMessage(null, 200,apiResponse.getResults().getNumFound(),apiResponse.getResults(),null,null,null,start,rows,advance);
		  
		 ResponseMessage successResponse= new ResponseMessage.Builder("Content added successfully", 200)
					.withNumFound(apiResponse.getResults().getNumFound())
					.withDocument(apiResponse.getResults())
					.withStart(start)
					.withRow(rows)
					.withDto(advance)
					.withResponseType("created")
					.build();
		  
		//  ResponseMessage(String responseMessage, int responseCode, Long numFound, SolrDocumentList document,String iD, String query, String responseType) 
		 
		  return new ResponseEntity<>(successResponse, HttpStatus.OK);	
	}
	
	
	private FacetFieldDTO mapToFacetFieldDTO(FacetField facetField) {
        FacetFieldDTO facetFieldDTO = new FacetFieldDTO();
        facetFieldDTO.setFieldName(facetField.getName());

        List<FacetValueDTO> valuesDTO = facetField.getValues().stream()
                .map(count -> new FacetValueDTO(count.getName(), count.getCount()))
                .collect(Collectors.toList());

        facetFieldDTO.setValues(valuesDTO);

        return facetFieldDTO;
    }
	
	
	

	
	private static void printFacetResults(List<FacetField> facetFieldsResponse) {
        for (FacetField facetField : facetFieldsResponse) {
            System.out.println("Field: " + facetField.getName());
            List<Count> values = facetField.getValues();
            for (Count value : values) {
                System.out.println("  " + value.getName() + ": " + value.getCount());
            }
        }
    }

	//This function needs to delete as work finished
	@PostMapping("/add2")
    public <T> ResponseEntity<T> add2(@RequestBody Map<String, Object> payload,HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
       
		String requestUri = request.getRequestURI();
		System.out.println("--->"+requestUri.substring(0));
		
		int colonIndex = Utility.findNthIndexOf(requestUri, '/', 2);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		

	    // Convert payload to JSON string
	    String payloadJson = objectMapper.writeValueAsString(payload);
		
	    System.out.println("------->"+payloadJson);
	    
		Map<String, Object> payload2 = new HashMap<String, Object>();
		
		payload2.put("ID", Utility.getUniqueId());
		payload2.put("reviewComments_s", payloadJson);
				
		Object apiResponse = commonDocumentService.addDocumentAndExceptionByTemplate(payload2, baseUrl+requestUri.substring(1, colonIndex));  
		
		 // ResponseMessage successResponse = new ResponseMessage("Content added successfully", 201,null,null,payload.get("ID").toString(),null,null);
          
		  ResponseMessage successResponse= new ResponseMessage.Builder("Content added successfully", 201)
					.withID(payload.get("ID").toString())
					.build();
		  
		 return new ResponseEntity<T>((T) successResponse,HttpStatus.OK);
    }
	
	
	

	
	
	
	 @GetMapping("/parse-response")
	    public String parseResponse() throws Exception {
	        String jsonResponse = "{\n"
	        		+ "    \"data\": [\n"
	        		+ "        {\n"
	        		+ "            \"updatedAt\": \"2024-04-20 10:01:34\",\n"
	        		+ "            \"createdAt\": \"2024-04-20 10:01:34\",\n"
	        		+ "            \"id\": \"F6vcfFNsiiLpWckiO2uDZ\",\n"
	        		+ "            \"name\": \"ravi2\",\n"
	        		+ "            \"box\": \"{\\\"x\\\":22,\\\"y\\\":-140,\\\"w\\\":1344,\\\"h\\\":753,\\\"clientW\\\":1344,\\\"clientH\\\":753}\",\n"
	        		+ "            \"linkDict\": \"{\\\"C6r0zywoU_yMYSexTmIST\\\":{\\\"id\\\":\\\"C6r0zywoU_yMYSexTmIST\\\",\\\"name\\\":null,\\\"endpoints\\\":[{\\\"id\\\":\\\"LFVjP74EXU11INto3bAPj\\\",\\\"fieldId\\\":\\\"bWyEEGQXT_Gk_4jlGP8wr\\\",\\\"relation\\\":\\\"1\\\"},{\\\"id\\\":\\\"lizobCfDEH4h1N4oBIvd4\\\",\\\"fieldId\\\":\\\"S__MHXW82t3N4IrFTEOpo\\\",\\\"relation\\\":\\\"*\\\"}]},\\\"6VHpaPKngwRu7bv71rzkg\\\":{\\\"id\\\":\\\"6VHpaPKngwRu7bv71rzkg\\\",\\\"name\\\":null,\\\"endpoints\\\":[{\\\"id\\\":\\\"LFVjP74EXU11INto3bAPj\\\",\\\"fieldId\\\":\\\"bWyEEGQXT_Gk_4jlGP8wr\\\",\\\"relation\\\":\\\"1\\\"},{\\\"id\\\":\\\"mt7wkKwRGAXao_CPofbCl\\\",\\\"fieldId\\\":\\\"nHXNr06_MkFmGuh3EjqYV\\\",\\\"relation\\\":\\\"*\\\"}]},\\\"xBtc4HqaP-ioTbkyXHcph\\\":{\\\"id\\\":\\\"xBtc4HqaP-ioTbkyXHcph\\\",\\\"name\\\":null,\\\"endpoints\\\":[{\\\"id\\\":\\\"mt7wkKwRGAXao_CPofbCl\\\",\\\"fieldId\\\":\\\"l8bGwdCrXlrnnOJgCvZZD\\\",\\\"relation\\\":\\\"1\\\"},{\\\"id\\\":\\\"LFVjP74EXU11INto3bAPj\\\",\\\"fieldId\\\":\\\"bWyEEGQXT_Gk_4jlGP8wr\\\",\\\"relation\\\":\\\"*\\\"}]}}\",\n"
	        		+ "            \"tableDict\": \"{\\\"mt7wkKwRGAXao_CPofbCl\\\":{\\\"id\\\":\\\"mt7wkKwRGAXao_CPofbCl\\\",\\\"name\\\":\\\"ravi\\\",\\\"x\\\":189,\\\"y\\\":300,\\\"fields\\\":[{\\\"id\\\":\\\"l8bGwdCrXlrnnOJgCvZZD\\\",\\\"name\\\":\\\"ravi-field\\\",\\\"type\\\":\\\"INTEGER\\\",\\\"note\\\":\\\"\\\",\\\"dbdefault\\\":\\\"\\\",\\\"pk\\\":true,\\\"increment\\\":true},{\\\"id\\\":\\\"nHXNr06_MkFmGuh3EjqYV\\\",\\\"name\\\":\\\"ravi-Field2\\\",\\\"type\\\":\\\"VARCHAR\\\",\\\"unique\\\":false,\\\"note\\\":\\\"\\\",\\\"dbdefault\\\":\\\"\\\"}]},\\\"LFVjP74EXU11INto3bAPj\\\":{\\\"id\\\":\\\"LFVjP74EXU11INto3bAPj\\\",\\\"name\\\":\\\"dhyani\\\",\\\"x\\\":597,\\\"y\\\":-33,\\\"fields\\\":[{\\\"id\\\":\\\"bWyEEGQXT_Gk_4jlGP8wr\\\",\\\"name\\\":\\\"dhyani-field\\\",\\\"type\\\":\\\"INTEGER\\\",\\\"note\\\":\\\"\\\",\\\"dbdefault\\\":\\\"\\\",\\\"pk\\\":true,\\\"increment\\\":true}]},\\\"lizobCfDEH4h1N4oBIvd4\\\":{\\\"id\\\":\\\"lizobCfDEH4h1N4oBIvd4\\\",\\\"name\\\":\\\"second\\\",\\\"x\\\":911,\\\"y\\\":163,\\\"fields\\\":[{\\\"id\\\":\\\"S__MHXW82t3N4IrFTEOpo\\\",\\\"name\\\":\\\"second-field\\\",\\\"type\\\":\\\"INTEGER\\\",\\\"note\\\":\\\"\\\",\\\"dbdefault\\\":\\\"\\\",\\\"pk\\\":true,\\\"increment\\\":true}]}}\"\n"
	        		+ "        }\n"
	        		+ "    ]\n"
	        		+ "}"; // Your JSON response here
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	     var  dd= objectMapper.readValue(jsonResponse, ResponseData.class);
	        
	     
	     Map<String, LinkItem> linkDict = objectMapper.readValue(dd.data.get(0).getLinkDict(), new TypeReference<Map<String, LinkItem>>() {});

	        System.out.println(linkDict.get(0).getEndpoints().get(0).getRelation());
	        return linkDict.get(0).getEndpoints().get(0).getRelation();
	    }
	    

	
	
}