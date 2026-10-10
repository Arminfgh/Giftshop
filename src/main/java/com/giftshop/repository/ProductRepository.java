package com.giftshop.repository;

import com.giftshop.model.Category;
import com.giftshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrueOrderByCreatedAtDesc();

    List<Product> findByActiveTrueAndFeaturedTrueOrderByCreatedAtDesc();

    List<Product> findByActiveTrueAndCategoryOrderByCreatedAtDesc(Category category);

    List<Product> findByActiveTrueAndNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);

    long countByCategory(Category category);
}
