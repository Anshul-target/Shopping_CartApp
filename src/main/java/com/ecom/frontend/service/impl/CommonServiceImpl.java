package com.ecom.frontend.service.impl;

import com.ecom.frontend.model.Category;
import com.ecom.frontend.service.CommonService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class CommonServiceImpl implements CommonService {
 @Autowired
  private RestTemplate restTemplate;
    @Override
    public void removeSessionMessage() {
     HttpServletRequest request=((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpSession session = request.getSession();
        session.removeAttribute("succMsg");
        session.removeAttribute("errorMsg");


    }

    @Override
    public List<Category> getAllCategory() {

//      https://productcatalog-production.up.railway.app/api/admin/saveCategory
        String url = System.getenv("PRODUCT_SERVICE") + "/api/admin/getAllCategory";

        // Use ParameterizedTypeReference to capture the response as List<Category>
        ResponseEntity<List<Category>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Category>>() {}
        );

        // Return the list of categories

        return responseEntity.getBody();
    }




    public Boolean saveCategory(Category category, MultipartFile file) throws IOException {
        String url = System.getenv("PRODUCT_SERVICE") + "/api/admin/saveCategory";
        System.out.println("Service: " + category.getName());
  String category1= category.getName().replace(" ","");
        // Send the request using RestTemplate, passing the Category object and the file

        // Prepare form data for sending
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("name", category1);
        formData.add("isActive", category.getIsActive());
        formData.add("imageName", category.getImageName());



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA); // Set the correct content type

        // Create the HTTP request entity with headers and form data
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        // Send the request
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Boolean.class
        );

        return responseEntity.getBody(); // Return the response from the service
    }




    public Boolean deleteCategory(String id){
        System.out.println("Entering delete endpoint...");
        String url = System.getenv("PRODUCT_SERVICE") + "/api/admin/deleteCategory/{id}";

        // Perform a GET request to retrieve the Boolean result from the server
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,  // or HttpMethod.DELETE based on your endpoint design
                null,
                Boolean.class,
                id
        );

        // Return the boolean value received from the response
        return responseEntity.getBody();
    }

    @Override
    public Category getCategory(String id) {
        System.out.println("Entering edit endpoint...");
        String url = System.getenv("PRODUCT_SERVICE") + "/api/admin/editCategory/{id}";
        ResponseEntity<Category> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,  // or HttpMethod.DELETE based on your endpoint design
                null,
                Category.class,
                id
        );

        // Return the boolean value received from the response
        return responseEntity.getBody();

    }

    @Override
    public boolean updateCategory(Category category) {
        // The URL for the update request to the external API
        String url = System.getenv("PRODUCT_SERVICE") + "/api/admin/updateCategory";

        // Create HttpHeaders for form submission
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA); // For sending as @ModelAttribute

        // Prepare form data
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("id", category.getId()); // Assuming your Category has an ID field
        formData.add("name", category.getName());
        formData.add("imageName", category.getImageName());
        formData.add("isActive", category.getIsActive());


        // Create an HttpEntity with the form data and headers
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        // Send the request using RestTemplate
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT, // Use POST for updates if using @ModelAttribute
                requestEntity,
                Boolean.class
        );

        return responseEntity.getBody(); // Return the response body indicating success or failure
    }

    @Override
    public List<Category> getAllActiveCategory() {
        String url = System.getenv("PRODUCT_SERVICE") + "/api/admin/getAllActiveCategory";

        // Use ParameterizedTypeReference to capture the response as List<Category>
        ResponseEntity<List<Category>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Category>>() {}
        );

        // Return the list of categories

        return responseEntity.getBody();
//        return null;
    }

}
