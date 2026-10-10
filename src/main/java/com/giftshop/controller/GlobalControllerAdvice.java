package com.giftshop.controller;

import com.giftshop.service.CartService;
import com.giftshop.service.CategoryService;
import com.giftshop.service.SettingService;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds data needed by the shared header/footer to every page:
 * shop settings, the category menu, and the cart item count.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final SettingService settingService;
    private final CategoryService categoryService;
    private final CartService cartService;

    public GlobalControllerAdvice(SettingService settingService,
                                  CategoryService categoryService,
                                  CartService cartService) {
        this.settingService = settingService;
        this.categoryService = categoryService;
        this.cartService = cartService;
    }

    @ModelAttribute("shop")
    public java.util.Map<String, String> shop() {
        return settingService.all();
    }

    @ModelAttribute("navCategories")
    public Object navCategories() {
        return categoryService.all();
    }

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.getItemCount();
    }

    /**
     * Treat empty form fields (e.g. blank "old price" or "no category")
     * as null instead of failing to convert an empty string to a number.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
    }
}
