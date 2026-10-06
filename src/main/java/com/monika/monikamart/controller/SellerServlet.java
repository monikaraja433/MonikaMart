package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.ActivityDAOImpl;
import com.monika.monikamart.dao.impl.CartDAOImpl;
import com.monika.monikamart.dao.impl.OrderDAOImpl;
import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.dto.ProductDTO;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.Order;
import com.monika.monikamart.model.OrderStatus;
import com.monika.monikamart.model.Product;
import com.monika.monikamart.service.ActivityService;
import com.monika.monikamart.service.OrderService;
import com.monika.monikamart.service.ProductService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {
    "/seller/dashboard",
    "/seller/products",
    "/seller/product-form",
    "/seller/product-save",
    "/seller/product-delete",
    "/seller/orders",
    "/seller/status-update"
})
public class SellerServlet extends HttpServlet {
    private ProductService productService;
    private OrderService orderService;
    private ActivityService activityService;

    @Override
    public void init() {
        ProductDAOImpl productDAO = new ProductDAOImpl();
        OrderDAOImpl orderDAO = new OrderDAOImpl();
        CartDAOImpl cartDAO = new CartDAOImpl();
        this.productService = new ProductService(productDAO);
        this.orderService = new OrderService(orderDAO, cartDAO, productDAO);
        this.activityService = new ActivityService(new ActivityDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        UserResponseDTO seller = (UserResponseDTO) session.getAttribute("user");

        if ("/seller/dashboard".equals(path)) {
            List<Product> products = productService.getProductsBySeller(seller.getId());
            List<Order> orders = orderService.getOrdersForSeller(seller.getId());

            BigDecimal totalSales = BigDecimal.ZERO;
            for (Order o : orders) {
                if (o.getStatus() != OrderStatus.CANCELLED) {
                    totalSales = totalSales.add(o.getTotalAmount());
                }
            }

            req.setAttribute("products", products);
            req.setAttribute("orders", orders);
            req.setAttribute("productCount", products.size());
            req.setAttribute("orderCount", orders.size());
            req.setAttribute("totalSales", totalSales);

            req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
        } else if ("/seller/products".equals(path)) {
            List<Product> products = productService.getProductsBySeller(seller.getId());
            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
        } else if ("/seller/product-form".equals(path)) {
            String idStr = req.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                int id = Integer.parseInt(idStr);
                Product product = productService.getProductById(id);
                req.setAttribute("product", product);
            }
            req.setAttribute("categories", productService.getAllCategories());
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        } else if ("/seller/orders".equals(path)) {
            List<Order> orders = orderService.getOrdersForSeller(seller.getId());
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/seller/orders.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        UserResponseDTO seller = (UserResponseDTO) session.getAttribute("user");

        if ("/seller/product-save".equals(path)) {
            String idStr = req.getParameter("id");
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String priceStr = req.getParameter("price");
            String stockStr = req.getParameter("stockQty");
            String category = req.getParameter("category");
            String imageUrl = req.getParameter("imageUrl");

            ProductDTO dto = new ProductDTO();
            if (idStr != null && !idStr.trim().isEmpty()) {
                dto.setId(Integer.parseInt(idStr));
            }
            dto.setName(name);
            dto.setDescription(description);
            try {
                dto.setPrice(new BigDecimal(priceStr));
                dto.setStockQty(Integer.parseInt(stockStr));
            } catch (Exception ignored) {}
            dto.setCategory(category);
            dto.setImageUrl(imageUrl);

            try {
                if (dto.getId() != null) {
                    Product updated = productService.updateProduct(dto, seller.getId());
                    activityService.logActivity("PRODUCT_UPDATED", "Product updated: '" + dto.getName() + "' (ID: #" + dto.getId() + ") by " + seller.getName() + " (" + seller.getEmail() + ")", seller.getEmail());
                    session.setAttribute("flashSuccess", "Product updated successfully!");
                } else {
                    Product created = productService.createProduct(dto, seller.getId());
                    activityService.logActivity("PRODUCT_ADDED", "Product added: '" + created.getName() + "' by " + seller.getName() + " (" + seller.getEmail() + ") - Price: ₹" + created.getPrice(), seller.getEmail());
                    session.setAttribute("flashSuccess", "Product added successfully!");
                }
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard?success=ProductSaved");
            } catch (ValidationException e) {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("fieldErrors", e.getFieldErrors());
                req.setAttribute("product", dto);
                req.setAttribute("categories", productService.getAllCategories());
                req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
            }
        } else if ("/seller/product-delete".equals(path)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            try {
                productService.deleteProduct(productId, seller.getId());
                activityService.logActivity("PRODUCT_DELETED", "Product deleted: ID #" + productId + " by " + seller.getName() + " (" + seller.getEmail() + ")", seller.getEmail());
                session.setAttribute("flashSuccess", "Product deleted successfully!");
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard?success=ProductDeleted");
            } catch (Exception e) {
                session.setAttribute("flashError", "Failed to delete product: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            }
        } else if ("/seller/status-update".equals(path)) {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String statusStr = req.getParameter("newStatus");
            String notes = req.getParameter("notes");
            OrderStatus newStatus = OrderStatus.fromString(statusStr);

            try {
                orderService.updateOrderStatus(orderId, newStatus, notes, seller.getId(), seller.getRole());
                activityService.logActivity("ORDER_STATUS", "Order #MKM-" + orderId + " status updated to " + newStatus + " by " + seller.getEmail(), seller.getEmail());
                session.setAttribute("flashSuccess", "Order status updated successfully!");
                resp.sendRedirect(req.getContextPath() + "/seller/orders?success=StatusUpdated");
            } catch (ValidationException e) {
                session.setAttribute("flashError", e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/seller/orders?error=" + e.getMessage());
            } catch (Exception e) {
                session.setAttribute("flashError", "Failed to update order status: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/seller/orders?error=StatusUpdateFailed");
            }
        }
    }
}
