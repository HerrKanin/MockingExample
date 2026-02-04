package com.example.shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.array;

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

    @Test
    @DisplayName("Adding the same product twice accumulates quantity")
    void addSameProductTwiceAccumulatesQuantity(){
        ShoppingCart cart = new ShoppingCart();
        Product product = new Product("p1", "Coffe", new BigDecimal("49.90"));

        cart.add(product);
        cart.add(product);

        assertThat(cart.getQuantity("p1")).isEqualTo(2);
        assertThat(cart.getTotal()).isEqualByComparingTo("99.80");
    }

    @Test
    @DisplayName("Removing a product removes it completely from the cart")
    void removeProductRemovesCompletely(){
        ShoppingCart cart = new ShoppingCart();
        cart.add(new Product("p1", "Coffee", new BigDecimal("49.90")));
        cart.add(new Product("p2", "Tee", new BigDecimal("30.00")));

        boolean removed = cart.remove("p1");

        assertThat(removed).isTrue();
        assertThat(cart.getQuantity("p1")).isZero();
        assertThat(cart.getTotal()).isEqualByComparingTo("30.00");

    }
}
