package com.ecom.frontend.util;

public enum BucketType {
    CATEGORY(1, ""), Product(2, ""), Profile(3, "");

    private Integer id;

    private String name;

    private BucketType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
