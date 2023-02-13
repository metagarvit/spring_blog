package com.springboot.blog.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileSytemStorageService {
	
	void init();
	String saveFile(MultipartFile file);
	Resource loadFile(String fileName);
	public String getFilePath(String fileName) ;
}
