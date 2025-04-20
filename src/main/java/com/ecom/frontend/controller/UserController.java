package com.ecom.frontend.controller;

import com.ecom.frontend.model.*;
import com.ecom.frontend.repository.UserRepository;
import com.ecom.frontend.service.CartService;
import com.ecom.frontend.service.CommonService;
import com.ecom.frontend.service.UserService;
import com.ecom.frontend.service.impl.CartServiceImpl;
import com.ecom.frontend.service.impl.OrderServiceImpl;
import com.ecom.frontend.service.impl.ProductService;
import com.ecom.frontend.service.impl.UserServiceImpl;
import com.ecom.frontend.util.CommonUtil;
import com.ecom.frontend.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CommonService categoryService;
    @Autowired
  private   UserRepository userRepository;

    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    ProductService productService;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
   private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categorys", allActiveCategory);
    }

//    addCart?pid=671b7e877ae51f4cb4e73ae6&uid=5
    @GetMapping("/addCart")
    public String addToCart(@RequestParam String pid, @RequestParam Integer uid, HttpSession session) {


        Product product = productService.getProductById(pid);

        UserDtls user = userRepository.findById(uid).orElse(null);
        Boolean Issaved = cartService.saveCart(product, user);

        if (!Issaved) {
            session.setAttribute("errorMsg", "Product add to cart failed");
        }else {
            session.setAttribute("succMsg", "Product added to cart");
        }
        return "redirect:/product/" + pid;
    }


    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m) {
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());




//        6803f280ad82537c2a0e2802
//        6803f598ad82537c2a0e2803
        m.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal p) {
      String email=p.getName();

      UserDtls userDtls=userService.getUserByEmail(email);
      return  userDtls;
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam String cid) {
        cartService.updateQuantity(sy, cid);
        return "redirect:/user/cart";
    }


    @GetMapping("/orders")
    public String orderPage(Principal p, Model m) {
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        m.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
            m.addAttribute("orderPrice", orderPrice);
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws Exception {
         System.out.println(request);
        UserDtls user = getLoggedInUserDetails(p);
        orderService.saveOrder(user.getId(), request);

        return "redirect:/user/success";
    }
    @GetMapping("/success")
    public String loadSuccess() {
        return "user/success";
    }
    @GetMapping("/user-orders")
    public String myOrder(Model m, Principal p) {
        UserDtls loginUser = getLoggedInUserDetails(p);
        List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
        m.addAttribute("orders", orders);
        return "user/my_orders";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

        try {
            commonUtil.sendMailForProductOrder(updateOrder, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "status not updated");
        }
        return "redirect:/user/user-orders";
    }

    @GetMapping("/profile")
    public String profile() {
        return "user/profile";
    }


    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
        UserDtls updateUserProfile = userService.updateUserProfile(user, img);
        if (ObjectUtils.isEmpty(updateUserProfile)) {
            session.setAttribute("errorMsg", "Profile not updated");
        }
        else {
            session.setAttribute("succMsg", "Profile Updated");
        }
//        user.getRole()
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
                                 HttpSession session) {
        UserDtls loggedInUserDetails = getLoggedInUserDetails(p);

        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

        if (matches) {
            String encodePassword = passwordEncoder.encode(newPassword);
            loggedInUserDetails.setPassword(encodePassword);
            UserDtls updateUser = userService.updateUser(loggedInUserDetails);
            if (ObjectUtils.isEmpty(updateUser)) {
                session.setAttribute("errorMsg", "Password not updated !! Error in server");
            } else {
                session.setAttribute("succMsg", "Password Updated sucessfully");
            }
        } else {
            session.setAttribute("errorMsg", "Current Password incorrect");
        }

        return "redirect:/user/profile";
    }

}
