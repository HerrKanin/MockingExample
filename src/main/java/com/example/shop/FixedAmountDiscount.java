package com.example.shop;

import java.math.BigDecimal;

public class FixedAmountDiscount implements Discount {
    private final BigDecimal amount;

    public FixedAmountDiscount(BigDecimal amount){
    this.amount = amount;
    }

    @Override
    public BigDecimal apply(BigDecimal subtotal){
        return subtotal.subtract(amount);
    }
}
