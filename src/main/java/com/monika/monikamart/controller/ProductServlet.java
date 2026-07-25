package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.OrderDAOImpl;
import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.dao.impl.ReviewDAOImpl;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.model.Product;
import com.monika.monikamart.model.Review;
import com.monika.monikamart.service.ProductService;
import com.monika.monikamart.service.ReviewService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"", "/products", "/product-detail"})
public class ProductServlet extends HttpServlet {
    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() {
        this.productService = new ProductService(new ProductDAOImpl());
        this.reviewService = new ReviewService(new ReviewDAOImpl(), new OrderDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/product-detail".equals(path)) {
            handleProductDetail(req, resp);
        } else {
            handleProductList(req, resp);
        }
    }

    private void handleProductList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String category = req.getParameter("category");
        String sortBy = req.getParameter("sortBy");
        String pageStr = req.getParameter("page");

        int page = 1;
        if (pageStr != null) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }
        int pageSize = 9;

        List<Product> products = productService.searchProducts(keyword, category, sortBy, page, pageSize);
        int totalProducts = productService.countSearchResults(keyword, category);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        List<String> categories = productService.getAllCategories();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("selectedCategory", category);
        req.setAttribute("keyword", keyword);
        req.setAttribute("sortBy", sortBy);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalProducts", totalProducts);

        req.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(req, resp);
    }

    private void handleProductDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        try {
            int productId = Integer.parseInt(idStr);
            Product product = productService.getProductById(productId);
            List<Review> reviews = reviewService.getReviewsForProduct(productId);
            double avgRating = reviewService.getAverageRating(productId);

            req.setAttribute("product", product);
            req.setAttribute("reviews", reviews);
            req.setAttribute("avgRating", avgRating);

            req.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/products?error=ProductNotFound");
        }
    }
}
