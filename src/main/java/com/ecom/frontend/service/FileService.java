package com.ecom.frontend.service;

import com.ecom.frontend.util.CloudinaryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
// public Boolean uploadFileS3(MultipartFile file,Integer bucketType);
 public CloudinaryResponse uploadFile(MultipartFile file, Integer bucketType) throws IOException;


}
