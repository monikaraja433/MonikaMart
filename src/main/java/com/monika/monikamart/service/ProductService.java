package com.monika.monikamart.service;

import com.monika.monikamart.dao.ProductDAO;
import com.monika.monikamart.dto.ProductDTO;
import com.monika.monikamart.exception.ResourceNotFoundException;
import com.monika.monikamart.exception.UnauthorizedException;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.Product;
import com.monika.monikamart.util.ValidationUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product createProduct(ProductDTO dto, int sellerId) {
        validateProductDTO(dto);

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(ValidationUtil.sanitize(dto.getName()));
        product.setDescription(ValidationUtil.sanitize(dto.getDescription()));
        product.setPrice(dto.getPrice());
        product.setStockQty(dto.getStockQty());
        product.setCategory(ValidationUtil.sanitize(dto.getCategory()));
        product.setImageUrl(dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty() ? 
            dto.getImageUrl().trim() : "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600");
        product.setActive(true);

        return productDAO.create(product);
    }

    public Product updateProduct(ProductDTO dto, int sellerId) {
        if (dto.getId() == null || dto.getId() <= 0) {
            throw new ValidationException("Product ID is required for update");
        }
        validateProductDTO(dto);

        Product existing = productDAO.findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + dto.getId()));

        if (existing.getSellerId() != sellerId) {
            throw new UnauthorizedException("You are not authorized to edit this product");
        }

        existing.setName(ValidationUtil.sanitize(dto.getName()));
        existing.setDescription(ValidationUtil.sanitize(dto.getDescription()));
        existing.setPrice(dto.getPrice());
        existing.setStockQty(dto.getStockQty());
        existing.setCategory(ValidationUtil.sanitize(dto.getCategory()));
        if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
            existing.setImageUrl(dto.getImageUrl().trim());
        }
        if (dto.getActive() != null) {
            existing.setActive(dto.getActive());
        }

        boolean updated = productDAO.update(existing);
        if (!updated) {
            throw new RuntimeException("Could not update product");
        }
        return existing;
    }

    public boolean deleteProduct(int productId, int sellerId) {
        Product existing = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (existing.getSellerId() != sellerId) {
            throw new UnauthorizedException("You are not authorized to delete this product");
        }
        return productDAO.delete(productId);
    }

    public Product getProductById(int id) {
        return productDAO.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    public List<Product> getProductsBySeller(int sellerId) {
        return productDAO.findBySellerId(sellerId);
    }

    public List<Product> searchProducts(String keyword, String category, String sortBy, int page, int pageSize) {
        int limit = pageSize > 0 ? pageSize : 12;
        int offset = Math.max(0, (page - 1) * limit);
        return productDAO.search(keyword, category, sortBy, limit, offset);
    }

    public int countSearchResults(String keyword, String category) {
        return productDAO.countSearch(keyword, category);
    }

    public List<String> getAllCategories() {
        return productDAO.findAllCategories();
    }

    public List<Product> getAllProductsForAdmin() {
        return productDAO.findAll(false);
    }

    public boolean moderateProductStatus(int productId, boolean active) {
        return productDAO.setStatus(productId, active);
    }

    public int countProducts() {
        return productDAO.countProducts();
    }

    private void validateProductDTO(ProductDTO dto) {
        Map<String, String> errors = new HashMap<>();
        if (dto == null) {
            throw new ValidationException("Product data cannot be empty");
        }

        ValidationUtil.validateRequiredString(dto.getName(), "name", 2, 200, errors);
        ValidationUtil.validatePositivePrice(dto.getPrice(), "price", errors);
        ValidationUtil.validatePositiveQuantity(dto.getStockQty(), "stockQty", errors);
        ValidationUtil.validateRequiredString(dto.getCategory(), "category", 2, 50, errors);

        ValidationUtil.checkErrors(errors);
    }
}
