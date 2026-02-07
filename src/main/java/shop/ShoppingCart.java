package shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private final Map<String, Product> productsById = new HashMap<>();
    private final Map<String, Integer> quantityById = new HashMap<>();
    private Discount discount;

    public void applyDiscount(Discount discount){
        if (discount == null) {
            throw new IllegalArgumentException("discount");
        }
        this.discount = discount;
    }

    public void add(Product product){
        productsById.putIfAbsent(product.getId(), product);
        quantityById.merge(product.getId(), 1, Integer::sum);
    }

    public int getQuantity(String productId){
        return quantityById.getOrDefault(productId, 0);
    }

    public BigDecimal getTotal(){
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : quantityById.entrySet()) {
            String id = entry.getKey();
            int qty = entry.getValue();
            Product p = productsById.get(id);
            subtotal = subtotal.add(p.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        BigDecimal total = (discount == null) ? subtotal : discount.apply(subtotal);

        if (total.compareTo(BigDecimal.ZERO) < 0 ){
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public boolean remove(String productId){
        boolean existed = quantityById.remove(productId) != null;
        productsById.remove(productId);
        return existed;
    }

    public boolean updateQuantity(String productId, int quantity){
        if (quantity < 0) throw new IllegalArgumentException("quantity");
        if (!quantityById.containsKey(productId))  return false;

        if (quantity == 0) {
            quantityById.remove(productId);
            productsById.remove(productId);
            return true;
        }

        quantityById.put(productId, quantity);
        return true;
    }
}
