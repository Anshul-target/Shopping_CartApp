package com.ecom.frontend.service.impl;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.S3Client;
import com.ecom.frontend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket.category}")
    private String categoryBucket;

    @Value("${aws.s3.bucket.product}")
    private String productBucket;

    @Value("${aws.s3.bucket.profile}")
    private String profileBucket;

    @Override
    public Boolean uploadFileS3(MultipartFile file, Integer bucketType) {
        String bucketName = null;
        try {
            // Determine the bucket based on the bucket type
            if (bucketType == 1) {
                bucketName = categoryBucket;
            } else if (bucketType == 2) {
                bucketName = productBucket;
            } else {
                bucketName = profileBucket;
            }

            String fileName = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();

            // Create PutObjectRequest using the updated SDK
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)


                    .build();


            System.out.println("Uploading to bucket: " + bucketName);

            // Upload file to S3
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

            // Check if the file was uploaded successfully
            if (putObjectResponse != null && putObjectResponse.sdkHttpResponse().isSuccessful()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
