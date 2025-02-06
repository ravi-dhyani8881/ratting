package com.spring.ratting.control;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.spring.ratting.solr.SolrConnection;
import com.spring.ratting.util.ResponseMessage;
import com.spring.ratting.util.SolrUrls;
import com.spring.ratting.util.Utility;
import com.spring.ratting.validation.ValidationService;

import io.swagger.annotations.ApiOperation;

import com.main.external.exception.user.CustomException4xx;
import com.main.external.exception.user.UserException;
import com.main.external.model.User;
import com.spring.ratting.domain.InnerAnalytic;
import com.spring.ratting.domain.InnerReviews;
import com.spring.ratting.service.CommonDocumentService;

@RestController
@ExposesResourceFor(UserController.class)
@RequestMapping("users")
public class UserController {

	@Autowired
	SolrConnection solrRatting;

	@Autowired
	CommonDocumentService commonDocumentService;
	
	
	
	@Autowired
	ValidationService validationService;
	
	String url=SolrUrls.userUrl;

	//New one created by Me on 30/05/2021
	
	
	// Need to check duplicate email adress in old implemetation in git
	
	@PostMapping("/userSignUp")
	public ModelMap addNew(@RequestBody Map<String, Object> payload, HttpServletResponse response) {
		ModelMap model = new ModelMap();
		String userId=Utility.getUniqueId();		
		
			payload.put("ID", userId);
			String userActivationKey=(String)payload.get("userActivationKey")!=null ? (String)payload.get("userActivationKey") : Utility.getUniqueId();
			payload.put("userActivationKey", userActivationKey);
			

			if (emailExists((String)payload.get("email"))) {
				//throw new Exception();
				Exception r = new RuntimeException("Email Exist");
				
			//	throw new CustomException4xx("There is an account with that email address: "  + payload.get("email"), r);
				
				throw new UserException("There is an account with that email address: "  + payload.get("email"), r);
	        }

			Object apiResponse = commonDocumentService.addDocumentByTemplate(payload, url);
			
			
			if(apiResponse instanceof Exception )
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
				return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500).build());
			}
			response.setStatus(HttpServletResponse.SC_CREATED);
		//	model.addAttribute("Message", new ResponseMessage("User added Sucesfully. Please activate through email activation code.", HttpServletResponse.SC_CREATED,null,null,userId,null,"created"));					
			
			
			 model.addAttribute("Message", new ResponseMessage.Builder("User added Sucesfully. Please activate through email activation code.", HttpServletResponse.SC_CREATED)
					.withID(userId)
					.withActivationCode(userActivationKey)
					.withResponseType("created")
					.build());
			
		return model;
	}
	
	//Activate user one created by Me on 30/05/2021
//		{
//		"ID": "516e1aa5-9510-46f7-8e0d-26bdebf30a17",
//		"userActivationKey": "1234"
//		}
//	
	@Deprecated
	@PostMapping("/activate-user")
	public ModelMap activateUser(@RequestBody Map<String, Object> payload, HttpServletResponse response) {
		/*
		 * userStatus=A means Activated user
		 * userStatus=I means In-Activated user
		 */

		ModelMap model = new ModelMap();
		String activationKey=(String)payload.get("userActivationKey");
		String userId=(String)payload.get("userId");
		
		Map<String, String> searchCriteria=new HashMap<String,String>(); 
	//	searchCriteria.put("q", "userActivationKey:"+activationKey+ " && "+ "ID:"+userId+ " && "+ "userStatus: I");
		
		searchCriteria.put("q", "ID:"+userId);
		
	//	QueryResponse res= commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, url);	
		
		QueryResponse apiResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, url);
		
		if (apiResponse.getResults().size() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		//	return model.addAttribute("Message", new ResponseMessage("Invalid User", 401));
			return model.addAttribute("Message", new ResponseMessage.Builder("Invalid User", 401).build());
		} 
		else if(apiResponse.getResults().get(0).get("userActivationKey").equals("Activated")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
		//	return	model.addAttribute("Message", new ResponseMessage("User already activate through email activation code.",  HttpServletResponse.SC_BAD_REQUEST,null,null,null,null,"Bad request"));					
		
			return model.addAttribute("Message", new ResponseMessage.Builder("User already activate through email activation code.", HttpServletResponse.SC_BAD_REQUEST)
					.withID(userId)
					.withResponseType("Bad request")
					.build());
			
			
		}
		else if(!apiResponse.getResults().get(0).get("userActivationKey").equals(activationKey)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		//	return	model.addAttribute("Message", new ResponseMessage("Invalid activation Key",  HttpServletResponse.SC_BAD_REQUEST,null,null,null,null,"Invalid request"));					
		
			return model.addAttribute("Message", new ResponseMessage.Builder("Invalid activation Key", HttpServletResponse.SC_BAD_REQUEST)
					.withID(userId)
					.withResponseType("Invalid request")
					.build());
			
		//	return model.addAttribute("Message", new ResponseMessage("Invalid activation Key", 401));
		}
		
			SolrDocument solrDocument = apiResponse.getResults().get(0);
			solrDocument.put("userStatus", "A");
			solrDocument.put("userActivationKey", "Activated");
			
			commonDocumentService.updateDocumentByTemplate(solrDocument, url) ;
		//	model.addAttribute("userId",userId);
			response.setStatus(HttpServletResponse.SC_OK);
		//	model.addAttribute("Message", new ResponseMessage("User activated Sucesfully.",  HttpServletResponse.SC_OK,null,null,null,null,"activated"));					
			
			
			 model.addAttribute("Message", new ResponseMessage.Builder("User activated Sucesfully.", HttpServletResponse.SC_OK)
					.withID(userId)
					.withResponseType("activated")
					.build());
			
			return model;
		} 
	
	@PostMapping("/activate-user-new")
	public ModelMap activateUserNew(@RequestBody Map<String, Object> payload, HttpServletResponse response) {
		/*
		 * userStatus=A means Activated user
		 * userStatus=I means In-Activated user
		 */

		ModelMap model = new ModelMap();
		String userActivationKeyDecode=null;
		String userEmail =null;
		String activationKey=(String)payload.get("userActivationKey");
		try {
		byte[] decodedBytes = Base64.getDecoder().decode(activationKey);
		//	String decodedString = new String(decodedBytes);
			
			String[] parts = new String(decodedBytes).split("\\+", 2);
			 userActivationKeyDecode = parts[0];
			 userEmail = parts[1];
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		//	return	model.addAttribute("Message", new ResponseMessage("Invalid activation code",  HttpServletResponse.SC_BAD_REQUEST,null,null,null,null,"Bad request"));
			
		return	model.addAttribute("Message", new ResponseMessage.Builder("Invalid activation code.", HttpServletResponse.SC_BAD_REQUEST)
																	.withResponseType("Bad request")
																	.build());
		}
		
		Map<String, String> searchCriteria=new HashMap<String,String>(); 
	//	searchCriteria.put("q", "userActivationKey:"+activationKey+ " && "+ "ID:"+userId+ " && "+ "userStatus: I");
		
		searchCriteria.put("q", "email:"+userEmail);
		
	//	QueryResponse res= commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, url);	
		
		QueryResponse apiResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, url);
		
		if (apiResponse.getResults().size() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		//	return model.addAttribute("Message", new ResponseMessage("Invalid User", 401));
			
			return	model.addAttribute("Message", new ResponseMessage.Builder("Invalid User.", HttpServletResponse.SC_UNAUTHORIZED).build());
			
		} 
		else if(apiResponse.getResults().get(0).get("userActivationKey").equals("Activated")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
		//	return	model.addAttribute("Message", new ResponseMessage("User already activate through email activation code.",  HttpServletResponse.SC_BAD_REQUEST,null,null,null,null,"Bad request"));	
			
			return	model.addAttribute("Message", new ResponseMessage.Builder("User already activate through email activation code.", HttpServletResponse.SC_BAD_REQUEST)
																	 .withResponseType("Bad request")
																	 .build());
			
		}
		else if(!apiResponse.getResults().get(0).get("userActivationKey").equals(userActivationKeyDecode)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		//	return	model.addAttribute("Message", new ResponseMessage("Invalid activation Key",  HttpServletResponse.SC_BAD_REQUEST,null,null,null,null,"Invalid request"));					
		
			return	model.addAttribute("Message", new ResponseMessage.Builder("Invalid activation Key", HttpServletResponse.SC_BAD_REQUEST)
					 .withResponseType("Invalid request")
					 .build());

			
		//	return model.addAttribute("Message", new ResponseMessage("Invalid activation Key", 401));
		}
		
			SolrDocument solrDocument = apiResponse.getResults().get(0);
			solrDocument.put("userStatus", "A");
			solrDocument.put("userActivationKey", "Activated");
			
			commonDocumentService.updateDocumentByTemplate(solrDocument, url) ;
		//	model.addAttribute("userId",userId);
			response.setStatus(HttpServletResponse.SC_OK);
		//	model.addAttribute("Message", new ResponseMessage("User activated Sucesfully.",  HttpServletResponse.SC_OK,null,null,null,null,"activated"));					
			
			 model.addAttribute("Message", new ResponseMessage.Builder("User activated Sucesfully.", HttpServletResponse.SC_OK)
															  .withResponseType("activated")
															  .build());
			return model;
		} 


	@RequestMapping(value = "/changeUserStatus", method = RequestMethod.GET)
	public ModelMap changeUserStatus(@RequestParam(name = "userId", required = true) String userId,
			@RequestParam(name = "userStatusFlag", required = true) String userStatusFlag) {
		/*
		 * userStatusFlag=A means Activate user, userStatusFlag=I means user is
		 * inactive or deactivated by admin
		 */

		ModelMap model = new ModelMap();

		QueryResponse queryResponse = solrRatting.serachDocument(SolrUrls.userUrl, "ID:" + userId);
		
		if (queryResponse.getResults().size() == 0) {
		//	return model.addAttribute("Message", new ResponseMessage("Invalid User id", 401));
			return model.addAttribute("Message", new ResponseMessage.Builder("Invalid User id", 401).build());
		}
		SolrDocument solrDoc = queryResponse.getResults().get(0);
		SolrInputDocument document = new SolrInputDocument();

		try {
			document.addField("ID", (String) solrDoc.get("userId"));
			document.addField("firstName", (String) solrDoc.get("firstName"));
			document.addField("lastName", (String) solrDoc.get("lastName"));
			document.addField("middleName", (String) solrDoc.get("middleName"));
			document.addField("emailAddress", (String) solrDoc.get("emailAddress"));
			document.addField("phoneNumber", (String) solrDoc.get("phoneNumber"));
			document.addField("gender", (String) solrDoc.get("gender"));
			document.addField("birthday", (Date) solrDoc.get("birthday"));
			document.addField("password", (String) solrDoc.get("password"));
			document.addField("userStatus", userStatusFlag);
			document.addField("userActivationKey", (String) solrDoc.get("userActivationKey"));

			solrRatting.updateDocument(SolrUrls.userUrl, document);
		//	return model.addAttribute("Message", new ResponseMessage("User status updated Sucesfully", 200));
			return model.addAttribute("Message", new ResponseMessage.Builder("User status updated Sucesfully", 200).build());
		} catch (Exception e) {
			return model.addAttribute("Message", e.getMessage());
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ModelMap updateUser(@RequestParam(name = "userId", required = true) String userId,
			@RequestParam(name = "firstName", required = true) String firstName,
			@RequestParam(name = "lastName", required = true) String lastName,
			@RequestParam(name = "middleName", required = true) String middleName,
			@RequestParam(name = "emailAddress", required = true) String emailAddress,
			@RequestParam(name = "phoneNumber", required = true) String phoneNumber,
			@RequestParam(name = "gender", required = true) String gender,
			@RequestParam(name = "birthday", required = true) String birthday,
			@RequestParam(name = "password", required = true) String password) {

		ModelMap model = new ModelMap();
		try {
			QueryResponse queryResponse = solrRatting.serachDocument(SolrUrls.userUrl, "ID:" + userId);
			
			if (queryResponse.getResults().size() == 0) {
			//	return model.addAttribute("Message", new ResponseMessage("Invalid User id", 401));
				return model.addAttribute("Message", new ResponseMessage.Builder("Invalid User id", 401).build());
				
			}
			SolrDocument solrDoc = queryResponse.getResults().get(0);
			SolrInputDocument document = new SolrInputDocument();
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			
			document.addField("userId", userId);
			document.addField("firstName", firstName);
			document.addField("lastName", lastName);
			document.addField("middleName", middleName);
			document.addField("emailAddress", emailAddress);
			document.addField("phoneNumber", phoneNumber);
			document.addField("gender", gender);
			document.addField("birthday", dateFormat.parse(birthday));
			document.addField("password", password);
			document.addField("userStatus", (String) solrDoc.get("userStatus"));
			document.addField("userActivationKey", (String) solrDoc.get("userActivationKey"));
			solrRatting.updateDocument(SolrUrls.userUrl, document);

		//	return model.addAttribute("Message", new ResponseMessage("User updated Sucesfully.", 200));
		return	 model.addAttribute("Message", new ResponseMessage.Builder("User updated Sucesfully.", 200)
					  											  .build());
		} catch (Exception e) {
			return model.addAttribute("Message", e.getMessage());
		}
	}
// to be deleted before committing to SVN
	String protocolUrl = SolrUrls.protocolUrl;

	@RequestMapping(value = "/addProtocol", method = RequestMethod.POST)
	public ModelMap addProtocol(@RequestBody Map<String, Object> payload) {
		// Example payload in body
//		{
//		"reviewComments":"Hello",
//		"reviewRatting":"1",
//		"reviewUserId":"1",
//		"reviewContentId":"1"
//		}
//	
		ModelMap model = new ModelMap();
		//payload.containsKey("reviewUserId");
		System.out.println("----->" + payload.containsKey("methodName"));
		String query = "methodName:" + (String) payload.get("methodName");
		if (commonDocumentService.SearchByQuery(query, protocolUrl).getResults().getNumFound() > 0) {
			System.out.println("Already exist");
		//	return model.addAttribute("Message", new ResponseMessage("Method name already added", 403));
			return model.addAttribute("Message", new ResponseMessage.Builder("Method name already added", 403).build());	
			
		} else {
			String methodId = commonDocumentService.addDocument(payload, protocolUrl);
		//	return model.addAttribute("Message", new ResponseMessage("Method added Sucesfully", 201));
			return model.addAttribute("Message", new ResponseMessage.Builder("Method added Sucesfully", 201).build());		
		}
	}
	
	@PostMapping("/user-authentication")
	public ModelMap userAuth( @RequestBody @Valid User user, HttpServletResponse response, @RequestHeader Map<String, String> headers) {
		// Example payload in body
//		{
//		"reviewComments":"Hello",
//		"reviewRatting":"1",
//		"reviewUserId":"1",
//		"reviewContentId":"1"
//		}
//	
		
		headers.forEach((key, value) -> {
		//	System.out.println("----------->"+key+"----------->"+value);
	       
	    });
		
		
		ModelMap model = new ModelMap();
		//payload.containsKey("reviewUserId");
		String userId=null;
		
		Map<String, String> searchCriteria=new HashMap<String,String>(); 
		searchCriteria.put("q", "((email:"+user.getUsername()+") && ( email:"+user.getUsername()+" && password:"+user.getPassword()+"))");
		searchCriteria.put("fl", "userStatus,ID");
		
		
		
		QueryResponse apiResponse =commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, url);
		
		((QueryResponse) apiResponse).getResults().forEach((C)-> {
		
			if(C.get("userStatus").equals("I")){		
			//	model.addAttribute("Message", new ResponseMessage("User is inactive. Please activate user account.", 403,null,null,null,null,"INACTIVE"));
				model.addAttribute("Message", new ResponseMessage.Builder("User is inactive. Please activate user account.", 403)
																 .withResponseType("INACTIVE")										
																 .build());	
														
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				
		}else {
			response.setStatus(HttpServletResponse.SC_OK);
		//	model.addAttribute("Message", new ResponseMessage("User authenticated sucesfully.", HttpServletResponse.SC_OK,null,null,null,null,"AUTHENTICATED"));					
			model.addAttribute("Message", new ResponseMessage.Builder("User authenticated sucesfully.", HttpServletResponse.SC_OK)
															 .withID(C.get("ID").toString())
															 .withResponseType("AUTHENTICATED")										
															 .build());		
		}
			}
		
				);
		if(apiResponse.getResults().size()==0) {
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	//	model.addAttribute("Message", new ResponseMessage("User / password incorrect", HttpServletResponse.SC_UNAUTHORIZED,null,null,null,null,"UNAUTHORIZED"));
	
		model.addAttribute("Message", new ResponseMessage.Builder("User / password incorrect", HttpServletResponse.SC_UNAUTHORIZED)
				 .withResponseType("UNAUTHORIZED")										
				 .build());	
		}
		return model;	
	}
	
	
	
	@ApiOperation(value = "This service used to search user by query")
	@RequestMapping(value="/searchUserByQuery" , method=RequestMethod.GET)
	public ModelMap  userSearchByQuery(@RequestParam(name = "query", required = true) String query,
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
		Object apiResponse=null;
		Object apiDetails=null;
		String reviewDetailsQuery=null;
		
		if(validationService.validateApiKey(apiKey, userId) == 500 )
		{	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
			
			return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500)										
					 												.build());	
			 
		}else if(validationService.validateApiKey(apiKey, userId) == 401) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			// return model.addAttribute("Message", new ResponseMessage("Invalid Api Key", 401));
			 return model.addAttribute("Message", new ResponseMessage.Builder("Invalid Api Key", 401)										
						.build());
		}else {
		//	query=Utility.getQuery(query, userId);
			Map<String, String> searchCriteria=new HashMap<String,String>(); 
			searchCriteria.put("q", query);
			searchCriteria.put("rows", rows);
			searchCriteria.put("start", start);
			searchCriteria.put("fl", fl);
			searchCriteria.put("fq", fq);
			searchCriteria.put("sort", sort);
			
			 apiResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, url);
			 if(apiResponse instanceof Exception )
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//	return model.addAttribute("Message", new ResponseMessage("Server down Internal server error",500));
				
				return model.addAttribute("Message", new ResponseMessage.Builder("Server down Internal server error", 500)										
						.build());
			}else {
				
				((QueryResponse) apiResponse).getResults().forEach((K)-> {
				//	System.out.println(K.get("ID"));							
				});
				
 //				 To check number of review in Content
//				 apiDetailsQuery="reviewContentId:"+((QueryResponse) apiResponse).getResults().get;
//				 apiDetails=commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, url);
				
				return model.addAttribute("result",((QueryResponse) apiResponse).getResults());
			}
		}
	}
	
	
	

	private boolean emailExists(String email) {
		Map<String, String> searchCriteria=new HashMap<String,String>(); 
		searchCriteria.put("q", "email:"+email);
		searchCriteria.put("rows", "1");
		searchCriteria.put("start", "0");
       return ((QueryResponse)	commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, url)).getResults().size() > 0 ? true :false  ;
	
	}
	

	
}