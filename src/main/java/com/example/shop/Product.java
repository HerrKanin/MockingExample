package com.example.shop;

import java.math.BigDecimal;

public class Product {

    private final String id;
    private final String name;
    private final BigDecimal price;

    public Product(String id, String name, BigDecimal price){
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public String getId(){
        return id;
    }
}
