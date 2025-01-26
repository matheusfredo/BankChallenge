package br.com.compass.models;

import java.math.BigDecimal;

public class Account {
    private Long accountNumber; // Número único da conta
    private String accountType; // Tipo de conta
    private BigDecimal balance; // Saldo
    private String userCpf; // CPF do dono da conta

    public Account(Long accountNumber, String accountType, BigDecimal balance, String userCpf) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.userCpf = userCpf;
    }

    // Getters e setters
    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getUserCpf() {
        return userCpf;
    }

    public void setUserCpf(String userCpf) {
        this.userCpf = userCpf;
    }
}