package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.CartDAOImpl;
import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.dto.ApiResponse;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.CartItem;
import com.monika.monikamart.service.CartService;
import com.monika.monikamart.util.JsonUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/cart", "/cart/add", "/cart/update", "/cart/remove", "/cart/clear"})
public class CartServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init() {
        this.cartService = new CartService(new CartDAOImpl(), new ProductDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserResponseDTO user = (UserResponseDTO) session.getAttribute("user");

        List<CartItem> cartItems = cartService.getCartForUser(user.getId());
        BigDecimal total = cartService.calculateCartTotal(cartItems);

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("cartTotal", total);

        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        UserResponseDTO user = (UserResponseDTO) session.getAttribute("user");

        try {
            if ("/cart/add".equals(path)) {
                int productId = Integer.parseInt(req.getParameter("productId"));
                int quantity = 1;
                String qtyParam = req.getParameter("quantity");
                if (qtyParam != null && !qtyParam.trim().isEmpty()) {
                    quantity = Integer.parseInt(qtyParam);
                }

                cartService.addToCart(user.getId(), productId, quantity);

                String isAjax = req.getHeader("X-Requested-With");
                if ("XMLHttpRequest".equalsIgnoreCase(isAjax)) {
                    resp.setContentType("application/json");
                    JsonUtil.writeJson(resp.getWriter(), ApiResponse.success(cartService.getCartCount(user.getId()), "Product added to cart successfully!"));
                    return;
                }

                session.setAttribute("flashSuccess", "Product added to cart successfully!");
                resp.sendRedirect(req.getContextPath() + "/cart?added=true");
            } else if ("/cart/update".equals(path)) {
                int cartItemId = Integer.parseInt(req.getParameter("cartItemId"));
                int quantity = Integer.parseInt(req.getParameter("quantity"));

                cartService.updateQuantity(cartItemId, user.getId(), quantity);
                session.setAttribute("flashSuccess", "Cart updated successfully!");
                resp.sendRedirect(req.getContextPath() + "/cart");
            } else if ("/cart/remove".equals(path)) {
                int cartItemId = Integer.parseInt(req.getParameter("cartItemId"));
                cartService.removeItem(cartItemId, user.getId());
                session.setAttribute("flashSuccess", "Item removed from cart successfully!");
                resp.sendRedirect(req.getContextPath() + "/cart");
            } else if ("/cart/clear".equals(path)) {
                cartService.clearCart(user.getId());
                session.setAttribute("flashSuccess", "Cart cleared successfully!");
                resp.sendRedirect(req.getContextPath() + "/cart");
            }
        } catch (ValidationException e) {
            String isAjax = req.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equalsIgnoreCase(isAjax)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                JsonUtil.writeJson(resp.getWriter(), ApiResponse.error(e.getMessage()));
                return;
            }
            session.setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cart");
        } catch (Exception e) {
            session.setAttribute("flashError", "Cart operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cart?error=ActionFailed");
        }
    }
}
