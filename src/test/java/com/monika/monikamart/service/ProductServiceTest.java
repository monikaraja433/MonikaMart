package com.monika.monikamart.service;

import com.monika.monikamart.dao.ProductDAO;
import com.monika.monikamart.dto.ProductDTO;
import com.monika.monikamart.exception.UnauthorizedException;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.Product;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ProductServiceTest {
    private ProductDAO productDAO;
    private ProductService productService;

    @BeforeEach
    public void setup() {
        this.productDAO = Mockito.mock(ProductDAO.class);
        this.productService = new ProductService(productDAO);
    }

    @Test
    public void testCreateProductSuccess() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Wireless Mouse");
        dto.setDescription("High speed optical mouse");
        dto.setPrice(new BigDecimal("799.00"));
        dto.setStockQty(50);
        dto.setCategory("Electronics");

        Mockito.when(productDAO.create(Mockito.any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(10);
            return p;
        });

        Product created = productService.createProduct(dto, 2);
        Assertions.assertNotNull(created);
        Assertions.assertEquals(10, created.getId());
        Assertions.assertEquals(2, created.getSellerId());
    }

    @Test
    public void testRejectInvalidPrice() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Bad Product");
        dto.setDescription("Description");
        dto.setPrice(new BigDecimal("-50.00"));
        dto.setStockQty(5);
        dto.setCategory("Electronics");

        Assertions.assertThrows(ValidationException.class, () -> productService.createProduct(dto, 2));
    }

    @Test
    public void testRejectUnauthorizedSellerUpdate() {
        Product p = new Product();
        p.setId(15);
        p.setSellerId(2); // Owned by seller 2

        Mockito.when(productDAO.findById(15)).thenReturn(Optional.of(p));

        ProductDTO updateDto = new ProductDTO();
        updateDto.setId(15);
        updateDto.setName("Hacked Name");
        updateDto.setPrice(new BigDecimal("100.00"));
        updateDto.setStockQty(1);
        updateDto.setCategory("Electronics");

        // Attempt update by seller 3
        Assertions.assertThrows(UnauthorizedException.class, () -> productService.updateProduct(updateDto, 3));
    }
}
