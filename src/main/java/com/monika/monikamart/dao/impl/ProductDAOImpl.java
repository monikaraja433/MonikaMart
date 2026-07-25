package com.monika.monikamart.dao.impl;

import com.monika.monikamart.dao.ProductDAO;
import com.monika.monikamart.exception.DatabaseException;
import com.monika.monikamart.model.Product;
import com.monika.monikamart.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public Product create(Product product) {
        String sql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, product.getSellerId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStockQty());
            ps.setString(6, product.getCategory());
            ps.setString(7, product.getImageUrl());
            ps.setBoolean(8, product.isActive());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                } else {
                    throw new DatabaseException("Creating product failed, no ID obtained.");
                }
            }
            return product;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating product: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> findById(int id) {
        String sql = "SELECT p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.is_active, p.created_at, " +
                     "u.name AS seller_name, " +
                     "COALESCE(AVG(r.rating), 0.0) AS avg_rating, " +
                     "COUNT(r.id) AS review_count " +
                     "FROM products p " +
                     "JOIN users u ON p.seller_id = u.id " +
                     "LEFT JOIN reviews r ON p.id = r.product_id " +
                     "WHERE p.id = ? " +
                     "GROUP BY p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.is_active, p.created_at, u.name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error finding product by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> findAll(boolean activeOnly) {
        String sql = "SELECT p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.is_active, p.created_at, " +
                     "u.name AS seller_name, " +
                     "COALESCE(AVG(r.rating), 0.0) AS avg_rating, " +
                     "COUNT(r.id) AS review_count " +
                     "FROM products p " +
                     "JOIN users u ON p.seller_id = u.id " +
                     "LEFT JOIN reviews r ON p.id = r.product_id " +
                     (activeOnly ? "WHERE p.is_active = TRUE " : "") +
                     "GROUP BY p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.is_active, p.created_at, u.name " +
                     "ORDER BY p.id DESC";

        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all products: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> findBySellerId(int sellerId) {
        String sql = "SELECT p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.is_active, p.created_at, " +
                     "u.name AS seller_name, " +
                     "COALESCE(AVG(r.rating), 0.0) AS avg_rating, " +
                     "COUNT(r.id) AS review_count " +
                     "FROM products p " +
                     "JOIN users u ON p.seller_id = u.id " +
                     "LEFT JOIN reviews r ON p.id = r.product_id " +
                     "WHERE p.seller_id = ? " +
                     "GROUP BY p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.is_active, p.created_at, u.name " +
                     "ORDER BY p.id DESC";

        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding products by seller ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> search(String keyword, String category, String sortBy, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
            "SELECT p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.is_active, p.created_at, " +
            "u.name AS seller_name, " +
            "COALESCE(AVG(r.rating), 0.0) AS avg_rating, " +
            "COUNT(r.id) AS review_count " +
            "FROM products p " +
            "JOIN users u ON p.seller_id = u.id " +
            "LEFT JOIN reviews r ON p.id = r.product_id " +
            "WHERE p.is_active = TRUE "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?) ");
            String kwParam = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kwParam);
            params.add(kwParam);
        }

        if (category != null && !category.trim().isEmpty() && !"All".equalsIgnoreCase(category.trim())) {
            sql.append("AND LOWER(p.category) = LOWER(?) ");
            params.add(category.trim());
        }

        sql.append("GROUP BY p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.is_active, p.created_at, u.name ");

        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.price ASC ");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.price DESC ");
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY avg_rating DESC ");
        } else {
            sql.append("ORDER BY p.id DESC ");
        }

        sql.append("LIMIT ? OFFSET ?");
        params.add(limit > 0 ? limit : 20);
        params.add(Math.max(offset, 0));

        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error searching products: " + e.getMessage(), e);
        }
    }

    @Override
    public int countSearch(String keyword, String category) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products WHERE is_active = TRUE ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(name) LIKE ? OR LOWER(description) LIKE ?) ");
            String kwParam = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kwParam);
            params.add(kwParam);
        }

        if (category != null && !category.trim().isEmpty() && !"All".equalsIgnoreCase(category.trim())) {
            sql.append("AND LOWER(category) = LOWER(?) ");
            params.add(category.trim());
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting products: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> findAllCategories() {
        String sql = "SELECT DISTINCT category FROM products WHERE is_active = TRUE ORDER BY category ASC";
        List<String> categories = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
            return categories;
        } catch (SQLException e) {
            throw new DatabaseException("Error getting product categories: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock_qty = ?, category = ?, image_url = ?, is_active = ? WHERE id = ? AND seller_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStockQty());
            ps.setString(5, product.getCategory());
            ps.setString(6, product.getImageUrl());
            ps.setBoolean(7, product.isActive());
            ps.setInt(8, product.getId());
            ps.setInt(9, product.getSellerId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating product: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStock(int productId, int quantityChange) {
        String sql = "UPDATE products SET stock_qty = stock_qty + ? WHERE id = ? AND (stock_qty + ?) >= 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantityChange);
            ps.setInt(2, productId);
            ps.setInt(3, quantityChange);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating product stock: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        // Soft delete / deactivate
        return setStatus(id, false);
    }

    @Override
    public boolean setStatus(int id, boolean active) {
        String sql = "UPDATE products SET is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, active);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error changing product status: " + e.getMessage(), e);
        }
    }

    @Override
    public int countProducts() {
        String sql = "SELECT COUNT(*) FROM products WHERE is_active = TRUE";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error counting all products: " + e.getMessage(), e);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setSellerId(rs.getInt("seller_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQty(rs.getInt("stock_qty"));
        p.setCategory(rs.getString("category"));
        p.setImageUrl(rs.getString("image_url"));
        p.setActive(rs.getBoolean("is_active"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            p.setSellerName(rs.getString("seller_name"));
        } catch (SQLException ignored) {}
        try {
            p.setAverageRating(rs.getDouble("avg_rating"));
            p.setReviewCount(rs.getInt("review_count"));
        } catch (SQLException ignored) {}
        return p;
    }
}
