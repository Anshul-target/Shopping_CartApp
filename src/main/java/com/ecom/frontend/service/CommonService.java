package com.ecom.frontend.service;

import com.ecom.frontend.model.Category;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CommonService {
    public void removeSessionMessage();

    public List<Category> getAllCategory();

    public Boolean saveCategory(Category category,MultipartFile file)throws IOException ;

    public Boolean deleteCategory(String id);

    public Category getCategory(String id);

    public boolean updateCategory(Category category);

    public List<Category> getAllActiveCategory();
}
