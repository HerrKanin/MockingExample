package shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.FixedAmountDiscount;
import shop.PercentageDiscount;
import shop.Product;
import shop.ShoppingCart;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

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

    @Test
    @DisplayName("Removing an unknow product returns false and does not chang the cart")
    void removeUnknownProductReturnsFalse(){
        ShoppingCart cart = new ShoppingCart();
        cart.add(new Product("p1", "Coffee", new BigDecimal("49.90")));

        boolean removed = cart.remove("does not exist");

        assertThat(removed).isFalse();
        assertThat(cart.getQuantity("p1")).isEqualTo(1);
        assertThat(cart.getTotal()).isEqualByComparingTo("49.90");
    }

    @Test
    @DisplayName("Updating quantity changes total price")
    void updateQuantityChangesTotalPrice(){
        ShoppingCart cart = new ShoppingCart();
        cart.add(new Product("p1", "Coffee", new BigDecimal("10.00")));

        boolean updated = cart.updateQuantity("p1", 5);

        assertThat(updated).isTrue();
        assertThat(cart.getQuantity("p1")).isEqualTo(5);
        assertThat(cart.getTotal()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("Updating quantity to zero removes the product from the cart")
    void updatingQuantityToZeroRemovesTheProductFromTheCart(){
        ShoppingCart cart = new ShoppingCart();
        cart.add(new Product("p1", "Coffee", new BigDecimal("10.00")));

        boolean updated = cart.updateQuantity("p1", 0);

        assertThat(updated).isTrue();
        assertThat(cart.getQuantity("p1")).isZero();
        assertThat(cart.getTotal()).isEqualByComparingTo("0.00");

        boolean updateAgain = cart.updateQuantity("p1", 1);
        assertThat(updateAgain).isFalse();
    }

    @Test
    @DisplayName("Updating quantity to a negative value throws IllegalArgumentException")
    void updatingQuantityToNegativeThrows(){
        ShoppingCart cart = new ShoppingCart();
        cart.add(new Product("p1", "Coffee", new BigDecimal("10.00")));

        assertThatThrownBy(()-> cart.updateQuantity("p1", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantity");
    }

    @Test
    @DisplayName("Applying a percentage discount reduces total price")
    void percentageDiscountReducesTotalPrice() {
        ShoppingCart cart = new ShoppingCart();
        cart.add(new Product("p1", "Coffee", new BigDecimal("100.00")));

        cart.applyDiscount(new PercentageDiscount(new BigDecimal("10")));

        assertThat(cart.getTotal()).isEqualByComparingTo("90.00");
    }

    @Test
    @DisplayName("A discount must never make the total price negtive")
    void discountNeverMakesTotalPriceNegative(){
        ShoppingCart cart = new ShoppingCart();
        cart.add(new Product("p1", "Coffee", new BigDecimal("20.00")));

        cart.applyDiscount(new FixedAmountDiscount(new BigDecimal("50.00")));

        assertThat(cart.getTotal()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Applying a null discount throws IllegalArgumentException")
    void applyDiscountNullThrows(){
        ShoppingCart cart = new ShoppingCart();

        assertThatThrownBy(()-> cart.applyDiscount(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("discount");
    }

}
