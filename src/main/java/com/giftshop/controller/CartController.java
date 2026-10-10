package com.giftshop.controller;

import com.giftshop.model.Order;
import com.giftshop.model.OrderItem;
import com.giftshop.model.Product;
import com.giftshop.service.CartItem;
import com.giftshop.service.CartService;
import com.giftshop.service.OrderService;
import com.giftshop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final OrderService orderService;

    public CartController(CartService cartService,
                          ProductService productService,
                          OrderService orderService) {
        this.cartService = cartService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @PostMapping("/cart/add")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      RedirectAttributes ra) {
        Optional<Product> product = productService.findById(productId);
        if (product.isPresent() && product.get().isActive()) {
            cartService.add(product.get(), Math.max(1, quantity));
            ra.addFlashAttribute("toast", "Product added to your cart.");
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("total", cartService.getTotal());
        return "cart";
    }

    @PostMapping("/cart/update")
    public String update(@RequestParam Long productId,
                         @RequestParam int quantity) {
        cartService.updateQuantity(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String remove(@RequestParam Long productId) {
        cartService.remove(productId);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        if (cartService.isEmpty()) {
            return "redirect:/shop";
        }
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("total", cartService.getTotal());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam String phone,
                             @RequestParam(required = false) String address,
                             @RequestParam(required = false) String note,
                             RedirectAttributes ra) {
        if (cartService.isEmpty()) {
            return "redirect:/shop";
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setStatus("NEW");
        order.setTotalAmount(cartService.getTotal());

        for (CartItem ci : cartService.getItems()) {
            OrderItem oi = new OrderItem(ci.getProductId(), ci.getName(), ci.getPrice(), ci.getQuantity());
            order.addItem(oi);
        }

        Order saved = orderService.save(order);
        cartService.clear();

        return "redirect:/order-success/" + saved.getId();
    }

    @GetMapping("/order-success/{id}")
    public String orderSuccess(@PathVariable Long id, Model model) {
        Optional<Order> order = orderService.findById(id);
        if (order.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("order", order.get());
        return "order-success";
    }
}
