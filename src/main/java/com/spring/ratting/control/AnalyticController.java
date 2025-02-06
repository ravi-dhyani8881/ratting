package com.spring.ratting.control;

import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.spring.ratting.domain.InnerAnalytic;
import com.spring.ratting.domain.InnerReviews;
import com.spring.ratting.service.CommonDocumentService;
import com.spring.ratting.util.ResponseMessage;
import com.spring.ratting.util.SolrUrls;
import com.spring.ratting.util.Utility;
import com.spring.ratting.validation.ValidationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Api(value = "Content Mangment System" , description = "This service used to perform operation on Content.")
@RestController
@ExposesResourceFor(AnalyticController.class)
@RequestMapping("/analytic")
public class AnalyticController {
	
	@Autowired
	CommonDocumentService commonDocumentService;
	
	@Autowired
	ValidationService validationService;
	
    String contentUrl=SolrUrls.contentUrl;
    
    String reviewUrl=SolrUrls.reviewUrl;
    
    String likeUrl=SolrUrls.likelUrl;
    
    String helpFullUrl=SolrUrls.helpFullUrl;    
		
	@ApiOperation(value = "This service used to add content")
	@RequestMapping(value="/addContent" , method=RequestMethod.POST)
	public ModelMap  addContent(@RequestBody Map<String, Object> payload , HttpServletResponse response, HttpServletRequest request,
			@RequestHeader(name="X-API-Key", required=true) String apiKey ,
			@RequestHeader(name="X-USER-ID", required=true) String userId) {
		
		ModelMap model=new ModelMap();
		String contentId=Utility.getUniqueId();
		
		
		if(validationService.validateApiKey(apiKey, userId) == 500 )
		{	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
			 
		}else if(validationService.validateApiKey(apiKey, userId) == 401) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			// return model.addAttribute("Message", new ResponseMessage("Invalid Api Key", 401));
			 return model.addAttribute("Message", new ResponseMessage.Builder("Invalid Api Key", 401).build());
		}
		try {
			payload.put("ID", contentId);
			payload.put("custId", userId);
			Object apiResponse = commonDocumentService.addDocumentByTemplate(payload, contentUrl);
			
			if(apiResponse instanceof Exception )
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
				return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	//	return model.addAttribute("Message", new ResponseMessage("Content added Sucesfully", 201,contentId,"created"));
		return model.addAttribute("Message", new ResponseMessage.Builder("Content added Sucesfully", 201).withID(contentId).withResponseType("created").build());
	}
	
	@ApiOperation(value = "This service used to update content")
	@RequestMapping(value="/updateContent" , method=RequestMethod.PUT)
	public ModelMap  updateContent(@RequestBody Map<String, Object> payload , HttpServletResponse response, HttpServletRequest request,
			@RequestHeader(name="X-API-Key", required=true) String apiKey ,
			@RequestHeader(name="X-USER-ID", required=true) String userId) {
		
		ModelMap model=new ModelMap();
		String contentId=null;
		
		if(validationService.validateApiKey(apiKey, userId) == 500 )
		{	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
			 
		}else if(validationService.validateApiKey(apiKey, userId) == 401) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		//	 return model.addAttribute("Message", new ResponseMessage("Invalid Api Key", 401));
			 return model.addAttribute("Message", new ResponseMessage.Builder("Invalid Api Key", 401).build());
		} else if(!payload.containsKey("ID")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		//	 return model.addAttribute("Message", new ResponseMessage("Unique ID missing from request, Invalid request",400));
			 return model.addAttribute("Message", new ResponseMessage.Builder("Unique ID missing from request", 400).build());
		}
		
		try {
			contentId=(String) payload.get("ID");
			Object apiResponse =commonDocumentService.advanceQueryByTemplate("ID:"+contentId, contentUrl);
					
			if(apiResponse instanceof Exception )
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
				return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
			}else if(((QueryResponse) apiResponse).getResults().size() == 0) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			//	return model.addAttribute("Message", new ResponseMessage("No Unique ID to update, Invalid ID",400 ));
				return model.addAttribute("Message", new ResponseMessage.Builder("No Unique ID to update, Invalid ID", 400).build());
			}else {
				SolrDocument solrDocument = ((QueryResponse) apiResponse).getResults().get(0);
				apiResponse =commonDocumentService.updateDocumentByTemplate(this.createDoc(payload, solrDocument), contentUrl) ;	
			}			
		//	return model.addAttribute("Message", new ResponseMessage("Content updated Sucesfully", 201,contentId,"sucess"));
			
			return model.addAttribute("Message", new ResponseMessage.Builder("Content updated Sucesfully", 201).withID(contentId).withResponseType("sucess").build());
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error",500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
		}	
	}
	
	@ApiOperation(value = "This service used to search content by query")
	@RequestMapping(value="/contentAnalytic" , method=RequestMethod.GET)
	public ModelMap  advanceSearch(@RequestParam(name = "query", required = true) String query,
			@RequestParam(name = "rows",  defaultValue = "8", required = false) String rows ,
			@RequestParam(name = "start",defaultValue = "0", required = false) String start,
			@RequestParam(name = "fl" ,defaultValue = "" , required = false) String fl ,
			@RequestParam(name = "fq" ,defaultValue = "" , required = false) String fq ,
			//default value asc|desc
			@RequestParam(name = "sort" ,defaultValue = "" , required = false) String sort,
			@RequestHeader(name="X-API-Key", required=true) String apiKey ,
			@RequestHeader(name="X-USER-ID", required=true) String userId,
		
			HttpServletRequest request, HttpServletResponse response
			) {
		ModelMap model=new ModelMap();
		Object contentResponse,reviewsResponse,likeResponse,helpFullResponse=null;
		
		
	//	Object apiDetails=null;
	//	String reviewDetailsQuery=null;
		
	//	String reviewQuery=null;
		
	//	Analytic analytic = null;
		

		
		List<InnerAnalytic> innerAnalyticObjectList=new ArrayList<InnerAnalytic>();
		
		
		if(validationService.validateApiKey(apiKey, userId) == 500 )
		{	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
			 
		}else if(validationService.validateApiKey(apiKey, userId) == 401) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			// return model.addAttribute("Message", new ResponseMessage("Invalid Api Key", 401));
			 return model.addAttribute("Message", new ResponseMessage.Builder("Invalid Api Key", 401).build());
		}else {
		//	query=Utility.getQuery(query, userId);
			Map<String, String> searchCriteria=new HashMap<String,String>(); 
			searchCriteria.put("q", query);
			searchCriteria.put("rows", rows);
			searchCriteria.put("start", start);
			searchCriteria.put("fl", fl);
			searchCriteria.put("fq", fq);
			searchCriteria.put("sort", sort);
			
			
		//	List<Analytic> ana=List<Analytic>();
				
			 contentResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, contentUrl);
			 
			 
			 
			 searchCriteria.put("q", "{!join from=ID to=reviewContentId fromIndex=content}"+query);
			 
			 reviewsResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, reviewUrl);
			
			 searchCriteria.put("q", "{!join from=ID to=contentId fromIndex=}"+query);
			 
			 likeResponse=commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, likeUrl);
			 
			 
			 searchCriteria.put("q", "{!join from=ID to=contentId fromIndex=content}"+query);
			 
			 helpFullResponse=commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, helpFullUrl);
			 
			 
			 if(contentResponse instanceof Exception )
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
				return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
			}else {
				
			//	model.addAttribute("content",((QueryResponse) apiResponse).getResults());
				
				
				
			((QueryResponse) contentResponse).getResults().forEach((C)-> {
			
			
				System.out.println("---content---->"+C.get("name"));
			
				InnerAnalytic innerAnalytic=new InnerAnalytic(null,null,null);
				innerAnalytic.setContent(C);
				
				
				
				((QueryResponse) reviewsResponse).getResults().forEach((R)-> {
					
					if(C.get("ID").equals(R.get("reviewContentId"))) {
						innerAnalytic.setReviews(R);
						innerAnalytic.setInnerReviews(new InnerReviews(R));
						
					}
				
				});		
				innerAnalyticObjectList.add(innerAnalytic);
			});
				
				
				
					
			//		searchCriteria.put("q","reviewContentId:"+K.get("ID"));
			//		searchCriteria.put("facet","on");
			//		searchCriteria.put("facet.field","reviewRatting");
					
					 
			//		 Object reviewsLocalResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, reviewUrl);
				
				
				
				
			
			
			
			
			
				
				
				
				
				model.addAttribute("content",((QueryResponse) contentResponse).getResults());
				
				model.addAttribute("reviews",((QueryResponse) reviewsResponse).getResults());
				
				model.addAttribute("like",((QueryResponse) likeResponse).getResults());
				
				model.addAttribute("helpFull",((QueryResponse) helpFullResponse).getResults());
				
				
				
			}
			 
		}
	//	model.addAttribute(innerAnalyticObjectList);
		 return model;
	}
	




	@ApiOperation(value = "This service delete content by query")
	@RequestMapping(value="/deleteByQuery" , method=RequestMethod.DELETE)
	public ModelMap  deleteByQuery(@RequestParam(name = "query", required = true) String query , 
			HttpServletResponse response, HttpServletRequest request,
			@RequestHeader(name="X-API-Key", required=true) String apiKey ,
			@RequestHeader(name="X-USER-ID", required=true) String userId) {
		
		ModelMap model=new ModelMap();
		
		if(validationService.validateApiKey(apiKey, userId) == 500 )
		{	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
			 
		}else if(validationService.validateApiKey(apiKey, userId) == 401) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			// return model.addAttribute("Message", new ResponseMessage("Invalid Api Key", 401));
			 return model.addAttribute("Message", new ResponseMessage.Builder("Invalid Api Key", 401).build());
		}
		
		Object apiResponse = commonDocumentService.deleteDocumentByTemplate(query,contentUrl);
		
		if(apiResponse instanceof Exception )
		{
			if(((Exception) apiResponse).getMessage().contains("SyntaxError")) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			//  	return model.addAttribute("Message", new ResponseMessage("Invalid Query or SyntaxError, SyntaxError", 401));
				 return model.addAttribute("Message", new ResponseMessage.Builder("Invalid Query or SyntaxError, SyntaxError", 401).build());
				
			}else{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
				return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
			}
		}
	//	return model.addAttribute("Message", new ResponseMessage("Content deleted Sucesfully with Query", 200,query,"deleted"));
		 return model.addAttribute("Message", new ResponseMessage.Builder("Content deleted Sucesfully with Query", 200).withQuery(query).withResponseType("deleted").build());
	}
	
	@ApiIgnore
	@ApiOperation(value = "This service delete content by query")
	@RequestMapping(value="/upload" , method=RequestMethod.POST)
	public String  uploadFile(@RequestParam(name = "file", required = true) MultipartFile file2,
			@RequestParam(name = "name", required = true) String name)
	{
		
	//	String Name=Arrays.asList(file2).get(0).getOriginalFilename();		
		return file2.toString();
	}
	
	@ApiIgnore
	@ApiOperation(value = "This service delete content by query")
	@RequestMapping(value="/upload2" , method=RequestMethod.POST)
	public String  uploadFile2(@RequestParam(name = "file", required = true) MultipartFile file2,
			@RequestParam(name = "name", required = true) String name)
	{
		 byte[] bytes;
		try {
			bytes = file2.getBytes();
			  Path path = Paths.get("C:\\aa\\" + file2.getOriginalFilename());
		         Files.write(path, bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return file2.toString();
	}
	
	@ApiIgnore
	@ApiOperation(value = "This service delete content by query")
	@RequestMapping(value="/upload3" , method=RequestMethod.POST)
	public String  uploadFile3(@RequestParam(name = "file", required = true) MultipartFile file2,
			@RequestParam(name = "name", required = true) String name) throws IOException
	{
		RestTemplate restTemplate = new RestTemplate();
		
		//thumbor Used for upload Images
		String serverUrl="http://192.168.29.190:8888/image";

		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // This nested HttpEntiy is important to create the correct
        // Content-Disposition entry with metadata "name" and "filename"
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename("ravi.jpeg")
                .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());  
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("filename", "ravi.jpeg");
        body.add("file", new ByteArrayResource(file2.getBytes()));
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
            		serverUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            System.out.println("-------------------------->"+response.getBody());
            
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }			
		return "ok";
	}

	public Map<String, Object> createDoc(Map<String, Object> payload , SolrDocument solrDocument) {
		solrDocument.forEach((k,v) ->{
	 		payload.put(k, v);
	 	});
		return payload;
	}

	
	
	
	
	
}