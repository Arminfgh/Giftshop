package com.giftshop.service;

import com.giftshop.model.Category;
import com.giftshop.model.Product;
import com.giftshop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> allActive() {
        return productRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Product> featured() {
        return productRepository.findByActiveTrueAndFeaturedTrueOrderByCreatedAtDesc();
    }

    public List<Product> byCategory(Category category) {
        return productRepository.findByActiveTrueAndCategoryOrderByCreatedAtDesc(category);
    }

    public List<Product> search(String query) {
        return productRepository.findByActiveTrueAndNameContainingIgnoreCaseOrderByCreatedAtDesc(query);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // For the admin panel: every product, including inactive ones.
    public List<Product> allForAdmin() {
        return productRepository.findAll();
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public long count() {
        return productRepository.count();
    }
}
