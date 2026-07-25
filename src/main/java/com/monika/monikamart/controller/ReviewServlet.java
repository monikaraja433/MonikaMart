package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.OrderDAOImpl;
import com.monika.monikamart.dao.impl.ReviewDAOImpl;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.service.ReviewService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/reviews/add"})
public class ReviewServlet extends HttpServlet {
    private ReviewService reviewService;

    @Override
    public void init() {
        this.reviewService = new ReviewService(new ReviewDAOImpl(), new OrderDAOImpl());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserResponseDTO user = (UserResponseDTO) session.getAttribute("user");

        String orderIdStr = req.getParameter("orderId");
        String productIdStr = req.getParameter("productId");
        String ratingStr = req.getParameter("rating");
        String comment = req.getParameter("comment");

        try {
            int orderId = Integer.parseInt(orderIdStr);
            int productId = Integer.parseInt(productIdStr);
            int rating = Integer.parseInt(ratingStr);

            reviewService.addReview(orderId, productId, user.getId(), rating, comment);
            resp.sendRedirect(req.getContextPath() + "/order-detail?id=" + orderId + "&reviewSuccess=true");
        } catch (ValidationException e) {
            resp.sendRedirect(req.getContextPath() + "/order-detail?id=" + orderIdStr + "&reviewError=" + e.getMessage());
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/order-detail?id=" + orderIdStr + "&reviewError=Could not submit review");
        }
    }
}
