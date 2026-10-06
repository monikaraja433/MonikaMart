package com.monika.monikamart.service;

import com.monika.monikamart.dao.UserDAO;
import com.monika.monikamart.dto.UserRegistrationDTO;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.exception.AuthenticationException;
import com.monika.monikamart.exception.ResourceNotFoundException;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.Role;
import com.monika.monikamart.model.User;
import com.monika.monikamart.util.PasswordUtil;
import com.monika.monikamart.util.ValidationUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserResponseDTO register(UserRegistrationDTO dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto == null) {
            throw new ValidationException("Registration payload is required");
        }

        // Validate name
        ValidationUtil.validateRequiredString(dto.getName(), "name", 2, 100, errors);

        // Validate email
        if (!ValidationUtil.isValidEmail(dto.getEmail())) {
            errors.put("email", "Invalid email format");
        } else if (userDAO.existsByEmail(dto.getEmail().trim())) {
            errors.put("email", "Email already in use");
        }

        // Validate password
        if (!ValidationUtil.isValidPassword(dto.getPassword())) {
            errors.put("password", "Password must be at least 8 characters with at least one uppercase letter, one lowercase letter, and one number");
        }

        if (dto.getConfirmPassword() == null || !dto.getConfirmPassword().equals(dto.getPassword())) {
            errors.put("confirmPassword", "Passwords do not match");
        }

        // Enforce: Admin is a designated single account only — no admin signup flow!
        String allowedAdminEmail = com.monika.monikamart.util.DBUtil.getProperty("admin.email", "monikaraja433@gmail.com").trim().toLowerCase();
        if (dto.getEmail() != null && dto.getEmail().trim().equalsIgnoreCase(allowedAdminEmail)) {
            errors.put("email", "Admin account cannot be registered");
        }

        Role requestedRole = dto.getRole();
        if (requestedRole == Role.ADMIN) {
            errors.put("role", "Admin accounts cannot be registered publicly");
        } else if (requestedRole == null) {
            requestedRole = Role.BUYER;
        }

        ValidationUtil.checkErrors(errors);

        // Create user entity
        User user = new User();
        user.setName(ValidationUtil.sanitize(dto.getName()));
        user.setEmail(dto.getEmail().toLowerCase().trim());
        user.setPasswordHash(PasswordUtil.hashPassword(dto.getPassword()));
        user.setRole(requestedRole);
        user.setPhone(ValidationUtil.sanitize(dto.getPhone()));
        user.setAddress(ValidationUtil.sanitize(dto.getAddress()));

        User created = userDAO.create(user);
        return UserResponseDTO.fromUser(created);
    }

    public UserResponseDTO authenticate(String email, String plainPassword) {
        Map<String, String> errors = new HashMap<>();
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email is required");
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            errors.put("password", "Password is required");
        }
        ValidationUtil.checkErrors(errors);

        Optional<User> userOpt = userDAO.findByEmail(email.trim());
        if (!userOpt.isPresent()) {
            throw new AuthenticationException("Invalid email or password");
        }

        User user = userOpt.get();

        // Security check: Only the designated single admin email can authenticate as ADMIN,
        // and the Admin password must be read strictly from the ADMIN_PASSWORD environment variable.
        if (user.getRole() == Role.ADMIN) {
            String allowedAdminEmail = com.monika.monikamart.util.DBUtil.getProperty("admin.email", "monikaraja433@gmail.com").trim().toLowerCase();
            if (!user.getEmail().equalsIgnoreCase(allowedAdminEmail)) {
                throw new AuthenticationException("Unauthorized administrator account");
            }
            String envAdminPassword = System.getenv("ADMIN_PASSWORD");
            if (envAdminPassword == null || envAdminPassword.isEmpty() || !plainPassword.equals(envAdminPassword)) {
                throw new AuthenticationException("Invalid email or password");
            }
        } else {
            if (!PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
                throw new AuthenticationException("Invalid email or password");
            }
        }

        return UserResponseDTO.fromUser(user);
    }

    public UserResponseDTO findById(int id) {
        return userDAO.findById(id)
            .map(UserResponseDTO::fromUser)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    public List<UserResponseDTO> getAllUsers() {
        return userDAO.findAll().stream()
            .map(UserResponseDTO::fromUser)
            .collect(Collectors.toList());
    }

    public boolean updateProfile(int id, String name, String phone, String address) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtil.validateRequiredString(name, "name", 2, 100, errors);
        ValidationUtil.checkErrors(errors);

        User user = userDAO.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setName(ValidationUtil.sanitize(name));
        user.setPhone(ValidationUtil.sanitize(phone));
        user.setAddress(ValidationUtil.sanitize(address));

        return userDAO.update(user);
    }

    public int countUsers() {
        return userDAO.countUsers();
    }
}
