package com.giftshop.controller;

import com.giftshop.config.AdminInterceptor;
import com.giftshop.model.Category;
import com.giftshop.model.Product;
import com.giftshop.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final MessageService messageService;
    private final SettingService settingService;
    private final FileStorageService fileStorageService;

    public AdminController(ProductService productService,
                          CategoryService categoryService,
                          OrderService orderService,
                          MessageService messageService,
                          SettingService settingService,
                          FileStorageService fileStorageService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.messageService = messageService;
        this.settingService = settingService;
        this.fileStorageService = fileStorageService;
    }

    // ==================== LOGIN / LOGOUT ====================

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String password,
                        HttpServletRequest request,
                        RedirectAttributes ra) {
        String real = settingService.get("adminPassword");
        if (real != null && real.equals(password)) {
            HttpSession session = request.getSession(true);
            session.setAttribute(AdminInterceptor.SESSION_KEY, Boolean.TRUE);
            return "redirect:/admin/dashboard";
        }
        ra.addFlashAttribute("error", "Incorrect password.");
        return "redirect:/admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/admin/login";
    }

    // ==================== DASHBOARD ====================

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("productCount", productService.count());
        model.addAttribute("orderCount", orderService.count());
        model.addAttribute("newOrderCount", orderService.countNew());
        model.addAttribute("messageCount", messageService.count());
        model.addAttribute("unreadCount", messageService.unreadCount());
        model.addAttribute("recentOrders", limit(orderService.all(), 5));
        return "admin/dashboard";
    }

    // ==================== PRODUCTS ====================

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.allForAdmin());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.all());
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Product> p = productService.findById(id);
        if (p.isEmpty()) {
            ra.addFlashAttribute("error", "Product not found.");
            return "redirect:/admin/products";
        }
        model.addAttribute("product", p.get());
        model.addAttribute("categories", categoryService.all());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@RequestParam(required = false) Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(defaultValue = "0") Long price,
                              @RequestParam(required = false) Long oldPrice,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(defaultValue = "0") Integer stock,
                              @RequestParam(defaultValue = "false") boolean featured,
                              @RequestParam(defaultValue = "false") boolean active,
                              @RequestParam(required = false) String imageUrl,
                              @RequestParam(required = false) MultipartFile imageFile,
                              RedirectAttributes ra) {

        Product product;
        if (id != null) {
            product = productService.findById(id).orElse(new Product());
        } else {
            product = new Product();
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price == null ? 0L : price);
        product.setOldPrice(oldPrice);
        product.setStock(stock == null ? 0 : stock);
        product.setFeatured(featured);
        product.setActive(active);

        if (categoryId != null) {
            categoryService.findById(categoryId).ifPresent(product::setCategory);
        } else {
            product.setCategory(null);
        }

        // Image: uploaded file wins; otherwise use pasted URL; otherwise keep old.
        String uploaded = fileStorageService.store(imageFile);
        if (uploaded != null) {
            product.setImageUrl(uploaded);
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            product.setImageUrl(imageUrl.trim());
        }

        productService.save(product);
        ra.addFlashAttribute("toast", "Product saved successfully.");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        productService.delete(id);
        ra.addFlashAttribute("toast", "Product deleted.");
        return "redirect:/admin/products";
    }

    // ==================== CATEGORIES ====================

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.all());
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@RequestParam(required = false) Long id,
                               @RequestParam String name,
                               @RequestParam(required = false) String icon,
                               RedirectAttributes ra) {
        Category c;
        if (id != null) {
            c = categoryService.findById(id).orElse(new Category());
        } else {
            c = new Category();
        }
        c.setName(name);
        c.setIcon(icon);
        categoryService.save(c);
        ra.addFlashAttribute("toast", "Destination saved.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes ra) {
        // Detach products from this category first to avoid database errors.
        for (Product p : productService.allForAdmin()) {
            if (p.getCategory() != null && p.getCategory().getId().equals(id)) {
                p.setCategory(null);
                productService.save(p);
            }
        }
        categoryService.delete(id);
        ra.addFlashAttribute("toast", "Destination deleted.");
        return "redirect:/admin/categories";
    }

    // ==================== ORDERS ====================

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.all());
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<com.giftshop.model.Order> order = orderService.findById(id);
        if (order.isEmpty()) {
            ra.addFlashAttribute("error", "Order not found.");
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order.get());
        return "admin/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    RedirectAttributes ra) {
        orderService.updateStatus(id, status);
        ra.addFlashAttribute("toast", "Order status updated.");
        return "redirect:/admin/orders/" + id;
    }

    // ==================== MESSAGES ====================

    @GetMapping("/messages")
    public String messages(Model model) {
        model.addAttribute("messages", messageService.all());
        return "admin/messages";
    }

    @GetMapping("/messages/{id}")
    public String messageDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<com.giftshop.model.Message> m = messageService.findById(id);
        if (m.isEmpty()) {
            ra.addFlashAttribute("error", "Message not found.");
            return "redirect:/admin/messages";
        }
        com.giftshop.model.Message msg = m.get();
        if (!msg.isRead()) {
            msg.setRead(true);
            messageService.save(msg);
        }
        model.addAttribute("msg", msg);
        return "admin/message-detail";
    }

    @PostMapping("/messages/delete/{id}")
    public String deleteMessage(@PathVariable Long id, RedirectAttributes ra) {
        messageService.delete(id);
        ra.addFlashAttribute("toast", "Message deleted.");
        return "redirect:/admin/messages";
    }

    // ==================== SETTINGS ====================

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("settings", settingService.all());
        return "admin/settings";
    }

    @PostMapping("/settings/save")
    public String saveSettings(@RequestParam String shopName,
                               @RequestParam(required = false) String logoEmoji,
                               @RequestParam(required = false) String tagline,
                               @RequestParam(required = false) String currency,
                               @RequestParam(required = false) String heroTitle,
                               @RequestParam(required = false) String heroSubtitle,
                               @RequestParam(required = false) String aboutText,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String address,
                               @RequestParam(required = false) String workingHours,
                               @RequestParam(required = false) String instagram,
                               @RequestParam(required = false) String telegram,
                               @RequestParam(required = false) String whatsapp,
                               @RequestParam(required = false) String footerText,
                               @RequestParam(required = false) String shippingNote,
                               RedirectAttributes ra) {
        settingService.set("shopName", shopName);
        settingService.set("logoEmoji", logoEmoji);
        settingService.set("tagline", tagline);
        settingService.set("currency", currency);
        settingService.set("heroTitle", heroTitle);
        settingService.set("heroSubtitle", heroSubtitle);
        settingService.set("aboutText", aboutText);
        settingService.set("phone", phone);
        settingService.set("email", email);
        settingService.set("address", address);
        settingService.set("workingHours", workingHours);
        settingService.set("instagram", instagram);
        settingService.set("telegram", telegram);
        settingService.set("whatsapp", whatsapp);
        settingService.set("footerText", footerText);
        settingService.set("shippingNote", shippingNote);
        ra.addFlashAttribute("toast", "Settings saved.");
        return "redirect:/admin/settings";
    }

    @PostMapping("/settings/password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        String real = settingService.get("adminPassword");
        if (real == null || !real.equals(currentPassword)) {
            ra.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/admin/settings";
        }
        if (newPassword == null || newPassword.length() < 4) {
            ra.addFlashAttribute("error", "New password must be at least 4 characters.");
            return "redirect:/admin/settings";
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "New password and confirmation do not match.");
            return "redirect:/admin/settings";
        }
        settingService.set("adminPassword", newPassword);
        ra.addFlashAttribute("toast", "Password changed successfully.");
        return "redirect:/admin/settings";
    }

    // helper
    private <T> List<T> limit(List<T> list, int max) {
        if (list.size() <= max) {
            return list;
        }
        return list.subList(0, max);
    }
}
