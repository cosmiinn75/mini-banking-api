package com.cosmin.mini_banking_api.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private BigDecimal balance;

    private Integer accountNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public BankAccount(Long id, String name, BigDecimal sold, Integer account_number, User user) {
        this.id = id;
        this.name = name;
        this.balance = sold;
        this.accountNumber = account_number;
        this.user = user;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer account_number) {
        this.accountNumber = account_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BankAccount() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
