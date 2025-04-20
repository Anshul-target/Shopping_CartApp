package com.ecom.frontend.service.impl;

import com.ecom.frontend.model.Cart;
import com.ecom.frontend.model.OrderAddress;
import com.ecom.frontend.model.OrderRequest;
import com.ecom.frontend.model.ProductOrder;
import com.ecom.frontend.repository.ProductOrderRepository;
import com.ecom.frontend.repository.UserRepository;
import com.ecom.frontend.service.OrderService;
import com.ecom.frontend.util.CommonUtil;
import com.ecom.frontend.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;
    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private ProductService productService;

    @Autowired
     private  UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
   private CommonUtil commonUtil;
    @Override
    public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception {
        List<Cart> carts = cartService.getCartsByUser(userid);

        for (Cart cart : carts) {

            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());

            order.setProductId(productService.getProductById(cart.getProductIds().get(0)).getId());
            order.setPrice(productService.getProductById(cart.getProductIds().get(0)).getDiscountPrice());

            order.setQuantity(cart.getQuantity());

            order.setUser(userRepository.findById(cart.getUserIds().get(0)).orElse(null));
            order.setProductTitle(productService.getProductById(cart.getProductIds().get(0)).getTitle());
            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);

            ProductOrder save = orderRepository.save(order);
            commonUtil.sendMailForProductOrder(save,"success");
        }
    }




    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        List<ProductOrder> orders = orderRepository.findByUserId(userId);
        return orders;
//        return null;
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> findById = orderRepository.findById(id);
        if (findById.isPresent()) {
            ProductOrder productOrder = findById.get();
            productOrder.setStatus(status);
            ProductOrder save = orderRepository.save(productOrder);
            return save;
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }
}

