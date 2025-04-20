package com.ecom.frontend.DTO;

import com.ecom.frontend.model.Product;
import com.ecom.frontend.model.UserDtls;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest_DTO
{

private Product product
        ;
private UserDtls userDtls;

}

