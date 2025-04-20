package com.ecom.frontend.service;

import com.ecom.frontend.model.OrderRequest;
import com.ecom.frontend.model.ProductOrder;

import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userid, OrderRequest orderRequest)throws Exception;
//    public void saveOrder(Integer userid);


    public List<ProductOrder> getOrdersByUser(Integer userId);

    public ProductOrder updateOrderStatus(Integer id,String status);

    public List<ProductOrder> getAllOrders();
}
