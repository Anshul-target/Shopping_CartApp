package com.ecom.frontend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dhtgh8ktx",
                "api_key", "667835323942981",
                "api_secret", "zHkp4zICqyw_7PJE54YkqqekBPc"
        ));
    }
}
//    Great question.
//
//        In the context of the **Cloudinary Java SDK**, `ObjectUtils` is a **utility/helper class** that provides methods to **easily create and manipulate maps and parameters** when interacting with Cloudinary’s API.
//
//        ---
//
//        ## 🧰 What is `ObjectUtils`?
//
//        * It is part of the `com.cloudinary.utils` package.
//        * It offers **static methods** to help build parameter maps for:
//
//        * Configuring the Cloudinary object.
//        * Uploading files.
//        * Applying transformations.
//        * Setting options for image/video delivery.
//
//        ---
//
//        ## 🔧 Key Methods of `ObjectUtils`
//
//        | Method                             | Description                                                                 |
//        | ---------------------------------- | --------------------------------------------------------------------------- |
//        | `asMap(Object... values)`          | Quickly creates a `Map<String, Object>` using key-value pairs.              |
//        | `emptyMap()`                       | Returns an empty, immutable map (commonly used when no options are needed). |
//        | `asArray(Object... values)`        | Returns an array from the given values.                                     |
//        | `asObjectMap(Map<String, Object>)` | Converts a generic map into a strongly typed object map.                    |
//
//        ---
//
//        ## ✅ Example Usages
//
//        ### 1. **Creating Configuration Parameters**
//
//        ```java
//        Map<String, Object> config = ObjectUtils.asMap(
//        "cloud_name", "mycloud",
//        "api_key", "12345",
//        "api_secret", "abcde"
//        );
//        ```
//
//        ### 2. **Passing Upload Options**
//
//        ```java
//        cloudinary.uploader().upload(file, ObjectUtils.asMap(
//        "folder", "profile_pics",
//        "public_id", "user123"
//        ));
//        ```
//
//        ### 3. **No options? Use `emptyMap()`**
//
//        ```java
//        cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
//        ```
//
//        ---
//
//        ## 🔍 Internally, what does `asMap()` do?
//
//        It's a **varargs method** that:
//
//        1. Ensures an **even number of arguments** (key1, value1, key2, value2…).
//        2. Puts each pair into a new `Map<String, Object>`.
//        3. Returns that map, ready to use in API methods.
//
//        ---
//
//        ## 📌 Summary
//
//        | Feature          | Description                                                |
//        | ---------------- | ---------------------------------------------------------- |
//        | **Class**        | `ObjectUtils`                                              |
//        | **From**         | Cloudinary Java SDK                                        |
//        | **Main Purpose** | Quickly build maps of options/parameters                   |
//        | **Used In**      | Configuration, upload options, transformations             |
//        | **Benefits**     | Cleaner code, faster setup, fewer bugs in map construction |
//
//        ---
//
//        If you're interested, I can show how `ObjectUtils` helps with image **transformations** too (like resizing or cropping). Would you like to see that?

