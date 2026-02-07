package shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.PercentageDiscount;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PercentageDiscountTest {

    @Test
    @DisplayName("Negative percentage discount throws IllegalArgumentException")
    void negativePercentageThrowsException() {
        assertThatThrownBy(()-> new PercentageDiscount(new BigDecimal("-10")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("percent");
    }
}
