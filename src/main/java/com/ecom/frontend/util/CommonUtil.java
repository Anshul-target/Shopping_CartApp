package com.ecom.frontend.util;

import com.ecom.frontend.model.ProductOrder;
import com.ecom.frontend.service.impl.ProductService;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

@Component
public class CommonUtil {

    @Value("${aws.s3.bucket.category}")
    private String categoryBucket;
    @Value("${aws.s3.bucket.product}")
    private String productBucket;
    @Value("${aws.s3.bucket.profile}")
    private  String profileBucket;

    @Autowired
    private JavaMailSender mailSender;

    public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("ayushyadav6a@gmail.com", "Shooping Cart");
        helper.setTo(reciepentEmail);

        String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
                + "\">Change my password</a></p>";
        helper.setSubject("Password Reset");
        helper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {

        // http://localhost:8080/forgot-password
        String siteUrl = request.getRequestURL().toString();

        return siteUrl.replace(request.getServletPath(), "");

    }

    String msg=null;;
    public Boolean sendMailForProductOrder(ProductOrder order, String status) throws Exception
    {

        msg="<p>Hello [[name]],</p>"
                + "<p>Thank you order <b>[[orderStatus]]</b>.</p>"
                + "<p><b>Product Details:</b></p>"
                + "<p>Name : [[productName]]</p>"
                + "<p>Quantity : [[quantity]]</p>"
                + "<p>Price : [[price]]</p>"
                + "<p>Payment Type : [[paymentType]]</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("ayushyadav6a@gmail.com", "Shooping Cart");
        helper.setTo(order.getOrderAddress().getEmail());
        msg=msg.replace("[[name]]",order.getOrderAddress().getFirstName());
        msg=msg.replace("[[orderStatus]]",status);
        msg=msg.replace("[[productName]]", order.getProductTitle());

        msg=msg.replace("[[quantity]]", order.getQuantity().toString());
        msg=msg.replace("[[price]]", order.getPrice().toString());
        msg=msg.replace("[[paymentType]]", order.getPaymentType());

        helper.setSubject("Product Order Status");
        helper.setText(msg, true);
        mailSender.send(message);
        return true;
    }


    public  String getImageUrl(MultipartFile file, Integer bucketType) {
        String bucketName = null;

        if (bucketType == 1) {
            bucketName = categoryBucket;
        } else if (bucketType == 2) {
            bucketName = productBucket;

        } else {
            bucketName = profileBucket;
        }
        String imageName=file !=null?file.getOriginalFilename():"default.jpg";
        String url="https://"+bucketName+".s3.amazonaws.com/"+imageName;
        return url;
    }

}
