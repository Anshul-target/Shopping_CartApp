package com.ecom.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Cart {



    private String id;

    private List<Integer> userIds;

    //    @DBRef
    private List<String> productIds;

    private Integer quantity;


    private Double totalPrice;
    private String ProductName;

    private String imageName;

    private Double ProductPrice;

    private Double totalOrderPrice;



}

