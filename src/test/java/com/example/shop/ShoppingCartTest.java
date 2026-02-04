package com.example.shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartTest {

    @Test
    @DisplayName("Empty cart should be total value 0")
    void emptyCartTotalIsZero(){
        ShoppingCart cart = new ShoppingCart();

        assertThat(cart.getTotal()).isZero();
    }

    @Test
    @DisplayName("Adding a product sets total price to product price")
    void addOneProductSetsTotalToProductPrice(){
        ShoppingCart cart = new ShoppingCart();
        Product product = new Product("p1", "Coffee", new BigDecimal("49.90"));

        cart.add(product);

        assertThat(cart.getTotal()).isEqualByComparingTo("49.90");
    }
}
