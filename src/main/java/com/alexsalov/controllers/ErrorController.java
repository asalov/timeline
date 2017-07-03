package com.alexsalov.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController extends BaseController implements org.springframework.boot.autoconfigure.web.ErrorController{
	private final String ERROR_PATH = "/error";
	
	@RequestMapping(value=ERROR_PATH)
	public String showError(HttpServletResponse response, Model model) {
		String errorMsg = "";
		
		errorMsg = (response.getStatus() == 404) 
				? "The page you are looking for does not exist." 
				: "Something went wrong. Please, try again.";
		
		model.addAttribute("errorMsg", errorMsg);
		
		return this.render(model, "error", "Error");
	}
	
	@Override
	public String getErrorPath() {
		return this.ERROR_PATH;
	}
}