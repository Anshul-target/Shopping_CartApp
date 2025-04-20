package com.ecom.frontend.controller;

import com.ecom.frontend.model.Category;
import com.ecom.frontend.model.Product;
import com.ecom.frontend.model.ProductOrder;
import com.ecom.frontend.model.UserDtls;
import com.ecom.frontend.service.CartService;
import com.ecom.frontend.service.FileService;
import com.ecom.frontend.service.UserService;
import com.ecom.frontend.service.impl.CommonServiceImpl;
import com.ecom.frontend.service.impl.OrderServiceImpl;
import com.ecom.frontend.service.impl.ProductService;
import com.ecom.frontend.service.impl.UserServiceImpl;
import com.ecom.frontend.util.BucketType;
import com.ecom.frontend.util.CommonUtil;
import com.ecom.frontend.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CommonServiceImpl commonService;
    @Autowired
    private ProductService productService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CartService cartService;

    @Autowired
     private OrderServiceImpl orderService;

    @Autowired
    private FileService fileService;

    @Autowired
    private CommonUtil commonUtil;






private PasswordEncoder passwordEncoder;
    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }

        List<Category> allActiveCategory = commonService.getAllActiveCategory();
        m.addAttribute("categorys", allActiveCategory);
    }
    @GetMapping("/")
    public String index3() {
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadProduct(Model model) {
        List<Category> allCategory = commonService.getAllCategory();
        model.addAttribute("categories", allCategory);
        return "admin/add_product";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String editCategory(@PathVariable String id, Model model) {
        System.out.println(id);
        Category category = commonService.getCategory(id);
        if (category != null) {
            System.out.println("Succefully get the editing Category");
            model.addAttribute("category", category);
        }
        return "admin/edit_category";
    }

    @GetMapping("/category")
    public String category(Model m) {
        List<Category> allCategory = commonService.getAllCategory();
        if (allCategory == null || allCategory.isEmpty()) {
//            System.out.println("Not found category");
        } else {
//            System.out.println("found category:"+allCategory.get(1).getName());
            m.addAttribute("categorys", allCategory);
        }


        return "admin/category";
    }


    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category categoryModel,

                               @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        System.out.println("saved");
// String imageName=file !=null?file.getOriginalFilename():"default.jpg";
        String imageUrl = commonUtil.getImageUrl(file, BucketType.CATEGORY.getId());
    categoryModel.setImageName(imageUrl);
        Boolean isSaved = commonService.saveCategory(categoryModel, file);

        if (!isSaved) {
            System.out.println("Not saved");
            session.setAttribute("errorMsg", "CategoryService not saved");
        } else {
            System.out.println("saved" + " " + isSaved);

//            File saveFile = new ClassPathResource("static/img").getFile();
//
//            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
//                    + file.getOriginalFilename());
//
//            System.out.println(path);
//            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            fileService.uploadFileS3(file,1);



            session.setAttribute("succMsg", "Saved successfully");

        }
        return "redirect:/admin/category";
    }


    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable String id, HttpSession session) {
        System.out.println("Entered here");
        Boolean deleteCategory = commonService.deleteCategory(id);
        if (deleteCategory) {
            System.out.println("true");
            session.setAttribute("succMsg", "category delete success");
        } else {
            System.out.println("false");
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/admin/category";
    }



    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        System.out.println("Entered the updateCategory");
        Category oldcategory = commonService.getCategory(category.getId());

//        String imageName = (file.isEmpty() ? oldcategory.getImageName() : file.getOriginalFilename());
        String imageUrl = commonUtil.getImageUrl(file, BucketType.CATEGORY.getId());

        if (category != null) {
            String category1= category.getName().replace(" ","");
            oldcategory.setName(category1);
            oldcategory.setIsActive(category.getIsActive());
            oldcategory.setImageName(imageUrl);
        }
        Boolean isUpdated = commonService.updateCategory(oldcategory);

        if (isUpdated) {
            session.setAttribute("succMsg", "Category updated");
            if (!file.isEmpty()) {
//                File saveFile = new ClassPathResource("static/img").getFile();
//
//                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
//                        + file.getOriginalFilename());
                fileService.uploadFileS3(file,1);



//                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

        } else {
            session.setAttribute("errorMsg", "Name already Present");
            return "redirect:/admin/category";
        }
        return "redirect:/admin/category";
    }


    //    Now the products endpoints
    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
                              HttpSession session) throws IOException {

//        System.out.println(product.getTitle());
//        System.out.println(product.getDescription());
//
//        System.out.println(product.getCategory());
//        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        String imageUrl = commonUtil.getImageUrl(image, BucketType.CATEGORY.getId());

        product.setImage(imageUrl);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        Boolean issaved = productService.saveProduct(product);

        if (issaved) {

//            File saveFile = new ClassPathResource("static/img").getFile();
//
//            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
//                    + image.getOriginalFilename());
//
////            System.out.println(path);
//            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            fileService.uploadFileS3(image,BucketType.Product.getId());


            session.setAttribute("succMsg", "Product Saved Success");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }


        return "redirect:/admin/loadAddProduct";

    }



    @GetMapping("/products")
    public String loadViewProduct(Model m) {
        System.out.println("HI here");
        List<Product> allProducts = productService.getAllProducts();
//        System.out.println(allProducts.get(0).getTitle());
//        System.out.println(allProducts.get(0).getDescription());
//        System.out.println(allProducts.get(1).getTitle());
//        System.out.println(allProducts.get(1).getDescription());
        m.addAttribute("products", allProducts);
        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable String id, HttpSession session) {
        Boolean deleteProduct = productService.deleteProduct(id);
        if (deleteProduct) {
            session.setAttribute("succMsg", "Product delete success");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/products";
//        return "admin/index";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable String id, Model m) {
        m.addAttribute("product", productService.getProductById(id));
        m.addAttribute("categories", commonService.getAllCategory());
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
                                HttpSession session, Model m) throws IOException {

        System.out.println(product.getIsActive());
        if(product.getDiscount()<0 || product.getDiscount()>100){
            session.setAttribute("errorMsg", "invalid Discount");
        }
        else {

        Boolean isUpdated = productService.updateProduct(product,image);

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

        product.setImage(imageName);



        if (isUpdated) {


            if (!image.isEmpty()) {

                try {
//
                    fileService.uploadFileS3(image,BucketType.Product.getId());


                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    session.setAttribute("succMsg", "Sucessfully updated");
                }

            }
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }
        }



        return "redirect:/admin/editProduct/" + product.getId();

    }

    @GetMapping("/users")
    public String getAllUsers(Model m, @RequestParam Integer type) {
        List<UserDtls> users = null;
        if (type == 1) {
            users = userService.getUsers("ROLE_USER");
        } else {
            users = userService.getUsers("ROLE_ADMIN");
        }
        m.addAttribute("userType",type);
        m.addAttribute("users", users);
        return "admin/users";
    }


    @GetMapping("/updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,@RequestParam Integer type,  HttpSession session) {
        Boolean f = userService.updateAccountStatus(id, status);
        if (f) {
            session.setAttribute("succMsg", "Account Status Updated");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/users?type="+type;
    }

    @GetMapping("/orders")
    public String getAllOrders(Model m) {
        List<ProductOrder> allOrders = orderService.getAllOrders();
        m.addAttribute("orders", allOrders);
        return "admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

        if (updateOrder!=null) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "status not updated");
        }
        return "redirect:/admin/orders";
    }


    @GetMapping("/add-admin")
    public String loadAdminAdd() {
        return "admin/add_admin";
    }

    @PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
            throws IOException {

//        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        String imageUrl = commonUtil.getImageUrl(file, BucketType.Profile.getId());

        user.setProfileImage(imageUrl);
        UserDtls saveUser = userService.saveAdmin(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {
                fileService.uploadFileS3(file,BucketType.Profile.getId());

            }
            session.setAttribute("succMsg", "Register successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/admin/add-admin";
    }

    @GetMapping("/profile")
    public String profile() {

        return "admin/profile";
//        return "/admin/add_admin";
    }
//    http://localhost:8080/admin/profile
//    http://localhost:8080/user/update-profile
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
        UserDtls updateUserProfile = userService.updateUserProfile(user, img);
        if (ObjectUtils.isEmpty(updateUserProfile)) {
            session.setAttribute("errorMsg", "Profile not updated");
        } else {
            session.setAttribute("succMsg", "Profile Updated");
        }
        return "redirect:/admin/profile";
    }

    private UserDtls getLoggedInUserDetails(Principal p) {
        String email=p.getName();

        UserDtls userDtls=userService.getUserByEmail(email);
        return  userDtls;
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

        return "redirect:/admin/profile";
    }



}

