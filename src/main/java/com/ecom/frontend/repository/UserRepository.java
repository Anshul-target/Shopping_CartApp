package com.ecom.frontend.repository;


import com.ecom.frontend.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserDtls, Integer> {
    public UserDtls findByEmail(String email);

    public List<UserDtls> findByRole(String role);

    public UserDtls findByResetToken(String token);

    public Optional<UserDtls> findById(Integer id);

}
