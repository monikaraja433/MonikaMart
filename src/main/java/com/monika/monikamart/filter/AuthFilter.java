package com.monika.monikamart.filter;

import com.monika.monikamart.dto.ApiResponse;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.model.Role;
import com.monika.monikamart.util.JsonUtil;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/seller/*", "/admin/*", "/cart/*", "/checkout/*", "/orders/*", "/wishlist/*", "/reviews/add"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        UserResponseDTO currentUser = (session != null) ? (UserResponseDTO) session.getAttribute("user") : null;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String relativePath = uri.substring(contextPath.length());

        // Check if user is logged in
        if (currentUser == null) {
            if (relativePath.startsWith("/api/")) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                JsonUtil.writeJson(res.getWriter(), ApiResponse.error("Authentication required. Please log in."));
                return;
            }
            res.sendRedirect(contextPath + "/login?redirect=" + req.getRequestURI());
            return;
        }

        // Role-based authorization checks
        if (relativePath.startsWith("/admin")) {
            if (currentUser.getRole() != Role.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin privileges required.");
                return;
            }
        } else if (relativePath.startsWith("/seller")) {
            if (currentUser.getRole() != Role.SELLER && currentUser.getRole() != Role.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Seller account required.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
