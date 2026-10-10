package com.giftshop.config;

import com.giftshop.model.Category;
import com.giftshop.model.Product;
import com.giftshop.repository.CategoryRepository;
import com.giftshop.repository.ProductRepository;
import com.giftshop.service.SettingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SettingService settingService;

    public DataInitializer(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           SettingService settingService) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.settingService = settingService;
    }

    @Override
    public void run(String... args) {
        // 1) Make sure all settings exist
        settingService.initDefaults();

        // 2) Seed categories the first time only (souvenirs by place)
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("Iran", "🇮🇷"));
            categoryRepository.save(new Category("Turkey", "🇹🇷"));
            categoryRepository.save(new Category("Italy", "🇮🇹"));
            categoryRepository.save(new Category("France", "🇫🇷"));
            categoryRepository.save(new Category("Japan", "🇯🇵"));
            categoryRepository.save(new Category("Morocco", "🇲🇦"));
        }

        // 3) Seed a few sample souvenirs the first time only
        if (productRepository.count() == 0) {
            Category iran = categoryRepository.findByName("Iran").orElse(null);
            Category turkey = categoryRepository.findByName("Turkey").orElse(null);
            Category italy = categoryRepository.findByName("Italy").orElse(null);
            Category france = categoryRepository.findByName("France").orElse(null);
            Category japan = categoryRepository.findByName("Japan").orElse(null);
            Category morocco = categoryRepository.findByName("Morocco").orElse(null);

            productRepository.save(sample(
                    "Persian Saffron — Superior Grade (1g)",
                    "Premium Sargol saffron from Khorasan, Iran. Rich aroma and deep color — a little goes a long way. Comes in a keepsake gift box.",
                    18L, 24L, iran, true, "https://images.unsplash.com/photo-1615885108069-7d5bef9a7e22?w=600&h=600&fit=crop"));

            productRepository.save(sample(
                    "Handwoven Persian Miniature Rug",
                    "A small hand-knotted rug woven with traditional Persian patterns. A timeless piece of Iranian craftsmanship for your home.",
                    145L, 175L, iran, true, "https://images.unsplash.com/photo-1600166898405-da9535204843?w=600&h=600&fit=crop"));

            productRepository.save(sample(
                    "Isfahan Enamel (Mina Kari) Bowl",
                    "Hand-painted copper bowl in the famous blue Mina Kari style of Isfahan. Each piece is unique and made by local artisans.",
                    60L, null, iran, true, "https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=600&h=600&fit=crop"));

            productRepository.save(sample(
                    "Turquoise & Silver Ring (Neyshabur)",
                    "Genuine Neyshabur turquoise set in sterling silver — one of Iran's most treasured gemstones.",
                    48L, null, iran, false, "https://images.unsplash.com/photo-1611591437281-460bfbe1220a?w=600&h=600&fit=crop"));

            productRepository.save(sample(
                    "Turkish Delight Assortment",
                    "A colorful box of authentic Turkish delight with pistachio, rose and pomegranate flavors.",
                    16L, null, turkey, true, "https://images.unsplash.com/photo-1576618148400-f54bed99fcfd?w=600&h=600&fit=crop"));

            productRepository.save(sample(
                    "Venetian Murano Glass Pendant",
                    "Hand-blown Murano glass pendant from Venice, Italy. Vibrant colors, one-of-a-kind.",
                    34L, 42L, italy, false, "https://images.unsplash.com/photo-1602752250015-52934bc45613?w=600&h=600&fit=crop"));

            productRepository.save(sample(
                    "Provence Lavender Sachet Set",
                    "Set of three sachets filled with fragrant dried lavender from the fields of Provence, France.",
                    14L, null, france, false, "https://images.unsplash.com/photo-1595235060109-32bcd3b9c9f2?w=600&h=600&fit=crop"));

            productRepository.save(sample(
                    "Japanese Furoshiki Wrapping Cloth",
                    "Traditional reusable Japanese cloth for wrapping gifts and carrying items. Elegant printed design.",
                    20L, null, japan, false, "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=600&h=600&fit=crop"));

            productRepository.save(sample(
                    "Moroccan Argan Oil (50ml)",
                    "Pure, cold-pressed argan oil from Morocco — a prized natural treasure for skin and hair.",
                    19L, null, morocco, true, "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=600&h=600&fit=crop"));
        }

        // 4) Backfill: give any existing product that has no image a matching photo.
        //    (Keeps images showing even if products were created before images existed.)
        for (Product p : productRepository.findAll()) {
            if (p.getImageUrl() == null || p.getImageUrl().isBlank()) {
                String url = imageFor(p.getName());
                if (url != null) {
                    p.setImageUrl(url);
                    productRepository.save(p);
                }
            }
        }
    }

    // Returns a stock photo URL for a known sample product name, or null if unknown.
    private String imageFor(String name) {
        if (name == null) return null;
        switch (name) {
            case "Persian Saffron — Superior Grade (1g)": return "https://images.unsplash.com/photo-1615885108069-7d5bef9a7e22?w=600&h=600&fit=crop";
            case "Handwoven Persian Miniature Rug":        return "https://images.unsplash.com/photo-1600166898405-da9535204843?w=600&h=600&fit=crop";
            case "Isfahan Enamel (Mina Kari) Bowl":        return "https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=600&h=600&fit=crop";
            case "Turquoise & Silver Ring (Neyshabur)":    return "https://images.unsplash.com/photo-1611591437281-460bfbe1220a?w=600&h=600&fit=crop";
            case "Turkish Delight Assortment":             return "https://images.unsplash.com/photo-1576618148400-f54bed99fcfd?w=600&h=600&fit=crop";
            case "Venetian Murano Glass Pendant":          return "https://images.unsplash.com/photo-1602752250015-52934bc45613?w=600&h=600&fit=crop";
            case "Provence Lavender Sachet Set":           return "https://images.unsplash.com/photo-1595235060109-32bcd3b9c9f2?w=600&h=600&fit=crop";
            case "Japanese Furoshiki Wrapping Cloth":      return "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=600&h=600&fit=crop";
            case "Moroccan Argan Oil (50ml)":              return "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=600&h=600&fit=crop";
            default: return null;
        }
    }

    private Product sample(String name, String desc, Long price, Long oldPrice,
                           Category category, boolean featured, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setOldPrice(oldPrice);
        p.setCategory(category);
        p.setStock(10);
        p.setFeatured(featured);
        p.setActive(true);
        p.setImageUrl(imageUrl);
        return p;
    }
}
