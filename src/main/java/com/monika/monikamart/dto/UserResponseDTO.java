package com.monika.monikamart.dto;

import com.monika.monikamart.model.Role;
import com.monika.monikamart.model.User;
import java.sql.Timestamp;

public class UserResponseDTO {
    private int id;
    private String name;
    private String email;
    private Role role;
    private String phone;
    private String address;
    private Timestamp createdAt;

    public UserResponseDTO() {}

    public UserResponseDTO(int id, String name, String email, Role role, String phone, String address, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }

    public static UserResponseDTO fromUser(User user) {
        if (user == null) return null;
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getPhone(),
            user.getAddress(),
            user.getCreatedAt()
        );
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
