package com.spring.ratting.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.ratting.domain.PostContent;




	@Controller
	@RequestMapping({ "/richText", "/index" })
	public class RichText {

	    @GetMapping
	    public String main(Model model) {
	        model.addAttribute("post", new PostContent());
	        return "index";
	    }

	    @PostMapping
	    public String save(PostContent post, Model model) {
	        model.addAttribute("post", post);
	        return "saved";
	    }
	    
	    
	   
	    
	}