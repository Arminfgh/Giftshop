package com.giftshop.controller;

import com.giftshop.model.Category;
import com.giftshop.model.Product;
import com.giftshop.service.CategoryService;
import com.giftshop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ShopController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ShopController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/shop")
    public String shop(@RequestParam(required = false) Long category,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false, defaultValue = "newest") String sort,
                       Model model) {

        List<Product> products;
        String activeCategoryName = null;

        if (q != null && !q.isBlank()) {
            products = productService.search(q.trim());
        } else if (category != null) {
            Optional<Category> cat = categoryService.findById(category);
            if (cat.isPresent()) {
                products = productService.byCategory(cat.get());
                activeCategoryName = cat.get().getName();
            } else {
                products = productService.allActive();
            }
        } else {
            products = productService.allActive();
        }

        // sorting
        if ("cheap".equals(sort)) {
            products.sort((a, b) -> Long.compare(nz(a.getPrice()), nz(b.getPrice())));
        } else if ("expensive".equals(sort)) {
            products.sort((a, b) -> Long.compare(nz(b.getPrice()), nz(a.getPrice())));
        }

        model.addAttribute("products", products);
        model.addAttribute("activeCategory", category);
        model.addAttribute("activeCategoryName", activeCategoryName);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        return "shop";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty() || !product.get().isActive()) {
            return "redirect:/shop";
        }
        model.addAttribute("product", product.get());

        // a few related products from the same category
        if (product.get().getCategory() != null) {
            List<Product> related = productService.byCategory(product.get().getCategory());
            related.removeIf(p -> p.getId().equals(id));
            if (related.size() > 4) {
                related = related.subList(0, 4);
            }
            model.addAttribute("relatedProducts", related);
        }
        return "product";
    }

    private long nz(Long v) {
        return v == null ? 0 : v;
    }
}
