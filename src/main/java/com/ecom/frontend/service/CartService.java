package com.ecom.frontend.service;



import com.ecom.frontend.model.Cart;
import com.ecom.frontend.model.Product;
import com.ecom.frontend.model.UserDtls;

import java.util.List;

public interface CartService {
    public Boolean saveCart(Product product, UserDtls user);

    public List<Cart> getCartsByUser(Integer userId);

    public Integer getCountCart(Integer userId);

    public void updateQuantity(String sy, String cid);

}
