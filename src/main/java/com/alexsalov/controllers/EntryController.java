package com.alexsalov.controllers;

import java.io.File;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alexsalov.models.Entry;
import com.alexsalov.models.EntryStat;
import com.alexsalov.models.Fact;
import com.alexsalov.models.Picture;
import com.alexsalov.services.IEntryService;
import com.alexsalov.services.IUserService;
import com.alexsalov.util.JsonResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RestController
@RequestMapping(value="/entries")
public class EntryController extends BaseController{
	private Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEntryService entryService;
	
	@Value("${upload.directory}")
	private String uploadDir;
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String getEntry(@PathVariable("id") long id){
		Entry entry = this.entryService.getEntry(id);
		
		if(entry == null){
			JsonResponse res = new JsonResponse("Entry does not exist.");

			return gson.toJson(res);
		}
		
		return gson.toJson(entry);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE, produces="application/json")
	@ResponseBody
	public String deleteEntry(@PathVariable("id") long id, HttpSession session){
		JsonResponse res = new JsonResponse();
		
		if(!this.isLoggedIn(session)){
			res.setError("Permission denied.");
			
			return gson.toJson(res);
		}
		
		this.entryService.deleteEntry(id);
		
		this.setFlashMsg(session, "confirmation", "The entry has been deleted.");
		
		return gson.toJson(res);
	}
	
	@RequestMapping(value="/{entryId}/pictures/{picId}", method=RequestMethod.DELETE, produces="application/json")
	@ResponseBody
	public String removePicture(@PathVariable("entryId") long entryId, @PathVariable("picId") long picId, HttpSession session){
		JsonResponse res = new JsonResponse();

		if(!this.isLoggedIn(session)){
			res.setError("Permission denied.");
			
			return gson.toJson(res);
		}
		
		Entry entry = this.entryService.getEntry(entryId);
		
		for(Iterator<Picture> iterator = entry.getPictures().iterator(); iterator.hasNext();){
			Picture pic = iterator.next();
			
			if(pic.getId() == picId){
				iterator.remove();
				
				String fileName = pic.getUrl().substring(8);
				
				File f = new File(this.uploadDir + fileName);
				f.delete();
			}
		}
		
		this.entryService.updateEntry(entry);
		
		return gson.toJson(res);
	}
	
	@RequestMapping(value="/{entryId}/facts/{factId}", method=RequestMethod.DELETE, produces="application/json")
	@ResponseBody
	public String removeFact(@PathVariable("entryId") long entryId, @PathVariable("factId") long factId, HttpSession session){
		JsonResponse res = new JsonResponse();
		
		if(!this.isLoggedIn(session)){
			res.setError("Permission denied.");
			
			return gson.toJson(res);
		}

		Entry entry = this.entryService.getEntry(entryId);
		
		for(Iterator<Fact> iterator = entry.getFacts().iterator(); iterator.hasNext();){
			Fact fact = iterator.next();
			
			if(fact.getId() == factId) iterator.remove();
		}
		
		this.entryService.updateEntry(entry);
		
		return gson.toJson(res);
	}
	
	@RequestMapping(value="/{entryId}/stats/{statId}", method=RequestMethod.DELETE, produces="application/json")
	@ResponseBody
	public String removeStat(@PathVariable("entryId") long entryId, @PathVariable("statId") long statId, HttpSession session){
		JsonResponse res = new JsonResponse();
		
		if(!this.isLoggedIn(session)){
			res.setError("Permission denied.");
			
			return gson.toJson(res);
		}

		Entry entry = this.entryService.getEntry(entryId);
		
		for(Iterator<EntryStat> iterator = entry.getStats().iterator(); iterator.hasNext();){
			EntryStat stat = iterator.next();
			
			if(stat.getId() == statId) iterator.remove();
		}
		
		this.entryService.updateEntry(entry);
		
		return gson.toJson(res);
	}
	
	private boolean isLoggedIn(HttpSession session){
		return this.userService.isLoggedIn(session);
	}
}