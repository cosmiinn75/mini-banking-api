package com.cosmin.mini_banking_api.Model;


import com.cosmin.mini_banking_api.Enum.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private Integer number_of_accounts;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;


    public User() {}

    public User(Long id, String email, String password,String username) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public Integer getNumber_of_accounts() {
        return number_of_accounts;
    }

    public void setNumber_of_accounts(Integer number_of_accounts) {
        this.number_of_accounts = number_of_accounts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
