package com.alexsalov.util;

import com.google.gson.annotations.Expose;

public class JsonResponse {
	@Expose
	private String error = null;
	
	public JsonResponse(){}
	
	public JsonResponse(String err){
		this.error = err;
	}
	
	public String getError(){
		return this.error;
	}
	
	public void setError(String val){
		this.error = val;
	}
	
	public boolean isSuccess(){
		return this.error == null;
	}
}