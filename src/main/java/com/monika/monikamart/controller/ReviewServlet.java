package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.ActivityDAOImpl;
import com.monika.monikamart.dao.impl.OrderDAOImpl;
import com.monika.monikamart.dao.impl.ReviewDAOImpl;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.service.ActivityService;
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
    private ActivityService activityService;

    @Override
    public void init() {
        this.reviewService = new ReviewService(new ReviewDAOImpl(), new OrderDAOImpl());
        this.activityService = new ActivityService(new ActivityDAOImpl());
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

            // Audit log for Admin notification: Review submitted
            activityService.logActivity("REVIEW_SUBMITTED", "Review submitted (" + rating + "★) for Product #" + productId + " by " + user.getName() + " (" + user.getEmail() + ")", user.getEmail());

            session.setAttribute("flashSuccess", "Review submitted successfully!");
            resp.sendRedirect(req.getContextPath() + "/order-detail?id=" + orderId + "&reviewSuccess=true");
        } catch (ValidationException e) {
            session.setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/order-detail?id=" + orderIdStr + "&reviewError=" + e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flashError", "Could not submit review: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/order-detail?id=" + orderIdStr + "&reviewError=Could not submit review");
        }
    }
}
