package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.CartDAOImpl;
import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.dao.impl.WishlistDAOImpl;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.exception.ResourceNotFoundException;
import com.monika.monikamart.model.WishlistItem;
import com.monika.monikamart.service.CartService;
import com.monika.monikamart.service.ProductService;
import com.monika.monikamart.service.WishlistService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/wishlist", "/wishlist/add", "/wishlist/remove", "/wishlist/move-to-cart"})
public class WishlistServlet extends HttpServlet {
    private WishlistService wishlistService;
    private CartService cartService;
    private ProductService productService;

    @Override
    public void init() {
        ProductDAOImpl productDAO = new ProductDAOImpl();
        this.wishlistService = new WishlistService(new WishlistDAOImpl());
        this.cartService = new CartService(new CartDAOImpl(), productDAO);
        this.productService = new ProductService(productDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserResponseDTO user = (UserResponseDTO) session.getAttribute("user");

        List<WishlistItem> items = wishlistService.getWishlist(user.getId());
        req.setAttribute("wishlistItems", items);
        req.getRequestDispatcher("/WEB-INF/views/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        UserResponseDTO user = (UserResponseDTO) session.getAttribute("user");

        try {
            int productId = Integer.parseInt(req.getParameter("productId"));

            if ("/wishlist/add".equals(path)) {
                productService.getProductById(productId);
                wishlistService.addToWishlist(user.getId(), productId);
                session.setAttribute("flashSuccess", "Product saved to your wishlist!");
                resp.sendRedirect(req.getContextPath() + "/wishlist?added=true");
            } else if ("/wishlist/remove".equals(path)) {
                wishlistService.removeFromWishlist(user.getId(), productId);
                session.setAttribute("flashSuccess", "Item removed from wishlist.");
                resp.sendRedirect(req.getContextPath() + "/wishlist?removed=true");
            } else if ("/wishlist/move-to-cart".equals(path)) {
                productService.getProductById(productId);
                wishlistService.moveToCart(user.getId(), productId, cartService);
                session.setAttribute("flashSuccess", "Product added to cart successfully!");
                resp.sendRedirect(req.getContextPath() + "/cart?moved=true");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("flashError", "Invalid product ID specified.");
            resp.sendRedirect(req.getContextPath() + "/wishlist");
        } catch (ResourceNotFoundException e) {
            session.setAttribute("flashError", "Requested product was not found.");
            resp.sendRedirect(req.getContextPath() + "/wishlist");
        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage() != null ? e.getMessage() : "Unable to process wishlist request.");
            resp.sendRedirect(req.getContextPath() + "/wishlist");
        }
    }
}
