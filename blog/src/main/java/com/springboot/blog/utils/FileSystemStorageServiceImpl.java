package com.springboot.blog.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.blog.exception.FileNotFoundException;
import com.springboot.blog.exception.FileStorageException;
import com.springboot.blog.properties.FileUploadProperties;
import com.springboot.blog.service.FileSytemStorageService;

import jakarta.annotation.PostConstruct;

/**
 * File uploading service
 * @author Garvit
 *
 */
@Service
public class FileSystemStorageServiceImpl implements FileSytemStorageService {

	
	
	private final Path dirLocation;
	
	@Autowired
	private FileUploadProperties fileUploadProperties;
	
    public FileSystemStorageServiceImpl() {
        this.dirLocation = Paths.get(AppConstants.FILE_LOCATION)
        		                .toAbsolutePath()
        		                .normalize();
    }
    
    /**
     * Used to create directory
     */
	@Override
	@PostConstruct
	public void init() {
		// TODO Auto-generated method stub
		try {
			Files.createDirectories(this.dirLocation);
		} 
		catch (Exception ex) {
			throw new FileStorageException( HttpStatus.BAD_REQUEST ,"Could not create upload dir!");
		}

	}

	/**
	 * Used to save file 
	 */
	@Override
	public String saveFile(MultipartFile file) {
		
		try {

//			Create dynamic path 
			  String UPLOAD_DIR = new ClassPathResource("static/image/").getFile().getAbsolutePath();

			
//			String fileName = file.getOriginalFilename();
//			Path dfile = this.dirLocation.resolve(fileName);
//			Files.copy(file.getInputStream(), dfile,StandardCopyOption.REPLACE_EXISTING);
			Files.copy(file.getInputStream(), Paths.get( UPLOAD_DIR +File.separator+file.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING);
			
//			return fileName;
			return "";
			
		} catch (Exception e) {
			throw new FileStorageException(HttpStatus.BAD_REQUEST ,  "Could not upload file");
	    }

	}

	/**
	 * Used to load file
	 */
	@Override
	public Resource loadFile(String fileName) {
		// TODO Auto-generated method stub
		
		try {
			
	      Path file = this.dirLocation.resolve(fileName).normalize();
	      Resource resource = new UrlResource(file.toUri());

	      if (resource.exists() || resource.isReadable()) {
	    	  return resource;
	      } 
	      else {
	    	  throw new FileNotFoundException( HttpStatus.BAD_REQUEST , "Could not find file");
	      }
	    } 
		catch (MalformedURLException e) {
			throw new FileNotFoundException( HttpStatus.BAD_REQUEST , "Could not download file");
	    }
		
	}

	
	
	@Override
	public String getFilePath(String fileName) {
		// TODO Auto-generated method stub
		
		try {
			
			Path file = this.dirLocation.resolve(fileName).normalize();
			return file.toString() ;
		} 
		catch (Exception e) {
			throw new FileNotFoundException( HttpStatus.BAD_REQUEST , "Could not download file");
		}
		
	}
	
	
}