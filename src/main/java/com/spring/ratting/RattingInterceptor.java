package com.spring.ratting;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.SolrPing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.restagent.beans.RequestDetails;
//import com.restagent.controller.Communication;
//import com.restagent.controller.CommunicationImpl;
//import com.restagent.util.SolrUrls;
import com.spring.ratting.util.ResponseMessage;
import com.spring.ratting.validation.ValidationService;

@Component
public class RattingInterceptor implements HandlerInterceptor {
	
	@Autowired
	ValidationService validationService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
//		
//			boolean auth=false;
//				
//		//System.out.println("-------------------------> " + request.getParameter("replyBody"));
//			ModelMap model = new ModelMap();
//			HttpSession session=request.getSession();
//			
//			String apiKey=request.getHeader("X-API-Key");
//			String userId=request.getHeader("X-USER-ID");
//			
//			session.setAttribute("auth", validationService.validateApiKey(apiKey, userId));
			
		//	response.setStatus(500);
		//	model.addAttribute("Message", new ResponseMessage("Reffffffply added Sucesfully", "Added"));
		//	response.getWriter().write(new ObjectMapper().writeValueAsString(model));
		//	request.setAttribute("error", model);
			return true;
		//		if (request.getParameter("replyBody") != null && request.getParameter("replyBody").equals("ravi")) {
//			ModelMap model = new ModelMap();
//			model.addAttribute("Message", new ResponseMessage("Reffffffply added Sucesfully", "Added"));
//			request.setAttribute("error", model);
//			return true;
//		} else {
//			ModelMap model = new ModelMap();
//			model.addAttribute("Message", new ResponseMessage("Rgggggggggeply added Sucesfully", "Added"));
//			return true;
//		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		try {
			String controllerName = "";
			String methodName = "";

			if( handler instanceof HandlerMethod ) {
				// there are cases where this handler isn't an instance of HandlerMethod, so the cast fails.
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				//controllerName = handlerMethod.getBean().getClass().getSimpleName().replace("Controller", "");
				controllerName  = handlerMethod.getBeanType().getSimpleName().replace("Controller", "");
				methodName = handlerMethod.getMethod().getName();
			//	System.out.println("Control Name: " + controllerName + "  ---- Method Name: " + methodName );

				ResponseErrorHandler responseHandler = new ResponseErrorHandler() {
					
					public boolean hasError(ClientHttpResponse response) throws IOException {
						if (response.getStatusCode() != HttpStatus.OK) {
							System.out.println("Error: " + response.getStatusText() + response.getRawStatusCode());
						}
						return response.getStatusCode() == HttpStatus.OK ? false : true;
					}
					
					public void handleError(ClientHttpResponse response) throws IOException {
						// TODO Auto-generated method stub
						System.out.println("Errorrr: " + response.getStatusText() + response.getRawStatusCode());
					}
				}; 
				
			//	RequestDetails requestDetails = new RequestDetails(SolrUrls.protocolUrl , methodName, HttpMethod.POST, MediaType.APPLICATION_JSON);
				String data = "{\"text\":\"Hello, a new action has been performed on " + new Date() + "\"}";
			//	Communication<String, String> comm = new CommunicationImpl<String, String>() ;
			//	String responseBody =comm.processRequest(requestDetails, data, responseHandler, String.class);
				//System.out.println("----->>>> Response: " + responseBody);

			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Post Handle method is Calling");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) throws Exception {

		System.out.println("Request and Response is completed " + response.toString());
	}
}