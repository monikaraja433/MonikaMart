package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.CartDAOImpl;
import com.monika.monikamart.dao.impl.OrderDAOImpl;
import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.dao.impl.UserDAOImpl;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.model.Order;
import com.monika.monikamart.model.Product;
import com.monika.monikamart.service.OrderService;
import com.monika.monikamart.service.ProductService;
import com.monika.monikamart.service.UserService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/admin/dashboard", "/admin/users", "/admin/listings", "/admin/moderate"})
public class AdminServlet extends HttpServlet {
    private UserService userService;
    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() {
        this.userService = new UserService(new UserDAOImpl());
        ProductDAOImpl productDAO = new ProductDAOImpl();
        this.productService = new ProductService(productDAO);
        this.orderService = new OrderService(new OrderDAOImpl(), new CartDAOImpl(), productDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/admin/dashboard".equals(path)) {
            int totalUsers = userService.countUsers();
            int totalProducts = productService.countProducts();
            int totalOrders = orderService.countOrders();
            BigDecimal totalRevenue = orderService.getTotalRevenue();
            List<Order> recentOrders = orderService.getAllOrdersForAdmin();

            req.setAttribute("totalUsers", totalUsers);
            req.setAttribute("totalProducts", totalProducts);
            req.setAttribute("totalOrders", totalOrders);
            req.setAttribute("totalRevenue", totalRevenue);
            req.setAttribute("recentOrders", recentOrders);

            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
        } else if ("/admin/users".equals(path)) {
            List<UserResponseDTO> users = userService.getAllUsers();
            req.setAttribute("users", users);
            req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
        } else if ("/admin/listings".equals(path)) {
            List<Product> products = productService.getAllProductsForAdmin();
            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/views/admin/listings.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/admin/moderate".equals(path)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            boolean active = Boolean.parseBoolean(req.getParameter("active"));
            productService.moderateProductStatus(productId, active);
            resp.sendRedirect(req.getContextPath() + "/admin/listings?success=StatusUpdated");
        }
    }
}
