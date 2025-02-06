package com.spring.ratting.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.ratting.service.CommonDocumentService;
import com.spring.ratting.solr.SolrConnection;
import com.spring.ratting.util.ResponseMessage;
import com.spring.ratting.util.SolrUrls;
import com.spring.ratting.util.Utility;
import com.spring.ratting.validation.ValidationService;

import io.swagger.annotations.Api;


@Api(value = "Review and ratting managment system", description = "Service used for add review and ratting on content", tags = "Comments")
@RestController
@ExposesResourceFor(CommentRattingController.class)
@RequestMapping("review-ratting")
public class CommentRattingController implements SolrUrls {
	
//	@Autowired
//	ReviewRattingRepository reviewRattingRepository;
	
	@Autowired
	SolrConnection solrRatting;
	
	@Autowired
	CommonDocumentService commonDocumentService;
	
	@Autowired
	ValidationService validationService;
	
	@Autowired
	SolrConnection solrConnection;
	
	
	
	
    String reviewUrl=SolrUrls.reviewUrl;
    String contentUrl=SolrUrls.contentUrl;
    String solrAnalyticUrl=SolrUrls.solrAnalyticUrl;
    String helpFullUrl=SolrUrls.helpFullUrl;
    String likeUrl=SolrUrls.likelUrl;
    String sampleUrl=SolrUrls.sampleUrl;

    /**
    
    @ApiIgnore
	@RequestMapping(value="/add2" , method=RequestMethod.POST)
	public ModelMap reviewsRating2(@RequestParam(name = "reviewComments", required = true) String reviewComments,
								 @RequestParam(name = "reviewRatting", required = true) int ratting,
								 @RequestParam(name = "reviewUserId", required = true) String reviewUserId,
								 @RequestParam(name = "reviewContentId", required = true) String reviewContentId
								// @RequestBody ReviewRatting rev
			) {	
		
		ModelMap model=new ModelMap();
		if (reviewRattingRepository.findByContentUserId(reviewContentId+"_"+reviewUserId)!=null)		{
			System.out.println("------------------------------------------------"+"review already added");
			return model.addAttribute("Message", new ResponseMessage("review already added", "AlreadyExist"));
		}		
		else {		
		String reviewId=Utility.getUniqueId();
		ReviewRatting reviewRatting=new ReviewRatting();
		reviewRatting.setId(reviewId);
		reviewRatting.setReviewRatting(ratting);
		reviewRatting.setReviewComments(reviewComments);
		reviewRatting.setReviewDateTime(Utility.getCurrentDateAndTime());
		reviewRatting.setReviewUpdatedateTime("none");
		reviewRatting.setReviewUserId(reviewUserId);
		reviewRatting.setReviewContentId(reviewContentId);		
		reviewRatting.setContentUserId(reviewContentId+"_"+reviewUserId);
		try {
			reviewRattingRepository.save(reviewRatting);
			solrRatting.addSolrRattingDoc(reviewRatting, SolrUrls.solrAnalyticUrl);
			model.addAttribute("reviewId",reviewId);
			return model.addAttribute("Message", new ResponseMessage("Review added Sucesfully", "Added"));
		}catch (Exception e) {
			return model.addAttribute("Message", e.getMessage());
			}
		}
	}
    
    
    
    @ApiIgnore
	@RequestMapping(value="/markHelpful2" , method=RequestMethod.POST)
	public ModelMap reviewsHelpful2(@RequestParam(name = "reviewId", required = true) String reviewId,
								 @RequestParam(name = "reviewUserId", required = true) String reviewUserId,
								 @RequestParam(name = "reviewContentId", required = true) String reviewContentId,
								 @RequestParam(name = "helpfull", required = true) String helpfull
								 
			) {	
		ModelMap model=new ModelMap();
	//	String helpfulId=Utility.getUniqueId();
		
		SolrInputDocument document = new SolrInputDocument();
		document.addField("helpfulId", reviewId+"_"+reviewUserId);
		document.addField("reviewId", reviewId);		
		document.addField("userId", reviewUserId);
		document.addField("contentId", reviewContentId);
		document.addField("helpFull", helpfull);		
		 
		 if(reviewRattingRepository.existsById(reviewId))		
		solrRatting.addDocument(SolrUrls.helpFullUrl, document);
		 else {
			 return model.addAttribute("Message", new ResponseMessage("Review does not exists", "NotAdded",null));
		 }
		return model.addAttribute("Message", new ResponseMessage("Review added as helpfull sucesfully", "Added"));
	}
	
	
	@ApiIgnore
	@RequestMapping(value="/like2" , method=RequestMethod.POST)
	public ModelMap reviewsLike2(@RequestParam(name = "reviewId", required = true) String reviewId,
								 @RequestParam(name = "reviewUserId", required = true) String reviewUserId,
								 @RequestParam(name = "reviewContentId", required = true) String reviewContentId,
								 @RequestParam(name = "like", required = true) String like								 
			) {	
		ModelMap model=new ModelMap();
		
		SolrInputDocument document = new SolrInputDocument();
	//	String likeId=Utility.getUniqueId();
		
		document.addField("likeId", reviewId+"_"+reviewUserId);
		document.addField("reviewId", reviewId);		
		document.addField("userId", reviewUserId);
		document.addField("contentId", reviewContentId);
		document.addField("like", like);
		
		Optional<ReviewRatting> reviewRatting=reviewRattingRepository.findById(reviewId);
		
		 if(reviewRatting.isPresent())		{
			 solrRatting.addDocument(SolrUrls.likelUrl, document);	
			 ReviewRatting updatedRatting=reviewRatting.get();
			 
			
			 QueryResponse queryResponse=solrRatting.serachDocument(SolrUrls.likelUrl, "contentId:"+reviewContentId+ "&& reviewId:"+reviewId+" && like:Yes");
			
			 System.out.println("-------->"+queryResponse.getResults().getNumFound());
			 
			 updatedRatting.setLike(Long.toString(queryResponse.getResults().getNumFound())); 
			 reviewRattingRepository.save(updatedRatting);
		 }
		 else {
			 return model.addAttribute("Message", new ResponseMessage("Review does not exists", "NotAdded"));			
		 }
		return model.addAttribute("Message", new ResponseMessage("Review Like added sucesfully", "Added"));
	}	
	
	
	@RequestMapping(value="/findReviewByContentId" , method=RequestMethod.GET)
	public ModelMap  findReviewByContentId(@RequestParam(name = "reviewContentId", required = true) 
	String reviewContentId) {
		ModelMap model=new ModelMap();
	//	model.addAttribute("review", reviewRattingRepository.findById(contentId).get());
		
		ReviewRattingStats reviewRattingStats=new ReviewRattingStats();
		List<ReviewRatting> reviewRatting=	reviewRattingRepository.findReviewByContentId(reviewContentId);
		
		reviewRatting.forEach((k) ->{
			
		
			SolrDocument solrDoc= solrRatting.serachDocument(SolrUrls.solrCommentRattingAnalyticUrl, "contentId:"+k.getReviewContentId()).getResults().get(0);
			
			reviewRattingStats.setAverageRatting((int)solrDoc.get("averageRatting"));
			reviewRattingStats.setTotalCount((int)solrDoc.get("rattingCount"));
			reviewRattingStats.setOneStart((int)solrDoc.get("ratting1"));
			reviewRattingStats.setTwoStart((int)solrDoc.get("ratting2"));
			reviewRattingStats.setThreeStart((int)solrDoc.get("ratting3"));
			reviewRattingStats.setFourStart((int)solrDoc.get("ratting4"));
			reviewRattingStats.setFiveStart((int)solrDoc.get("ratting5"));
				
			k.setReviewRattingStats(reviewRattingStats);
						
			model.addAttribute(k.getId(),k);
			// model.addAttribute("Analytic"+k.getId(),solrRatting.serachDocument(SolrUrls.solrCommentRattingAnalyticUrl, "contentId:"+k.getReviewContentId()).getResults());			
		});	
		
	//	solrRatting.serachDocument(solrUrl, query);
	return model;
	}
	
	
	**/
	
	@RequestMapping(value="/markHelpful" , method=RequestMethod.POST)
	public ModelMap reviewsHelpful(@RequestBody Map<String, Object> payload,HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(name="X-API-Key", required=true) String apiKey ,
			@RequestHeader(name="X-USER-ID", required=true) String userId) {	
		
//			{
//			"reviewId":"ae4e5b21-5ca3-490d-b2ac-77166868064b",
//			"contentId":"7d8c63bf-4d3c-43b3-b2a5-81c9b3463d1c",
//			"userId":"e8f17091-cf8b-4c06-ad1c-ba4f237b613e",
//			"helpFull":"none"
//			}
		ModelMap model=new ModelMap();
		String helpFullId=null;
		Object apiHelpFullResponse=null;
		helpFullId=Utility.getUniqueId();
		
		String contentId=(String) payload.get("contentId");
		String helpfullUserId=(String) payload.get("userId");
		String reviewId=(String) payload.get("reviewId");
		

		if(validationService.validateApiKey(apiKey, userId) == 500 )
		{	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down, Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down, Internal server error", 500)
					.build());
			 
		}else if(validationService.validateApiKey(apiKey, userId) == 401) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		//	 return model.addAttribute("Message", new ResponseMessage("Invalid Api Key", 403));
			return model.addAttribute("Message", new ResponseMessage.Builder("Invalid Api Key", 403)
					.build());
		}
		
		String reviewExistQuery="ID:"+reviewId+ " && " + "reviewContentId:"+contentId;
		Object reviewExistResponse =commonDocumentService.advanceQueryByTemplate(reviewExistQuery, reviewUrl);
		
		if(reviewExistResponse instanceof Exception )
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down, Internal server error",500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down, Internal server error", 500)
					.build());
			
		}else if(((QueryResponse) reviewExistResponse).getResults().size() == 0) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		//	return model.addAttribute("Message", new ResponseMessage("Invalid review/content Id", 401));
			return model.addAttribute("Message", new ResponseMessage.Builder("Invalid review/content Id", 401)
					.build());
		}else {
			payload.put("ID", helpFullId);
			payload.put("helpfulId", contentId+"_"+reviewId+"_"+helpfullUserId);
			apiHelpFullResponse =commonDocumentService.addDocumentByTemplate(payload, helpFullUrl);
		}
		if(apiHelpFullResponse instanceof Exception )
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down,Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down, Internal server error", 500)
					.build());
			
		}else {
		//	return model.addAttribute("Message", new ResponseMessage("Review Mark as helpfull sucesfully", 201, helpFullId,"created"));
			return model.addAttribute("Message", new ResponseMessage.Builder("Review Mark as helpfull sucesfully", 201)
					.withID(helpFullId)
					.withResponseType("created")
					.build());			
		}
		}
	
	@RequestMapping(value="/like-dislike" , method=RequestMethod.POST)
	public ModelMap reviewsLike(@RequestBody Map<String, Object> payload,HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(name="X-API-Key", required=true) String apiKey ,
			@RequestHeader(name="X-USER-ID", required=true) String userId) {	
		
//			{
//			"reviewId":"ae4e5b21-5ca3-490d-b2ac-77166868064b",
//			"contentId":"7d8c63bf-4d3c-43b3-b2a5-81c9b3463d1c",
//			"userId":"e8f17091-cf8b-4c06-ad1c-ba4f237b613e",
//			"like":"Yes"
//			}
		ModelMap model=new ModelMap();		
		String likeId=null;
		Object apiLikeResponse=null;
		likeId=Utility.getUniqueId();
		
		String contentId=(String) payload.get("contentId");
		String likeUserId=(String) payload.get("userId");
		String reviewId=(String) payload.get("reviewId");
		
		if(validationService.validateApiKey(apiKey, userId) == 500 )
		{	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down, Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down, Internal server error", 500)
																	.build());		
			 
		}else if(validationService.validateApiKey(apiKey, userId) == 401) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			// return model.addAttribute("Message", new ResponseMessage("Invalid Api Key", 403));
			return model.addAttribute("Message", new ResponseMessage.Builder("Invalid Api Key", 403).build());	
		}
		
		String reviewExistQuery="ID:"+reviewId+ " && " + "reviewContentId:"+contentId;
		Object reviewExistResponse =commonDocumentService.advanceQueryByTemplate(reviewExistQuery, reviewUrl);
		
		if(reviewExistResponse instanceof Exception )
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down, Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down, Internal server error", 500).build());
		}else if(((QueryResponse) reviewExistResponse).getResults().size() == 0) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		//	return model.addAttribute("Message", new ResponseMessage("Invalid review/content Id", 401));
			return model.addAttribute("Message", new ResponseMessage.Builder("Invalid review/content Id", 401).build());
		}else {
			payload.put("ID", likeId);
			payload.put("likeId", contentId+"_"+reviewId+"_"+likeUserId);
			apiLikeResponse =commonDocumentService.addDocumentByTemplate(payload, likeUrl);
		}
		if(apiLikeResponse instanceof Exception )
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down, Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down, Internal server error", 500).build());
		}else {
		//	return model.addAttribute("Message", new ResponseMessage("Review Mark as like sucesfully", 201, likeId,"created"));
			return model.addAttribute("Message", new ResponseMessage.Builder("Review Mark as like sucesfully", 201)
					.withID(likeId)
					.withResponseType("created")
					.build());
		}	
	}
	
	
	
	
	@RequestMapping(value="/findAllReview" , method=RequestMethod.GET)
	public ModelMap  findAllReview( @RequestParam(name = "noOfRecords", required = true) String noOfRecords,
			@RequestParam(name = "pageNumber", required = true) String pageNumber
	
			) {
		ModelMap model=new ModelMap();
		String query="*:*";
		Map<String, String> searchCriteria=new HashMap<String, String>();
		searchCriteria.put("q", query);
		searchCriteria.put("rows", noOfRecords);
		searchCriteria.put("start", pageNumber);
		QueryResponse queryResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, reviewUrl);
	//	model.addAttribute(new ResponseMessage(queryResponse.getResults().getNumFound(), queryResponse.getResults()));
		
		ResponseMessage successResponse= new ResponseMessage.Builder(null, 201)
				.withNumFound(queryResponse.getResults().getNumFound())
				.withDocument(queryResponse.getResults())
				.build();
	
		model.addAttribute(successResponse);
		return model;
	}
	
	@RequestMapping(value="/findAllReviewOnContent" , method=RequestMethod.GET)
	public ModelMap findAllReviewOnContent(@RequestParam(name = "contentId", required = true) String contentId,
	@RequestParam(name = "noOfRecords", required = true) 
	int noOfRecords, @RequestParam(name = "pageNumber", required = true) int pageNumber
			) {
	
		ModelMap model=new ModelMap();
		String query="reviewContentId:"+contentId;
		Map<String, String> searchCriteria=new HashMap<String, String>();
		searchCriteria.put("q", query);
		searchCriteria.put("rows", Integer.toString(noOfRecords));
		searchCriteria.put("start", Integer.toString(pageNumber));
		QueryResponse queryResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, reviewUrl);
	
	//	model.addAttribute(new ResponseMessage(queryResponse.getResults().getNumFound(), queryResponse.getResults()));
	
		ResponseMessage successResponse= new ResponseMessage.Builder(null, 201)
				.withNumFound(queryResponse.getResults().getNumFound())
				.withDocument(queryResponse.getResults())
				.build();
		
		model.addAttribute(successResponse);
		return model;
	
	}
	
	// To check how many likes in an reviews 
	
	@RequestMapping(value="/findAnalyticLikeOnReview" , method=RequestMethod.GET)
	public ModelMap findAnalyticOnReview(@RequestParam(name = "contentId", required = true) String contentId,
			@RequestParam(name = "reviewId", required = true) String reviewId,
	@RequestParam(name = "noOfRecords", required = true) 
	int noOfRecords, @RequestParam(name = "pageNumber", required = true) int pageNumber
			) {
	
		ModelMap model=new ModelMap();
		String query="(contentId:"+contentId+" AND "+"reviewId:"+reviewId+")";
		Map<String, String> searchCriteria=new HashMap<String, String>();
		searchCriteria.put("q", query);
		searchCriteria.put("rows", Integer.toString(noOfRecords));
		searchCriteria.put("start", Integer.toString(pageNumber));
		searchCriteria.put("facet", "true");
		searchCriteria.put("facet.field", "like");
	//	searchCriteria.put("fl", "userId");
		
		QueryResponse queryResponse = null;
	    queryResponse=commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, likelUrl);
	
		
		Map<String, Long> fieldCounts = new HashMap<>();
		
		 List<FacetField> clusterFields =queryResponse.getFacetFields();
		
	
		 List<String> clusterResponse = new ArrayList<String>();
		 
			    FacetField clusterFacets = clusterFields.get(0);
			    for (FacetField.Count clusterCount : clusterFacets.getValues()) {
			      clusterResponse.add(clusterCount.getName());
			      clusterCount.getCount();
			      
			      fieldCounts.put(clusterCount.getName(), clusterCount.getCount());
			     
			      System.out.println("-------clusterCount.getCount()-->"+clusterCount.getCount()+"==============clusterCount.getName()========>"+clusterCount.getName());
			    
			    
			    }
			  
			
		 model.addAttribute("Total Count",queryResponse.getResults().getNumFound());
	//	 model.addAttribute("Response", ((QueryResponse) queryResponse).getResults());
		
		 
		model.addAttribute("Reviews stats ",fieldCounts);
		return model;
	
	}

	@RequestMapping(value="/findAllReviewOnContentOnly" , method=RequestMethod.GET)
	public ModelMap findAllReviewOnContentOnly(@RequestParam(name = "contentId", required = true) String contentId,
	@RequestParam(name = "noOfRecords", required = true) 
	int noOfRecords, @RequestParam(name = "pageNumber", required = true) int pageNumber
			) {
	
		ModelMap model=new ModelMap();
		Object apiResponse=null;
		String query="reviewContentId:"+contentId;
		Map<String, String> searchCriteria=new HashMap<String, String>();
		searchCriteria.put("q", query);
		searchCriteria.put("rows", Integer.toString(noOfRecords));
		searchCriteria.put("start", Integer.toString(pageNumber));
		searchCriteria.put("facet", "true");
		searchCriteria.put("q.op", "O");
		searchCriteria.put("facet.field", "reviewRatting");
		
	//	searchCriteria.put("fl", "userId");
	    apiResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, reviewUrl);
		
	
     	// model.putAll(model)
		 
	//	model.addAttribute("solr",queryResponse.getResults().toArray());
	//	model.addAllAttributes(queryResponse.getResults());
		
		
		return model.addAttribute("result",((QueryResponse) apiResponse).getResults());

	}
	
	
	
	
	@RequestMapping(value="/findReviewAnalytic" , method=RequestMethod.GET)
	public ModelMap findReviewAnalytic(@RequestParam(name = "contentId", required = true) String contentId,
	@RequestParam(name = "noOfRecords", required = true) 
	int noOfRecords, @RequestParam(name = "pageNumber", required = true) int pageNumber
			) {
	
		ModelMap model=new ModelMap();	
		String query="reviewContentId:"+contentId;
		Map<String, String> searchCriteria=new HashMap<String, String>();
		searchCriteria.put("q", query);
		searchCriteria.put("rows", Integer.toString(noOfRecords));
		searchCriteria.put("start", Integer.toString(pageNumber));
		searchCriteria.put("facet", "true");
		searchCriteria.put("facet.field", "reviewRatting");
		
	//	searchCriteria.put("facet.query", "*:*");

		HttpHeaders headers = new HttpHeaders();		
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    headers.set("X-API-Key", "486f334e-f894-46e8-9d72-7800f6ce1bb6");
	    headers.set("X-USER-ID", "74a1932c-aa62-4ff1-9e75-2f0dbe9e5c57");
	    HttpEntity <String> entity = new HttpEntity<String>(headers);
		
	//	Map<String, String> response2=resttemplate.exchange("http://192.168.1.101:8983/solr/review/select?facet.field=reviewRatting&facet.query=*%3A*&facet.sort=count&facet=true&fl=reviewRatting&q=reviewContentId%3A4&rows=1&start=0",HttpMethod.GET, entity,new ParameterizedTypeReference<Map<String, String>>() {}).getBody();

	//	Map<String, String> response2= commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, reviewUrl);
		
	   
	    
	    
	//	searchCriteria.put("fl", "reviewRatting");
	    QueryResponse queryResponse = null;
	    List<FacetField>	ff2=null;
	    
	   queryResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, reviewUrl);
	    
	   
	
	    
		ff2=	queryResponse.getFacetFields();
		FacetField	ff=	queryResponse.getFacetField("reviewRatting");
		
		
		
		
		Map<String, Long> fieldCounts = new HashMap<>();
		
		 List<FacetField> clusterFields =queryResponse.getFacetFields();
		
		 long average=0;
		 List<String> clusterResponse = new ArrayList();
		 
			    FacetField clusterFacets = clusterFields.get(0);
			    for (FacetField.Count clusterCount : clusterFacets.getValues()) {
			      clusterResponse.add(clusterCount.getName());
			      clusterCount.getCount();
			      
			      fieldCounts.put(clusterCount.getName(), clusterCount.getCount());
			      average=average+ (clusterCount.getCount()* Integer.parseInt(clusterCount.getName() ));
			      
			      System.out.println("-------clusterCount.getCount()-->"+clusterCount.getCount()+"==============clusterCount.getName()========>"+clusterCount.getName());
			    
			      System.out.println(average);
			    }
			  
		 
		 
		
		
		
		// List<Count> gg=ff2.get(0).getValues();
		 model.addAttribute("Total Reviews",queryResponse.getResults().getNumFound());
		model.addAttribute("Reviews stats ",fieldCounts);
		model.addAttribute("Average Ratting", average>0 ? Math.round(average/queryResponse.getResults().getNumFound()):0);
	//	model.addAllAttributes(queryResponse.getResults());
		return model;
	
	}
	
	@RequestMapping(value="/{reviewContentId}" , method=RequestMethod.GET)
	public SolrDocument findOneReviewOnContentOnly(@PathVariable(required = false) String reviewContentId,
	@RequestParam(name = "noOfRecords", required = true, defaultValue = "12") 
	int noOfRecords, @RequestParam(name = "pageNumber", required = true, defaultValue = "0") int pageNumber
			) {
	
		ModelMap model=new ModelMap();
		String query="reviewContentId:"+reviewContentId;
		Map<String, String> searchCriteria=new HashMap<String, String>();
		searchCriteria.put("q", query);
		searchCriteria.put("rows", Integer.toString(noOfRecords));
		searchCriteria.put("start", Integer.toString(pageNumber));
		QueryResponse queryResponse = commonDocumentService.advanceSearchDocument(searchCriteria, reviewUrl);
	
	
		
		 
		
		// model.putAll(model)
		 
		model.addAttribute("solr",queryResponse.getResults().toArray());
	//	model.addAllAttributes(queryResponse.getResults());
		return queryResponse.getResults().get(0);
	
	}
	
	@RequestMapping(value="/add" , method=RequestMethod.POST)
	public ModelMap reviewsRating(@RequestBody Map<String, Object> payload,HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(name="X-API-Key", required=true) String apiKey ,
			@RequestHeader(name="X-USER-ID", required=true) String userId) {	
		// Example payload in body
//			{
//			"reviewComments":"Hello",
//			"reviewRatting":"1",

//			"reviewContentId":"1"
//			}
//		
		ModelMap model=new ModelMap();
		payload.containsKey("reviewUserId");
		payload.containsKey("reviewContentId");
		int validationResult=validationService.validateApiKey(apiKey, userId);
		if(validationResult == 500 )
		{	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down, Internal server error", 500));
			
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down, Internal server error", 500).build());
			
			 
		}else if(validationResult == 401) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		//	 return model.addAttribute("Message", new ResponseMessage("Invalid Api Key", 403));
			return model.addAttribute("Message", new ResponseMessage.Builder("Invalid Api Key", 403).build());
		}
		
		String queryContentExits="ID:"+(String)payload.get("reviewContentId");
		
		String queryReview="reviewUserId:"+userId+" && reviewContentId:"+(String)payload.get("reviewContentId");
		
		Object contentResponse =commonDocumentService.advanceQueryByTemplate(queryContentExits, contentUrl);
		
		Object reviewExitsResponse =commonDocumentService.advanceQueryByTemplate(queryReview, reviewUrl);
		
		if(contentResponse instanceof Exception || reviewExitsResponse instanceof Exception)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down, Internal server error", 500));
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down, Internal server error", 500)
					.build());
			
			
		}else if(((QueryResponse) contentResponse).getResults().size() == 0) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		//	return model.addAttribute("Message", new ResponseMessage("No content found to update, Invalid Content ID", 204));
			return model.addAttribute("Message", new ResponseMessage.Builder("No content found to update, Invalid Content ID", 204)
					.build());
			
		}else if( ((QueryResponse) reviewExitsResponse).getResults().size() >0 ) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
		//	return model.addAttribute("Message", new ResponseMessage("User Already added review to the content", 403));
			return model.addAttribute("Message", new ResponseMessage.Builder("User Already added review to the content", 403)
														.build());
			
		}else {
			String reviewId=Utility.getUniqueId();
			
			payload.put("ID", reviewId);
			payload.put("reviewUserId", userId);
			Object apiResponse =commonDocumentService.addDocumentByTemplate(payload, reviewUrl);
//			Map<String, Object> solrDocument= new HashMap<String, Object>();
//			solrDocument.put("reviewId", reviewId);
//			solrDocument.put("contentId", (String)payload.get("reviewContentId"));
//			solrDocument.put("userId", (String)payload.get("reviewUserId"));
//			solrDocument.put("ratting", (String)payload.get("reviewRatting"));
//			commonDocumentService.addDocument(solrDocument, solrAnalyticUrl) ;		
			
			if(contentResponse instanceof Exception || reviewExitsResponse instanceof Exception)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//	return model.addAttribute("Message", new ResponseMessage("Server down, Internal server error", 500));
				return model.addAttribute("Message", new ResponseMessage.Builder("Server down, Internal server error", 500).build());
				
			}
		//	return model.addAttribute("Message", new ResponseMessage("Review added Sucesfully", 201,reviewId,"created"));
			
			return model.addAttribute("Message", new ResponseMessage.Builder("Review added Sucesfully", 201).withResponseType("created").withID(reviewId).build());
		}
		}
	
	
	@RequestMapping(value="/testApi" , method=RequestMethod.POST)
	public String calltest(
			@RequestBody Map<String, String> payload
			) {
	
	//	System.out.println((String)payload.get("idBrand"));
	//	System.out.println("Ratting world2 ---------->"+UUID.randomUUID().toString());

	//	System.out.println("Ratting world2 ---------->"+(String)payload.get("title"));

	//	System.out.println("Ratting world2 ---------->"+(String)payload.get("content"));
		
		Map<String, Object> payload2= new HashMap<String, Object>();
		
		payload2.put("id",Utility.getUniqueId());
		payload2.put("title",(String)payload.get("title"));
		
		payload2.put("content","");
		payload2.put("content_text",payload.get("content"));
		
		
		Object apiResponse =commonDocumentService.addDocumentByTemplate(payload2, sampleUrl);
		
		
		return "gg";
	
	}
	
	@RequestMapping(value="/listPages" , method=RequestMethod.POST)
	public ModelMap listPages(
			@RequestBody Map<String, String> payload , @RequestParam(name = "start") String start, @RequestParam(name = "rows") String rows
		
			) {
	
	//	System.out.println((String)payload.get("idBrand"));
	//	System.out.println("Ratting world2 ---------->"+UUID.randomUUID().toString());
		ModelMap model=new ModelMap();
		System.out.println("ooooooo-----------oooooooooooooo>"+payload.get("Query"));
		
		System.out.println("ooooooo-----------oooooooooooooo>"+rows);
		System.out.println("ooooooo-----------oooooooooooooo>"+start);
		

		System.out.println("ooooooo-----------oooooooooooooo>"+payload.get("title"));
		
		Map<String, Object> payload2= new HashMap<String, Object>();
		
		payload2.put("id",Utility.getUniqueId());
		
		
		Map<String, String> searchCriteria=new HashMap<String, String>();
		System.out.println("--------Query---->"+"title:*"+payload.get("Query")+"*");
		System.out.println("--------rows---->"+rows);
		System.out.println("--------start---->"+start);
		
		searchCriteria.put("q", "title:*"+payload.get("Query")+"*");
		searchCriteria.put("rows", rows);
		searchCriteria.put("start", start);
		searchCriteria.put("facet", "true");
	
		QueryResponse dd = (QueryResponse)commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, sampleUrl);
	//	dd.getResults().getNumFound();
		//	dd.getResults();
	//	dd.getResponse();
	//	System.out.print("-4-->"+dd.getResults().getNumFound());
		
		
		System.out.println("------------->"+dd.getResults().toArray());

		System.out.println("-------dd.getResponse()------>"+dd.getResults().getNumFound());
		
//		QueryResponse apiResponse =commonDocumentService.addDocumentByTemplate(payload2, "http://192.168.1.104:8983/solr/Solr_sample");
		model.addAttribute("results",dd.getResults());
		model.addAttribute("qTime",dd.getQTime());
		
		model.addAttribute("totalRecord",dd.getResults().getNumFound());
		
		return model;
	
	}
	
	
	
	@RequestMapping(value="/listPagginationPages" , method=RequestMethod.POST)
	public ModelMap listPages(
			@RequestBody Map<String, String> payload , @RequestParam(name = "start") String start, @RequestParam(name = "rows") String rows,
			@RequestParam(value = "q", required = false) String query
			) {
	
	//	System.out.println((String)payload.get("idBrand"));
	//	System.out.println("Ratting world2 ---------->"+UUID.randomUUID().toString());
		ModelMap model=new ModelMap();
		System.out.println("ooooooo-----------oooooooooooooo>"+payload.get("Query"));
		
		System.out.println("ooooooo-----------oooooooooooooo>"+rows);
		System.out.println("ooooooo-----------oooooooooooooo>"+start);
		

		System.out.println("ooooooo-----------oooooooooooooo>"+payload.get("title"));
		
		Map<String, Object> payload2= new HashMap<String, Object>();
		
		payload2.put("id",Utility.getUniqueId());
		
		
		Map<String, String> searchCriteria=new HashMap<String, String>();
		searchCriteria.put("q", "title:*"+query+"*");
		searchCriteria.put("rows", rows);
		searchCriteria.put("start", start);
		searchCriteria.put("facet", "true");
	
		QueryResponse dd = (QueryResponse)commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, sampleUrl);
	//	dd.getResults().getNumFound();
		//	dd.getResults();
	//	dd.getResponse();
	//	System.out.print("-4-->"+dd.getResults().getNumFound());
		
		
		System.out.println("------------->"+dd.getResults().toArray());

		System.out.println("-------dd.getResponse()------>"+dd.getResults().getNumFound());
		
//		QueryResponse apiResponse =commonDocumentService.addDocumentByTemplate(payload2, "http://192.168.1.104:8983/solr/Solr_sample");
		model.addAttribute("results",dd.getResults());
		model.addAttribute("qTime",dd.getQTime());
		
		model.addAttribute("totalRecord",dd.getResults().getNumFound());
		
		return model;
	
	}
	
	
	
	@RequestMapping(value="/getDataByQuery" , method=RequestMethod.POST)
	public ModelMap getDataByQuery(
			@RequestBody Map<String, String> payload
			) {
	
	//	System.out.println((String)payload.get("idBrand"));
	//	System.out.println("Ratting world2 ---------->"+UUID.randomUUID().toString());
		ModelMap model=new ModelMap();
		System.out.println("ooooooo-----------oooooooooooooo>"+payload.get("query"));
			
		Map<String, String> searchCriteria=new HashMap<String, String>();
		searchCriteria.put("q", payload.get("query"));
		searchCriteria.put("rows", Integer.toString(800));
		searchCriteria.put("start", Integer.toString(0));
		searchCriteria.put("facet", "true");
	
		QueryResponse dd = (QueryResponse)commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, sampleUrl);
	//	dd.getResults().getNumFound();
		//	dd.getResults();
	//	dd.getResponse();
	//	System.out.print("-4-->"+dd.getResults().getNumFound());
		
		
		System.out.println("------------->"+dd.getResults().toArray());
		
//		QueryResponse apiResponse =commonDocumentService.addDocumentByTemplate(payload2, "http://192.168.1.104:8983/solr/Solr_sample");
		model.addAttribute("results",dd.getResults());
		
		return model;
	
	}
	
	
	
}