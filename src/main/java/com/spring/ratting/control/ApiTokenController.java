package com.spring.ratting.control;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spring.ratting.service.CommonDocumentService;
import com.spring.ratting.util.ResponseMessage;
import com.spring.ratting.util.SolrUrls;
import com.spring.ratting.util.Utility;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Api Key Mangment System" , description = "This service used to Activate and deactivate key .")
@RestController
@ExposesResourceFor(ContentController.class)
@RequestMapping("/apiKey")
public class ApiTokenController {
	
	
	@Autowired
	CommonDocumentService commonDocumentService;

	String url=SolrUrls.apiKeyUrl;
	String userUrl=SolrUrls.userUrl;
	
	@ApiOperation(value = "This service genearte Api key")
	@RequestMapping(value="/generateApiKey" , method=RequestMethod.POST)
	public ModelMap  generateApiKey(@RequestBody Map<String, Object> payload , HttpServletRequest request, HttpServletResponse response ) {
		
		ModelMap model=new ModelMap();
		Object apiResponse =null;
		String userId=(String)payload.get("userId");
		String apiKey=Utility.getUniqueId();
		
		Map<String, String> searchCriteria=new HashMap<String,String>(); 
		searchCriteria.put("q", "ID:"+userId+ " && "+ "userStatus:A");
		
		apiResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, userUrl);
		
		if(apiResponse instanceof RemoteSolrException )
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return model.addAttribute("Message", new ResponseMessage("Server down"+ ((RemoteSolrException) apiResponse).getMessage() , 500 ));	
		}else if(apiResponse instanceof Exception) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));	
		}
			
		else if(((QueryResponse) apiResponse).getResults().size() ==0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return model.addAttribute("Message", new ResponseMessage("Bad request user not exits", 404));
		}else {
			try {
				payload.remove("userId");
				payload.put("ID", userId);
				payload.put("apiKey", apiKey);
				payload.put("status", "A");
				searchCriteria.put("q", "ID:"+userId+ " && "+ "status:A");
				apiResponse = commonDocumentService.advanceSearchDocumentByTemplate(searchCriteria, url);
				
				if(apiResponse instanceof Exception )
				{
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return model.addAttribute("Message", new ResponseMessage("Server down Internal server error", 500));
				}
				
				if(((QueryResponse) apiResponse).getResults().size() >0) {
					payload.put("addedDate", ((QueryResponse) apiResponse).getResults().get(0).get("addedDate") );
					
					apiResponse = commonDocumentService.updateDocumentByTemplate(payload, url);
					
				}else {
					payload.put("addedDate", new java.util.Date());	
					apiResponse = commonDocumentService.addDocumentByTemplate(payload, url);	
				}				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			model.addAttribute("apiKey", apiKey);
			return model.addAttribute("Message", new ResponseMessage("Api key genrated sucesfully", 201));
		}	
	}
}