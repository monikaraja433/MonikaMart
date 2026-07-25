package com.monika.monikamart.dao;

import com.monika.monikamart.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Product create(Product product);
    Optional<Product> findById(int id);
    List<Product> findAll(boolean activeOnly);
    List<Product> findBySellerId(int sellerId);
    List<Product> search(String keyword, String category, String sortBy, int limit, int offset);
    int countSearch(String keyword, String category);
    List<String> findAllCategories();
    boolean update(Product product);
    boolean updateStock(int productId, int quantityChange);
    boolean delete(int id);
    boolean setStatus(int id, boolean active);
    int countProducts();
}
