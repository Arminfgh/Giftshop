package com.giftshop.service;

import com.giftshop.model.Product;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * One cart per browser session.
 */
@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService implements Serializable {

    private final List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void add(Product product, int quantity) {
        if (product == null || quantity < 1) {
            return;
        }
        for (CartItem item : items) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                quantity));
    }

    public void updateQuantity(Long productId, int quantity) {
        if (quantity < 1) {
            remove(productId);
            return;
        }
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void remove(Long productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    public long getTotal() {
        long total = 0;
        for (CartItem item : items) {
            total += item.getLineTotal();
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
