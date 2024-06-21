package com.spring.ratting.exception;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.main.external.response.ErrorResponse;
import com.main.external.exception.user.CustomException4xx;
import com.main.external.exception.user.CustomException5xx;
import com.main.external.exception.user.UserException;

@ControllerAdvice
public class UserExceptionController extends ResponseEntityExceptionHandler{
	
@ExceptionHandler(value= UserException.class)
@ResponseStatus(HttpStatus.CONFLICT)
public @ResponseBody <T> T  handleException(UserException ex)
{
	ModelMap model = new ModelMap();
	model.addAttribute("Message",new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
	
	
return (T) model ;
}

@ExceptionHandler(Exception.class)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public @ResponseBody <T> T globalExceptionHandler(Exception ex, HttpServletRequest request, HttpServletResponse response) {
	
	ErrorResponse message = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	ModelMap model = new ModelMap();
	
	 if(ex instanceof java.net.ConnectException ) {
		 System.out.println("-------------->"+"ConnectException");
	 }else if (ex instanceof org.apache.solr.client.solrj.SolrServerException) {
		 System.out.println("-------------->"+"SolrServerException");
	}else if (ex instanceof java.lang.ClassCastException) {
		message.setMessage("Internal server Error");
		message.setRootcause("Solr Server Down");
	}else if (ex instanceof CustomException4xx) {
		
		model.addAttribute("Message",new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
		
	}else if (ex instanceof CustomException5xx) {
	
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		model.addAttribute("Message",new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
		
	}
	 
	 else {
		 model.addAttribute("Message",new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
	}
	
	 
  return (T) model; 
}


public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatus status, WebRequest request) {

		ModelMap model = new ModelMap();
	
		Map<String, Object> body = new LinkedHashMap<>();
		
		body.put("statusCode", HttpServletResponse.SC_BAD_REQUEST);
		
		//Get all errors
		List<String> errors = ex.getBindingResult()
		.getFieldErrors()
		.stream()
		.map(x -> x.getDefaultMessage())
		.collect(Collectors.toList());
		
		body.put("responseMessage", "User / password incorrect");
		body.put("responseType", "Bad request");
		body.put("errors", errors);
		
		model.addAttribute("Message", body);					
		
		
		return new ResponseEntity<>(model, headers, status);
		//return null;
}




}