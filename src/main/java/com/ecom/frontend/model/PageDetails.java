package com.ecom.frontend.model;

import lombok.Data;

@Data
public class PageDetails
{
    int number ;
    long totalElements ;
    int totalPages;
    boolean first;
    boolean last;

}
