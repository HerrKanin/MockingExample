package com.example.shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartTest {

    @Test
    @DisplayName("Empty cart should be total value 0")
    void emptyCartTotalIsZero(){
        ShoppingCart cart = new ShoppingCart();

        assertThat(cart.getTotal()).isZero();
    }
}
