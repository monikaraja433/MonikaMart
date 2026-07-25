package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.CartDAOImpl;
import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.dao.impl.WishlistDAOImpl;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.model.WishlistItem;
import com.monika.monikamart.service.CartService;
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

    @Override
    public void init() {
        this.wishlistService = new WishlistService(new WishlistDAOImpl());
        this.cartService = new CartService(new CartDAOImpl(), new ProductDAOImpl());
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

        if ("/wishlist/add".equals(path)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            wishlistService.addToWishlist(user.getId(), productId);
            resp.sendRedirect(req.getContextPath() + "/wishlist?added=true");
        } else if ("/wishlist/remove".equals(path)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            wishlistService.removeFromWishlist(user.getId(), productId);
            resp.sendRedirect(req.getContextPath() + "/wishlist?removed=true");
        } else if ("/wishlist/move-to-cart".equals(path)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            wishlistService.moveToCart(user.getId(), productId, cartService);
            resp.sendRedirect(req.getContextPath() + "/cart?moved=true");
        }
    }
}
