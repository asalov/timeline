package com.alexsalov.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class ImageUpload {
	private String uploadDir;
	private long maxFileSize;
	
	private List<String> allowedFileTypes = new ArrayList<String>(
			Arrays.asList("image/jpeg", "image/png")
	);
	
	private List<String> uploadedFilesUrl = new ArrayList<String>();
	
	public ImageUpload(String uploadDir, long maxSize){
		this.uploadDir = uploadDir;
		this.maxFileSize = maxSize;
	}
	
	public String getUploadDir(){
		return this.uploadDir;
	}
	
	public List<String> getUploadedUrls(){
		return this.uploadedFilesUrl;
	}
	
	public void uploadFile(MultipartFile file){
		boolean isAllowedType = this.checkFileType(file);
		boolean isAllowedSize = this.checkSize(file);
		
		if(isAllowedType && isAllowedSize){
			File uploadDest = new File(this.uploadDir + file.getOriginalFilename());
			
			this.uploadedFilesUrl.add(this.getRelativeUploadPath(uploadDest.getAbsolutePath()));
			
			try{
				file.transferTo(uploadDest);
			}catch(IOException e){
				e.printStackTrace();
			}
		}else{
			System.out.println(file.getOriginalFilename() + " is not an allowed file type or exceeds the maximum file size.");
		}		
	}
	
	public void upload(MultipartFile[] files){
		for(MultipartFile f : files){
			this.uploadFile(f);
		}
	}
	
	private boolean checkFileType(MultipartFile f){
		return allowedFileTypes.contains(f.getContentType());
	}
	
	private boolean checkSize(MultipartFile f){
		return f.getSize() <= this.maxFileSize;
	}
	
	private String getRelativeUploadPath(String fullPath){
		int pos = fullPath.indexOf("uploads");
		
		return fullPath.substring(pos).replace('\\', '/');
	}
}