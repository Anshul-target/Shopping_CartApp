package com.ecom.frontend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
 public Boolean uploadFileS3(MultipartFile file,Integer bucketType);


}
