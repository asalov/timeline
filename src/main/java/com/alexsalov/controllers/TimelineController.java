package com.alexsalov.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import com.alexsalov.models.Entry;
import com.alexsalov.services.IEntryService;

@Controller
public class TimelineController extends BaseController{
	
	@Autowired
	private IEntryService entryService;
	
	@RequestMapping("/")
	public String index(Model model) {
		List<Entry> entries = this.entryService.getEntriesYear();
		
		model.addAttribute("entries", entries);
		
		return this.render(model, "index", "Welcome");
	}
}