package br.com.compass.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private Long accountNumber; 
    private String accountType; 
    private BigDecimal balance; 
    private String userCpf; 
    private List<String> transactionHistory = new ArrayList<>(); 

    public Account(Long accountNumber, String accountType, BigDecimal balance, String userCpf) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.userCpf = userCpf;
    }

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

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void addTransaction(String transaction) {
        this.transactionHistory.add(transaction);
    }
}
