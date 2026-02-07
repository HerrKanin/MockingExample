package com.example.shop;

import java.math.BigDecimal;

@FunctionalInterface
public interface Discount {
    BigDecimal apply(BigDecimal subtotal);

}
