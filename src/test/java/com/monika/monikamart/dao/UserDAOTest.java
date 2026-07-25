package com.monika.monikamart.dao;

import com.monika.monikamart.dao.impl.UserDAOImpl;
import com.monika.monikamart.model.Role;
import com.monika.monikamart.model.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDAOTest extends BaseDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    public void setup() {
        this.userDAO = new UserDAOImpl();
    }

    @Test
    public void testCreateAndFindUser() {
        String email = "testuser_" + System.currentTimeMillis() + "@monikamart.com";
        User user = new User();
        user.setName("Test Student");
        user.setEmail(email);
        user.setPasswordHash("$2a$10$hashedpasswordforexample");
        user.setRole(Role.BUYER);
        user.setPhone("+91 9988776655");
        user.setAddress("Anna University Hostel, Chennai");

        User created = userDAO.create(user);
        Assertions.assertTrue(created.getId() > 0, "Created user should have a generated ID");

        Optional<User> found = userDAO.findById(created.getId());
        Assertions.assertTrue(found.isPresent(), "User should be retrievable by ID");
        Assertions.assertEquals(email, found.get().getEmail());
        Assertions.assertEquals(Role.BUYER, found.get().getRole());
    }

    @Test
    public void testExistsByEmail() {
        Assertions.assertTrue(userDAO.existsByEmail("admin@monikamart.com"), "Seeded admin email should exist");
        Assertions.assertFalse(userDAO.existsByEmail("nonexistent_" + System.currentTimeMillis() + "@gmail.com"));
    }

    @Test
    public void testFindAllUsers() {
        List<User> all = userDAO.findAll();
        Assertions.assertNotNull(all);
        Assertions.assertTrue(all.size() >= 3, "At least 3 seeded users should exist in the database");
    }
}
