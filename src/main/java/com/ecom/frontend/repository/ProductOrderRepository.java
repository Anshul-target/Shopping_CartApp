package com.ecom.frontend.repository;

import com.ecom.frontend.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder,Integer> {

    List<ProductOrder> findByUserId(Integer userId);
}
