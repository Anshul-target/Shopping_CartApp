package com.ecom.frontend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecom.frontend.util.CloudinaryResponse;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectResponse;
//import software.amazon.awssdk.services.s3.S3Client;
import com.ecom.frontend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

//    @Autowired
//    private S3Client s3Client;
    @Autowired
    private Cloudinary cloudinary;

//    @Value("${aws.s3.bucket.category}")
//    private String categoryBucket;

//    @Value("${aws.s3.bucket.product}")
//    private String productBucket;

//    @Value("${aws.s3.bucket.profile}")
//    private String profileBucket;

    @Value("${cloudinary.category}")
    private String categoryBucket;

    @Value("${cloudinary.product}")
    private String productBucket;
    @Value("${cloudinary.profile}")
    private String profileBucket;

//    @Override
//    public Boolean uploadFileS3(MultipartFile file, Integer bucketType) {
//        String bucketName = null;
//        try {
//            // Determine the bucket based on the bucket type
//            if (bucketType == 1) {
//                bucketName = categoryBucket;
//            } else if (bucketType == 2) {
//                bucketName = productBucket;
//            } else {
//                bucketName = profileBucket;
//            }
//
//            String fileName = file.getOriginalFilename();
//            InputStream inputStream = file.getInputStream();
//
//            // Create PutObjectRequest using the updated SDK
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileName)
//
//
//                    .build();
//
//
//            System.out.println("Uploading to bucket: " + bucketName);
//
//            // Upload file to S3
//            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
//
//            // Check if the file was uploaded successfully
//            if (putObjectResponse != null && putObjectResponse.sdkHttpResponse().isSuccessful()) {
//                return true;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }


    public CloudinaryResponse uploadFile(MultipartFile file, Integer bucketType) throws IOException {
        String originalFileName = file.getOriginalFilename();

        String bucketName = null;
try{
    if (bucketType == 1) {
        bucketName = categoryBucket;
    } else if (bucketType == 2) {
        bucketName = productBucket;
    } else {
        bucketName = profileBucket;
    }
    // Remove extension if present (optional, but cleaner)
    String publicId = originalFileName != null
            ? originalFileName.replaceFirst("[.][^.]+$", "")
            : "default_filename";

    Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
            "folder", bucketName,
            "public_id", publicId
    ));
    String secureUrl = uploadResult.get("secure_url").toString();
    String Id = uploadResult.get("public_id").toString();
    return new CloudinaryResponse(Id,secureUrl);
}
catch (Exception exception){
    System.out.println(exception.getMessage());
    return null;
}

        // Determine the bucket based on the bucket type



    }


}

//Absolutely! Let's break down the line you asked about in detail:
//
//        ```java
//        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
//        "folder", bucketName,
//        "public_id", publicId
//        ));
//        ```
//
//        ---
//
//        ## 🔍 Purpose of This Line
//
//        This line uploads a file to **Cloudinary** and stores the result in a `Map` called `uploadResult`.
//
//        ---
//
//        ## ✅ Step-by-Step Breakdown
//
//        ### 🔹 `cloudinary.uploader()`
//
//        * This returns the **uploader object** from the Cloudinary Java SDK.
//        * The uploader is used to upload resources (images, videos, etc.) to your Cloudinary account.
//
//        ### 🔹 `.upload(...)`
//
//        * This method **performs the actual upload** to Cloudinary.
//        * It takes:
//
//        1. The **file data** as a byte array.
//        2. A **map of options** (like folder, public ID, etc.).
//
//        ### 🔹 `file.getBytes()`
//
//        * Converts the uploaded file (`MultipartFile`) into a **byte array**, which Cloudinary needs for upload.
//        * This is the actual **file content**.
//
//        ### 🔹 `ObjectUtils.asMap(...)`
//
//        * This is a utility method from the Cloudinary SDK.
//        * It creates a `Map<String, Object>` to hold **upload options**.
//        * In your case:
//
//        ```java
//        ObjectUtils.asMap(
//        "folder", bucketName,
//        "public_id", publicId
//        )
//        ```
//
//        This means:
//
//        | Key           | Value        | Purpose                                                                                         |
//        | ------------- | ------------ | ----------------------------------------------------------------------------------------------- |
//        | `"folder"`    | `bucketName` | Cloudinary **folder** (like a virtual directory: e.g., `product`, `category`)                   |
//        | `"public_id"` | `publicId`   | The **filename without extension** — this becomes the unique Cloudinary identifier for the file |
//
//        So the file is uploaded to:
//
//        ```
//        https://res.cloudinary.com/your-cloud-name/image/upload/<folder>/<public_id>.<extension>
//        ```
//
//        ---
//
//        ### 🔹 `Map uploadResult = ...`
//
//        * The `upload()` method returns a `Map` containing:
//
//        * `url`: URL of the uploaded image
//        * `secure_url`: HTTPS version of the URL
//        * `public_id`: The file's unique identifier in Cloudinary
//        * `format`: The file extension (like `jpg`, `png`)
//        * `resource_type`: Usually `"image"` or `"video"`
//        * `created_at`, `version`, etc.
//
//        > 🔧 You can extract values like this:
//
//        ```java
//        String uploadedUrl = uploadResult.get("url").toString();
//        ```
//
//        ---
//
//        ## 🧠 Summary
//
//        This line:
//
//        * Uploads a file (given as bytes)
//        * Assigns it a **custom name** and places it in a **folder**
//        * Stores the response in a `Map` so you can get the uploaded file's URL or other metadata
//
//        ---
//
//        ## 🧪 Optional — Print or Use URL
//
//        ```java
//        System.out.println("Image URL: " + uploadResult.get("secure_url"));
//        ```
//
//        Would you like me to show how to return the URL directly from your method too?
