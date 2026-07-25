package com.monika.monikamart.controller;

import com.monika.monikamart.dao.impl.UserDAOImpl;
import com.monika.monikamart.dto.UserRegistrationDTO;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.exception.AuthenticationException;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.Role;
import com.monika.monikamart.service.UserService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        this.userService = new UserService(new UserDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login?loggedOut=true");
            return;
        }

        if ("/login".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        if ("/register".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        try {
            UserResponseDTO user = userService.authenticate(email, password);

            // Session Fixation Prevention: Invalidate existing session and regenerate session ID
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setMaxInactiveInterval(30 * 60); // 30 minutes explicit timeout
            newSession.setAttribute("user", user);

            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("login") && !redirect.contains("register")) {
                resp.sendRedirect(redirect);
                return;
            }

            // Redirect based on role
            if (user.getRole() == Role.ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else if (user.getRole() == Role.SELLER) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/products");
            }
        } catch (AuthenticationException | ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String roleStr = req.getParameter("role");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");

        Role role = Role.fromString(roleStr);

        UserRegistrationDTO dto = new UserRegistrationDTO(name, email, password, confirmPassword, role, phone, address);

        try {
            UserResponseDTO newUser = userService.register(dto);

            // Automatically log in newly registered user
            HttpSession newSession = req.getSession(true);
            newSession.setMaxInactiveInterval(30 * 60);
            newSession.setAttribute("user", newUser);

            if (newUser.getRole() == Role.SELLER) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/products?registered=true");
            }
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("fieldErrors", e.getFieldErrors());
            req.setAttribute("form", dto);
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
