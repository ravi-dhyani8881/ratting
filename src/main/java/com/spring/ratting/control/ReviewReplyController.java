package com.spring.ratting.control;

import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.ratting.service.CommonDocumentService;
import com.spring.ratting.solr.SolrConnection;
import com.spring.ratting.util.ResponseMessage;
import com.spring.ratting.util.SolrUrls;
import com.spring.ratting.util.Utility;

@RestController
@ExposesResourceFor(ReviewReplyController.class)
@RequestMapping("reply")
public class ReviewReplyController {
	
	@Autowired
	SolrConnection solrRatting;
	
	@Autowired
	CommonDocumentService commonDocumentService;
	
	String url=SolrUrls.replyUrl;
		
	@RequestMapping(value="/add2" , method=RequestMethod.POST)
	public ModelMap addReplyOnReviews2(
			 @RequestParam(name = "contentId", required = true) String contentId,
			 @RequestParam(name = "reviewId", required = true) String reviewId,
			 @RequestParam(name = "userId", required = true) String userId,
			 @RequestParam(name = "replyHeading", required = true) String replyHeading,
			 @RequestParam(name = "replyBody", required = true) String replyBody
			) {
		ModelMap model=new ModelMap();
		SolrInputDocument document = new SolrInputDocument();
		String replyId=Utility.getUniqueId();
		document.addField("replyId", replyId);
		document.addField("contentId", contentId);
		document.addField("reviewId", reviewId);
		document.addField("userId", userId);
		document.addField("replyHeading", replyHeading);
		document.addField("replyBody", replyBody);
			
		solrRatting.addDocument(SolrUrls.replyUrl, document);		
		
		return model.addAttribute("Message", new ResponseMessage("Reply added Sucesfully", 201));
		
		}
	
	@RequestMapping(value="/add" , method=RequestMethod.POST)
	public ModelMap addReplyOnReviews(
			@RequestBody Map<String, Object> payload
			) {
		ModelMap model=new ModelMap();
		String replyId=commonDocumentService.addDocument(payload, url) ;
		return model.addAttribute("Message", new ResponseMessage("Reply added Sucesfully", 201,replyId));
		}
	
	
}