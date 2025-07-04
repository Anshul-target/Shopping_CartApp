package com.ecom.frontend.config;

import com.ecom.frontend.model.UserDtls;
import com.ecom.frontend.repository.UserRepository;
import com.ecom.frontend.service.UserService;
import com.ecom.frontend.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");

        UserDtls userDtls = userRepository.findByEmail(email);




        if (userDtls==null){
            exception = new LockedException("Register first!!");
        }

       else if (userDtls.getIsEnable()) {

            if (userDtls.getAccountNonLocked()) {

                if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
                    userService.increaseFailedAttempt(userDtls);
                } else {
                    userService.userAccountLock(userDtls);
                    exception = new LockedException("Your account is locked !! failed attempt 3");
                }
            } else {

                if (userService.unlockAccountTimeExpired(userDtls)) {
                    exception = new LockedException("Your account is unlocked !! Please try to login");
                } else {
                    exception = new LockedException("your account is Locked !! Please try after sometimes");
                }
            }

        } else {
            exception = new LockedException("your account is inactive");
        }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
