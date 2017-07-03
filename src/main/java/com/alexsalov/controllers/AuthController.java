package com.alexsalov.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alexsalov.models.Entry;
import com.alexsalov.models.EntryStat;
import com.alexsalov.models.Fact;
import com.alexsalov.models.Picture;
import com.alexsalov.models.Stat;
import com.alexsalov.models.User;
import com.alexsalov.services.IEntryService;
import com.alexsalov.services.IStatService;
import com.alexsalov.services.IUserService;
import com.alexsalov.util.ImageUpload;

@Controller
@RequestMapping(value="/admin")
public class AuthController extends BaseController{

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEntryService entryService;
	
	@Autowired
	private IStatService statService;
	
	private Entry currentEntry = null;
	
	@Value("${upload.directory}")
	private String uploadDir;
	
	@Value("${spring.http.multipart.max-file-size}")
	private String maxFileSize;
	
	@RequestMapping(method=RequestMethod.GET)
	public String adminHome(Model model, HttpSession session) {
		if(!this.isLoggedIn(session)) return this.redirToLogin();
		
		this.showFlashMsg(session, model, "confirmation");
		
		List<Entry> entries = this.entryService.getAllEntries();
		
		model.addAttribute("entries", entries);
		
		return this.render(model, "admin/home", "Admin");
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String showLogin(Model model) {		
		model.addAttribute("user", new User());
		
		return this.renderLogin(model);
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@ModelAttribute User user, HttpSession session, Model model) {
		if(user.getUsername() != "" && user.getPassword() != ""){
			if(this.userService.login(user, session)){
				return this.redir("admin");
			}else{
				model.addAttribute("error", "Incorrect username or password.");
				
				return this.renderLogin(model);
			}			
		}else{
			model.addAttribute("error", "Please provide a username and a password.");
			
			return this.renderLogin(model);
		}
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.POST)
	public String logout(HttpSession session) {
		this.userService.logout(session);
		
		return this.redirToLogin();
	}
	
	private boolean isLoggedIn(HttpSession session){
		return this.userService.isLoggedIn(session);
	}
	
	private String renderLogin(Model model){
		return this.render(model, "admin/login", "Login");
	}
	
	private String renderAdd(Model model){
		model.addAttribute("formAction", "add");
		
		return this.renderForm(model, "Add New Entry");
	}
	
	private String renderEdit(Model model){
		model.addAttribute("formAction", "edit");
		
		return this.renderForm(model, "Edit Entry");
	}
	
	private String renderForm(Model model, String title){
		List<Stat> stats = this.statService.getAllStats();
		
		model.addAttribute("stats", stats);
		
		return this.render(model, "admin/entry", title);
	}
	
	private String redirToLogin(){
		return this.redir("admin/login");
	}
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addEntry(Model model, HttpSession session) {
		if(!this.isLoggedIn(session)) return this.redirToLogin();
		
		model.addAttribute("entry", new Entry());
		
		return this.renderAdd(model);
	}
	
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String addNewEntry(@ModelAttribute Entry entry, @RequestParam(value="photos", required=false) MultipartFile[] uploadPics,
			@RequestParam(value="statValues", required=false) String[] statValues, Model model, HttpSession session){
		if(!this.isLoggedIn(session)) return this.redirToLogin();
		
		boolean isValid = true;
		String errorMsg = "";
		
		// Validation
		if(!this.entryService.isUnique(entry.getYear())){
			isValid = false;
			
			errorMsg = "An entry with the chosen year already exists.";
		}else if(entry.getSummary() == null){
			isValid = false;
			
			errorMsg = "Please provide a summary.";
		}
		
		if(isValid){
			entry.setAuthor(this.userService.getLoggedInUser(session));
			
			if(uploadPics.length > 0) this.uploadImages(uploadPics, entry);
			
			if(entry.getFacts().size() > 0){
				for(Fact fact : entry.getFacts()){
					fact.setEntry(entry);
				}
			}
	
			if(statValues != null && statValues.length > 0){
				List<Stat> allStats = this.statService.getAllStats();
				Map<Long, String> chosenStats = new HashMap<Long, String>();
				
				for(String str : statValues){
					String[] kvp = str.split("~");
					
					if(kvp.length > 1) chosenStats.put(Long.parseLong(kvp[0]), kvp[1]);
				}
				
				for(Stat stat : allStats){
					if(chosenStats.containsKey(stat.getId())){
						EntryStat entryStat = new EntryStat(entry, stat, chosenStats.get(stat.getId()));
						entryStat.setEntry(entry);
						entry.getStats().add(entryStat);
					}
				}
			}
			
			this.entryService.addEntry(entry);
			
			this.setFlashMsg(session, "confirmation", "The entry has been added.");
			
			return this.redir("admin");			
		}else{
			model.addAttribute("error", errorMsg);
			
			return this.renderAdd(model);
		}
	}
	
	@RequestMapping(value="/view/{id}", method=RequestMethod.GET)
	public String showEntry(@PathVariable("id") long id, Model model, HttpSession session){
		if(!this.isLoggedIn(session)) return this.redirToLogin();
		
		Entry entry = this.entryService.getEntry(id);
		
		if(entry == null){
			model.addAttribute("error", "The entry does not exist or has been removed.");
			
			return this.render(model, "admin/view", "Entry Not Found");
		}
		
		model.addAttribute("entry", entry);
		
		return this.render(model, "admin/view", entry.getYear() + " World Cup");
	}
	
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String editEntry(@PathVariable("id") long id, Model model, HttpSession session) {
		if(!this.isLoggedIn(session)) return this.redirToLogin();
		
		this.showFlashMsg(session, model, "confirmation");
		
		Entry entry = this.entryService.getEntry(id);
		
		if(entry == null){
			model.addAttribute("error", "The entry does not exist or has been removed.");
		}else{
			this.currentEntry = entry;
			
			model.addAttribute("entry", entry);			
		}
		
		return this.renderEdit(model);
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String updateEntry(@ModelAttribute Entry entry, @RequestParam(value="photos", required=false) MultipartFile[] uploadPics,
			@RequestParam(value="statValues", required=false) String[] statValues, Model model, HttpSession session) {
		if(!this.isLoggedIn(session)) return this.redirToLogin();
		
		boolean isValid = true;
		String errorMsg = "";
		
		// Validation
		if(this.currentEntry.getYear() != entry.getYear() && !this.entryService.isUnique(entry.getYear())){
			isValid = false;
			
			errorMsg = "An entry with the chosen year already exists.";
		}else if(entry.getSummary() == null){
			isValid = false;
			
			errorMsg = "Please provide a summary.";
		}
		
		if(isValid){
			entry.setId(this.currentEntry.getId());
			entry.setAuthor(this.currentEntry.getAuthor());
					
			if(uploadPics.length > 0) this.uploadImages(uploadPics, entry);
			
			if(entry.getFacts().size() > 0){
				for(Fact fact : entry.getFacts()){
					fact.setEntry(entry);
				}
			}
			
			if(statValues != null && statValues.length > 0){
				List<Stat> allStats = this.statService.getAllStats();
				Map<Long, String> chosenStats = new HashMap<Long, String>();
				
				for(String str : statValues){
					String[] kvp = str.split("~");
					
					if(kvp.length > 1) chosenStats.put(Long.parseLong(kvp[0]), kvp[1]);
				}
				
				for(Stat stat : allStats){
					if(chosenStats.containsKey(stat.getId())){
						EntryStat entryStat = new EntryStat(entry, stat, chosenStats.get(stat.getId()));
						entryStat.setEntry(entry);
						entry.getStats().add(entryStat);
					}
				}
			}
			
			this.entryService.updateEntry(entry);
			
			this.setFlashMsg(session, "confirmation", "The entry has been updated.");
			
			return this.redir("admin/edit/" + entry.getId());
		}else{
			model.addAttribute("error", errorMsg);
			
			return this.renderEdit(model);
		}
	}
	
	// Get the maximum allowed file size in bytes
	private long maxUploadSize(String value){
		return Long.parseLong(value.substring(0, 1)) * 1048576;
	}
	
	private void uploadImages(MultipartFile[] files, Entry entry){
		ImageUpload uploader = new ImageUpload(this.uploadDir, this.maxUploadSize(this.maxFileSize));
		uploader.upload(files);

		List<String> uploaded = uploader.getUploadedUrls();
		
		for(String path : uploaded){
			Picture pic = new Picture(path);
			pic.setEntry(entry);
			
			entry.getPictures().add(pic);
		}
	}
}