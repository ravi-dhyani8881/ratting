package com.spring.ratting.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
@RequestMapping("/portal")
public class UserPortalController {
	
	
	@RequestMapping(value="/conform-user" , method=RequestMethod.GET)
	public String  addContent() {
		
		
		return "conform-user";
	}
}