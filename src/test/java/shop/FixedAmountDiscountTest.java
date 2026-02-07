package shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixedAmountDiscountTest {

    @Test
    @DisplayName("Negative fixed amount discount throws IllegalArgumentException")
    void negativeFixedAmountThrowsException(){
        assertThatThrownBy(()-> new FixedAmountDiscount(new BigDecimal("-5.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("amount");
    }
}
