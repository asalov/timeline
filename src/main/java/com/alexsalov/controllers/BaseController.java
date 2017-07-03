package com.alexsalov.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public abstract class BaseController {
	private final String PATH = "YOUR_APPLICATION_PATH";
	
	protected String render(Model model, String template, String title){
		model.addAttribute("template", template);
		model.addAttribute("title", title);
		
		return "master";
	}
	
	protected String redir(String destination){
		return "redirect:" + this.PATH + destination;
	}

	protected void setFlashMsg(HttpSession s, String name, String msg){
		s.setAttribute(name, msg);
	}
	
	protected void showFlashMsg(HttpSession s, Model model, String name){
		if(s.getAttribute(name) != null){
			model.addAttribute("msg", s.getAttribute(name));
			
			s.removeAttribute(name);
		}
	}	
}