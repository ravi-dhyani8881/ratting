package com.spring.ratting.control;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrResponseBase;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.ratting.solr.SolrConnection;
import com.spring.ratting.util.ResponseMessage;
import com.spring.ratting.util.SolrUrls;
import com.spring.ratting.util.Utility;
//import com.restagent.beans.EmailParameters;
//import com.restagent.controller.Communication;
//import com.restagent.controller.CommunicationImpl;
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
	JavaMailSender javaMailSender;
	
	String url=SolrUrls.userUrl;

	//New one created by Me on 30/05/2021
	
	
	// Need to check duplicate email adress in old implemetation in git
	
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public ModelMap addNew(@RequestBody Map<String, Object> payload, HttpServletResponse response) {
		ModelMap model = new ModelMap();
		String userId=Utility.getUniqueId();		
		try {
			payload.put("ID", userId);
			payload.put("userActivationKey", Utility.getUniqueId());
			
			Object apiResponse = commonDocumentService.addDocumentByTemplate(payload, url);
			
			if(apiResponse instanceof Exception )
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return model.addAttribute("Message", new ResponseMessage("Server down", "Internal server error"));
			}
			model.addAttribute("Message", new ResponseMessage("User added Sucesfully. Please activate through email activation code.", "Added"));					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
	
	//Activate user one created by Me on 30/05/2021
//		{
//		"ID": "516e1aa5-9510-46f7-8e0d-26bdebf30a17",
//		"userActivationKey": "1234"
//		}
//	
	@RequestMapping(value = "/confirmUser", method = RequestMethod.POST)
	public ModelMap verifyUser(@RequestBody Map<String, Object> payload, HttpServletResponse response) {
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
			return model.addAttribute("Message", new ResponseMessage("Invalid User", "Error"));
		} 
		else if(apiResponse.getResults().get(0).get("userActivationKey").equals("Activated")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return model.addAttribute("Message", new ResponseMessage("User already conformed", "Error"));
		}
		else if(!apiResponse.getResults().get(0).get("userActivationKey").equals(activationKey)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return model.addAttribute("Message", new ResponseMessage("Invalid activation Key", "Error"));
		}
		
			SolrDocument solrDocument = apiResponse.getResults().get(0);
			solrDocument.put("userStatus", "A");
			solrDocument.put("userActivationKey", "Activated");
			
			commonDocumentService.updateDocumentByTemplate(solrDocument, url) ;
			model.addAttribute("userId",userId);
					
			return model.addAttribute("Message", new ResponseMessage("User conformation Sucessful", "conformed"));
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
			return model.addAttribute("Message", new ResponseMessage("Invalid User id", "Error"));
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
			return model.addAttribute("Message", new ResponseMessage("User status updated Sucesfully", "Status Changed"));
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
				return model.addAttribute("Message", new ResponseMessage("Invalid User id", "Error"));
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

			return model.addAttribute("Message", new ResponseMessage(
					"User updated Sucesfully.", "UPDATE"));
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
			return model.addAttribute("Message", new ResponseMessage("Method name already added", "AlreadyExist"));
		} else {
			String methodId = commonDocumentService.addDocument(payload, protocolUrl);
			return model.addAttribute("Message", new ResponseMessage("Method added Sucesfully", "Added", methodId));
		}
	}
	
}