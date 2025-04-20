package com.ecom.frontend.controller;

import com.ecom.frontend.model.Category;
import com.ecom.frontend.model.PageDetails;
import com.ecom.frontend.model.Product;
import com.ecom.frontend.model.UserDtls;
import com.ecom.frontend.service.FileService;
import com.ecom.frontend.service.impl.CommonServiceImpl;
import com.ecom.frontend.service.impl.ProductService;
import com.ecom.frontend.service.impl.UserServiceImpl;
import com.ecom.frontend.util.BucketType;
import com.ecom.frontend.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import java.io.File;
import java.io.File;
import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//import static java.awt.Window.limit;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private CommonServiceImpl categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
private FileService fileService;

@GetMapping
    public String index(Model m) {
        List<Category> allActiveCategory=categoryService.getAllActiveCategory().stream().sorted((c1,c2)->c2.getId().compareTo(c1.getId())).limit(6).collect(Collectors.toList());
        List<Product> products1 = productService.getAllActiveProducts1("").stream().sorted((p1,p2)->p2.getId().compareTo(p1.getId())).limit(8).collect(Collectors.toList());
        m.addAttribute("category",allActiveCategory);
        m.addAttribute("products",products1);
        return "index";
    }


    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
        }


        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categorys", allActiveCategory);
    }
    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category,@RequestParam(name = "pageNo",defaultValue="0") Integer pageNo,@RequestParam(name = "pageSize",defaultValue = "3")Integer pageSize) {
        // System.out.println("category="+category);
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts(category,pageNo,pageSize);
//        System.out.println(category);
        PageDetails page=productService.getPageDetails(category,pageNo,pageSize);
//        System.out.println(page.getTotalPages());
//        System.out.println(page);

        m.addAttribute("categories", categories);
        m.addAttribute("paramValue", category);

        m.addAttribute("products", products);
        m.addAttribute("productsSize", products.size());
        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable String id, Model m) {
        Product productById = productService.getProductById(id);
        m.addAttribute("product", productById);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
            throws IOException {

        String imageUrl = commonUtil.getImageUrl(file, BucketType.Profile.getId());

        user.setProfileImage(imageUrl);
        UserDtls userExist=userService.getUserByEmail(user.getEmail());
        if (userExist!=null){
            session.setAttribute("errorMsg", "user exist!!!");
            return "redirect:/signin";
        }
        System.out.println(user.getCpassword());
        System.out.println(user.getPassword());
        if(!user.getPassword().equals(user.getCpassword())){
            session.setAttribute("errorMsg", "password donot matched");
            return "redirect:/register";
        }
        UserDtls saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {

                fileService.uploadFileS3(file,BucketType.Profile.getId());

//                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Register successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/register";
    }


    //	Forgot Password Code

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot_password.html";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {

        UserDtls userByEmail = userService.getUserByEmail(email);

        if (ObjectUtils.isEmpty(userByEmail)) {
            session.setAttribute("errorMsg", "Invalid email");
        } else {

            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            // Generate URL :
            // http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg

            String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

            Boolean sendMail = commonUtil.sendMail(url, email);

            if (sendMail) {
                session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
            } else {
                session.setAttribute("errorMsg", "Somethong wrong on server ! Email not send");
            }
        }

        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

        UserDtls userByToken = userService.getUserByToken(token);

        if (userByToken == null) {
            m.addAttribute("msg", "Your link is invalid or expired !!");
            return "message";
        }
        m.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
                                Model m) {

        UserDtls userByToken = userService.getUserByToken(token);
        if (userByToken == null) {
            m.addAttribute("errorMsg", "Your link is invalid or expired !!");
            return "message";
        } else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            // session.setAttribute("succMsg", "Password change successfully");
            m.addAttribute("msg", "Password change successfully");

            return "message";
        }

    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model m) {
        List<Product> searchProducts = productService.searchProduct(ch);
        m.addAttribute("products", searchProducts);
        List<Category> categories = categoryService.getAllActiveCategory();
        m.addAttribute("categories", categories);
        return "product";

    }


}