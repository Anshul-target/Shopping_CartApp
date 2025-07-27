package com.ecom.frontend.service.impl;

import com.ecom.frontend.model.Category;
import com.ecom.frontend.model.PageDetails;
import com.ecom.frontend.model.Product;
import com.ecom.frontend.service.ProductInterface;
import com.ecom.frontend.util.BucketType;
import com.ecom.frontend.util.CloudinaryResponse;
import com.ecom.frontend.util.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.List;


@Service
public class ProductService implements ProductInterface {

    @Autowired
RestTemplate restTemplate;

    @Autowired
    private CommonUtil commonUtil;
    @Override
    public Boolean saveProduct(Product product) {
        String url = "http://localhost:8082/api/admin/saveProduct";


        // Prepare form data for sending
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("title", product.getTitle());
        formData.add("description", product.getDescription());
        formData.add("category", product.getCategory());
        formData.add(" price ", product.getPrice());
        formData.add("isActive", product.getIsActive());
        formData.add("stock", product.getStock());
        formData.add("image", product.getImage());
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

        return responseEntity.getBody();
    }

    @Override
    public List<Product> getAllProducts() {
        String url = "http://localhost:8082/api/admin/products";

        ResponseEntity<List<Product>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        );

        return responseEntity.getBody();
    }

    @Override
    public Boolean deleteProduct(String id) {
        String url = "http://localhost:8082/api/admin/deleteProduct/{id}";

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
    public Product getProductById(String id) {
        String url = "http://localhost:8082/api/admin/editProduct/{id}";

        ResponseEntity<Product> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,  // or HttpMethod.DELETE based on your endpoint design
                null,
                Product.class,
                id
        );
        return responseEntity.getBody();
    }

    @Override
    public Boolean updateProduct(Product product,MultipartFile file) throws IOException {
        String url = "http://localhost:8082/api/admin/updateProduct";

//        String imageUrl = commonUtil.getImageUrl(file, BucketType.Product.getId());

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("id", product.getId());
        formData.add("title", product.getTitle());
        formData.add("description", product.getDescription());
        formData.add("category", product.getCategory());
        formData.add("price", product.getPrice());
        formData.add("stock", product.getStock());
        formData.add("isActive",product.getIsActive());
        formData.add("image",product.getImage());
        formData.add("discount",product.getDiscount());
        formData.add("discountPrice",product.getDiscountPrice());





        // If the image file is not empty, use it. Otherwise, send a dummy image.
        if (file == null || file.isEmpty()) {
            // Create a dummy empty file
            ByteArrayResource emptyFileAsResource = new ByteArrayResource(new byte[0]) {
                @Override
                public String getFilename() {
                    return "empty.jpg"; // You can set the filename to whatever you want
                }
            };
            formData.add("file", emptyFileAsResource); // Add the empty file to form data
        } else {
            // Wrap the actual file provided
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            formData.add("file", fileAsResource); // Add the actual file to form data
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA); // Set the correct content type

        // Create the HTTP request entity with headers and form data
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        // Send the request
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                Boolean.class
        );

        return responseEntity.getBody();
    }

    public List<Product> getAllActiveProducts1(String category) {
        String url = "http://localhost:8082/api/admin/getAllActiveproducts1";
        // Construct the URL with the query parameter
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("categoryName", category);

        // Send the GET request with the constructed URL
        ResponseEntity<List<Product>> responseEntity = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        );

        return responseEntity.getBody();

    }
    @Override
    public List<Product> getAllActiveProducts(String category,Integer pageNo,Integer pageSize) {
        String url = "http://localhost:8082/api/admin/getAllActiveproducts";
        // Construct the URL with the query parameter
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("categoryName", category)
                  .queryParam("pageNo", pageNo)
                .queryParam("pageSize", pageSize);
        // Send the GET request with the constructed URL
        ResponseEntity<List<Product>> responseEntity = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        );

        return responseEntity.getBody();

    }



    public PageDetails getPageDetails(String category, Integer pageNo, Integer pageSize) {
        String baseUrl = "http://localhost:8082/api/admin/getPageDetails";

        // Construct URL with parameters
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("categoryName", category)
                .queryParam("pageNo", pageNo)
                .queryParam("pageSize", pageSize);

        // Execute GET request
        ResponseEntity<PageDetails> responseEntity = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                null,
                PageDetails.class
        );

        // Return the response body (PageDetails object)
        return responseEntity.getBody();
    }
    @Override
    public List<Product> searchProduct(String ch) {
        return null;
    }
}
