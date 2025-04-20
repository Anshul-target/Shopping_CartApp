package com.ecom.frontend.service;

import com.ecom.frontend.model.PageDetails;
import com.ecom.frontend.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductInterface {



        public Boolean saveProduct(Product product);

        public List<Product> getAllProducts();

        public Boolean deleteProduct(String id);

        public Product getProductById(String id);

        public Boolean updateProduct(Product product,MultipartFile image)throws IOException;

        public List<Product> getAllActiveProducts(String category,Integer pageNo,Integer pageSize);

        public PageDetails getPageDetails(String category, Integer pageNo, Integer pageSize);

       public List<Product> searchProduct(String ch);
    }


