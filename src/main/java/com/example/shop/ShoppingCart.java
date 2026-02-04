package com.example.shop;

import java.math.BigDecimal;

public class ShoppingCart {

    private BigDecimal total = BigDecimal.ZERO;

    public void add(Product product){
        total = total.add(product.getPrice());

    }
    public BigDecimal getTotal(){
        return total;
    }
}
