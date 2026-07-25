package com.monika.monikamart.service;

import com.monika.monikamart.dao.UserDAO;
import com.monika.monikamart.dto.UserRegistrationDTO;
import com.monika.monikamart.dto.UserResponseDTO;
import com.monika.monikamart.exception.AuthenticationException;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.Role;
import com.monika.monikamart.model.User;
import com.monika.monikamart.util.PasswordUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UserServiceTest {
    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        this.userDAO = Mockito.mock(UserDAO.class);
        this.userService = new UserService(userDAO);
    }

    @Test
    public void testSuccessfulRegistration() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "Ananya S",
            "ananya@example.com",
            "SecurePass123",
            "SecurePass123",
            Role.BUYER,
            "+91 9123456780",
            "Chennai"
        );

        Mockito.when(userDAO.existsByEmail("ananya@example.com")).thenReturn(false);
        Mockito.when(userDAO.create(Mockito.any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(101);
            return u;
        });

        UserResponseDTO response = userService.register(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(101, response.getId());
        Assertions.assertEquals("Ananya S", response.getName());
        Assertions.assertEquals("ananya@example.com", response.getEmail());
    }

    @Test
    public void testRejectAdminRegistration() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "Fake Admin",
            "hacker@example.com",
            "SecurePass123",
            "SecurePass123",
            Role.ADMIN,
            null,
            null
        );

        Assertions.assertThrows(ValidationException.class, () -> userService.register(dto));
    }

    @Test
    public void testRejectWeakPassword() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "Weak User",
            "weak@example.com",
            "12345",
            "12345",
            Role.BUYER,
            null,
            null
        );

        Assertions.assertThrows(ValidationException.class, () -> userService.register(dto));
    }

    @Test
    public void testAuthenticateSuccess() {
        User user = new User();
        user.setId(5);
        user.setName("Karthik");
        user.setEmail("karthik@example.com");
        user.setPasswordHash(PasswordUtil.hashPassword("CorrectPass123"));
        user.setRole(Role.BUYER);

        Mockito.when(userDAO.findByEmail("karthik@example.com")).thenReturn(Optional.of(user));

        UserResponseDTO authUser = userService.authenticate("karthik@example.com", "CorrectPass123");
        Assertions.assertNotNull(authUser);
        Assertions.assertEquals(5, authUser.getId());
    }

    @Test
    public void testAuthenticateFailure() {
        User user = new User();
        user.setEmail("karthik@example.com");
        user.setPasswordHash(PasswordUtil.hashPassword("CorrectPass123"));

        Mockito.when(userDAO.findByEmail("karthik@example.com")).thenReturn(Optional.of(user));

        Assertions.assertThrows(AuthenticationException.class, () -> 
            userService.authenticate("karthik@example.com", "WrongPassword")
        );
    }
}
