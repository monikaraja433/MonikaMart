package com.monika.monikamart.dao;

import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductDAOTest extends BaseDAOTest {
    private ProductDAO productDAO;

    @BeforeEach
    public void setup() {
        this.productDAO = new ProductDAOImpl();
    }

    @Test
    public void testFindByIdAndSearch() {
        // Test seeded products
        List<Product> products = productDAO.findAll(true);
        Assertions.assertFalse(products.isEmpty(), "Seeded products should be present");

        Product first = products.get(0);
        Optional<Product> retrieved = productDAO.findById(first.getId());
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(first.getName(), retrieved.get().getName());
    }

    @Test
    public void testCreateProductAndSearch() {
        Product p = new Product();
        p.setSellerId(2); // Seeded seller
        p.setName("Wireless ANC Earbuds Pro " + System.currentTimeMillis());
        p.setDescription("High fidelity audio with transparency mode");
        p.setPrice(new BigDecimal("4999.00"));
        p.setStockQty(20);
        p.setCategory("Electronics");
        p.setImageUrl("https://images.unsplash.com/photo-1590658268037-6bf12165a8df");
        p.setActive(true);

        Product created = productDAO.create(p);
        Assertions.assertTrue(created.getId() > 0);

        List<Product> searchResults = productDAO.search("Earbuds", "Electronics", "price_asc", 10, 0);
        Assertions.assertFalse(searchResults.isEmpty(), "Product should be found via keyword search");
    }

    @Test
    public void testUpdateStock() {
        List<Product> products = productDAO.findAll(true);
        Product target = products.get(0);
        int initialStock = target.getStockQty();

        boolean updated = productDAO.updateStock(target.getId(), -1);
        Assertions.assertTrue(updated);

        Product refreshed = productDAO.findById(target.getId()).orElseThrow();
        Assertions.assertEquals(initialStock - 1, refreshed.getStockQty());
    }
}
