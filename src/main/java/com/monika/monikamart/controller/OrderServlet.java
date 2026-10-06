package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.ActivityDAOImpl;
import com.monika.monikamart.dao.impl.CartDAOImpl;
import com.monika.monikamart.dao.impl.OrderDAOImpl;
import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.dao.impl.UserDAOImpl;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.CartItem;
import com.monika.monikamart.model.Order;
import com.monika.monikamart.service.ActivityService;
import com.monika.monikamart.service.CartService;
import com.monika.monikamart.service.OrderService;
import com.monika.monikamart.service.UserService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/checkout", "/checkout/place", "/orders", "/order-detail"})
public class OrderServlet extends HttpServlet {
    private OrderService orderService;
    private CartService cartService;
    private UserService userService;
    private ActivityService activityService;

    @Override
    public void init() {
        OrderDAOImpl orderDAO = new OrderDAOImpl();
        CartDAOImpl cartDAO = new CartDAOImpl();
        ProductDAOImpl productDAO = new ProductDAOImpl();
        UserDAOImpl userDAO = new UserDAOImpl();

        this.orderService = new OrderService(orderDAO, cartDAO, productDAO);
        this.cartService = new CartService(cartDAO, productDAO);
        this.userService = new UserService(userDAO);
        this.activityService = new ActivityService(new ActivityDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        UserResponseDTO user = (UserResponseDTO) session.getAttribute("user");

        if ("/checkout".equals(path)) {
            List<CartItem> cartItems = cartService.getCartForUser(user.getId());
            if (cartItems.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cart?error=CartIsEmpty");
                return;
            }
            BigDecimal total = cartService.calculateCartTotal(cartItems);
            UserResponseDTO freshUser = userService.findById(user.getId());

            req.setAttribute("cartItems", cartItems);
            req.setAttribute("cartTotal", total);
            req.setAttribute("defaultAddress", freshUser.getAddress());
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
        } else if ("/orders".equals(path)) {
            List<Order> orders = orderService.getOrdersForBuyer(user.getId());
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, resp);
        } else if ("/order-detail".equals(path)) {
            String orderIdStr = req.getParameter("id");
            if (orderIdStr == null) {
                resp.sendRedirect(req.getContextPath() + "/orders");
                return;
            }
            try {
                int orderId = Integer.parseInt(orderIdStr);
                Order order = orderService.getOrderById(orderId, user.getId(), user.getRole());
                req.setAttribute("order", order);
                req.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(req, resp);
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/orders?error=" + e.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        UserResponseDTO user = (UserResponseDTO) session.getAttribute("user");

        if ("/checkout/place".equals(path)) {
            String address = req.getParameter("shippingAddress");
            String paymentMethod = req.getParameter("paymentMethod");

            try {
                Order order = orderService.checkout(user.getId(), address, paymentMethod);

                // Audit log for Admin notification: Order placed (Never logs payment credentials)
                activityService.logActivity("ORDER_PLACED", "Order placed: #MKM-" + order.getId() + " by " + user.getName() + " (" + user.getEmail() + ") - Total: ₹" + order.getTotalAmount(), user.getEmail());

                // Set exact required success flash message
                session.setAttribute("flashSuccess", "Order placed successfully!");
                resp.sendRedirect(req.getContextPath() + "/order-detail?id=" + order.getId() + "&success=OrderPlaced");
            } catch (ValidationException e) {
                List<CartItem> cartItems = cartService.getCartForUser(user.getId());
                req.setAttribute("cartItems", cartItems);
                req.setAttribute("cartTotal", cartService.calculateCartTotal(cartItems));
                req.setAttribute("defaultAddress", address);
                req.setAttribute("errorMessage", e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
            } catch (Exception e) {
                req.setAttribute("errorMessage", "Order failed: " + e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
            }
        }
    }
}
