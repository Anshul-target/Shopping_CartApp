package com.ecom.frontend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderId;
//[insert into product_order
//            (order_address_id,order_date,order_id,payment_type,price,product_id,product_title,quantity,status,user_id)
//    values (?,?,?,?,?,?,?,?,?,?)];
//SQL [insert into product_order (order_address_id,order_date,order_id,payment_type,price,product_id,product_title,quantity,status,user_id) values (?,?,?,?,?,?,?,?,?,?)]; constraint [null]
    private LocalDate orderDate;


    private String  productId;
    private String productTitle;

    private Double price;

    private Integer quantity;

    @ManyToOne
    private UserDtls user;

    private String status;

    private String paymentType;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;

}
