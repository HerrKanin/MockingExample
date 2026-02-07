package shop;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageDiscount implements Discount {
    private final BigDecimal percent;

    public PercentageDiscount(BigDecimal percent){
        if (percent == null){
            throw new IllegalArgumentException("percent");
        }
        if (percent.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("percent");
        }
        this.percent = percent;
    }

    @Override
    public BigDecimal apply(BigDecimal subtotal){
        BigDecimal factor = BigDecimal.ONE.subtract(percent.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));
        return subtotal.multiply(factor);
    }
}
