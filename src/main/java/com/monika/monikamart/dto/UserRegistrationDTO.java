package com.monika.monikamart.dto;

import com.monika.monikamart.model.Role;

public class UserRegistrationDTO {
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private Role role;
    private String phone;
    private String address;

    public UserRegistrationDTO() {
        this.role = Role.BUYER;
    }

    public UserRegistrationDTO(String name, String email, String password, String confirmPassword, Role role, String phone, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
        this.phone = phone;
        this.address = address;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
