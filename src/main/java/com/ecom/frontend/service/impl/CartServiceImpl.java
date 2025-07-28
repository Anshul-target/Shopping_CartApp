package com.ecom.frontend.service.impl;

import com.ecom.frontend.DTO.CartRequest_DTO;
import com.ecom.frontend.model.Cart;
import com.ecom.frontend.model.Product;
import com.ecom.frontend.model.UserDtls;
import com.ecom.frontend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ProductService productService;
    @Override
    public Boolean saveCart(Product product, UserDtls user) {
 String url="https://cartservice-production-90d9.up.railway.app/api1/addCart";
        CartRequest_DTO cartRequestDto=new CartRequest_DTO(product,user);
        ResponseEntity<Boolean> response = restTemplate.postForEntity(url,
                cartRequestDto,
                Boolean.class
        );
        return response.getBody();
    }

    @Override
    public List<Cart> getCartsByUser(Integer userId) {
        String url="https://cartservice-production-90d9.up.railway.app/api1/getCart?userId={userId}";
        Map<String, Integer> uriVariables = Map.of("userId", userId);
        ResponseEntity<Cart[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null, // No request body needed for GET
                Cart[].class, // Expected response type
                uriVariables // Pass the URI variables
        );
        Cart[] cartArray = response.getBody();
        // Return the response body, which is the count of products in the cart


        List<Cart> carts = cartArray != null ? Arrays.asList(cartArray) : Collections.emptyList();

        Double totalOrderPrices=0.0;
        List<Cart>updateCarts=new ArrayList<>();
        for (Cart c : carts) {

            if (c.getProductIds() != null && !c.getProductIds().isEmpty()) {

                String productId = c.getProductIds().get(0);
                Double productDiscountPrice = productService.getProductById(productId).getDiscountPrice();
                Double totalPrice = productDiscountPrice * c.getQuantity();
                c.setTotalPrice(totalPrice);
                totalOrderPrices+=totalPrice;

                c.setTotalOrderPrice(totalOrderPrices);
                updateCarts.add(c);
            }

            else {
                System.out.println("Warning: Product ID list is null or empty for cart item.");
            }
        }
        return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer userId) {
String url="https://cartservice-production-90d9.up.railway.app/api1/countProduct?userId={userId}";
        Map<String, Integer> uriVariables = Map.of("userId", userId);
        ResponseEntity<Integer> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null, // No request body needed for GET
                Integer.class, // Expected response type
                uriVariables // Pass the URI variables
        );

        // Return the response body, which is the count of products in the cart
        return response.getBody();



    }

    @Override
    public void updateQuantity(String sy, String cid) {

        String url = "https://cartservice-production-90d9.up.railway.app/api1/cartQuantityUpdate?sy={sy}&cid={cid}";
        Map<String, String> uriVariables = Map.of("sy", sy, "cid", cid); // Include both "sy" and "cid"

        ResponseEntity<Boolean> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null, // No request body needed for GET
                Boolean.class, // Expected response type
                uriVariables // Pass the URI variables
        );

        // You can process the response here if needed
//        if (response.getStatusCode() == HttpStatus.OK) {
//
//            System.out.println("Quantity updated successfully to: " + updatedQuantity);
//        } else {
//            System.out.println("Failed to update quantity. Status code: " + response.getStatusCode());
//        }
    }

}
