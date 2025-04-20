package com.ecom.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String id;
    private String title;
    private String description;
    private String category;
    private  Double price;

    private int stock;
    private String image;
    private  int discount;
    private Double discountPrice;

    private Boolean isActive;
}

