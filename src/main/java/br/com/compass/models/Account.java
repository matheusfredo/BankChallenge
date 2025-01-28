package br.com.compass.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private Long accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String userCpf;
    private List<String> transactionHistory; 

    public Account(Long accountNumber, String accountType, BigDecimal balance, String userCpf) {
        this.accountNumber = accountNumber;
        setAccountType(accountType);
        setBalance(balance);
        this.userCpf = userCpf;
        this.transactionHistory = new ArrayList<>();
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
        List<String> validTypes = List.of("Checking Account", "Salary Account", "Savings Account");
        if (!validTypes.contains(accountType)) {
            throw new IllegalArgumentException("Invalid account type. Must be one of: " + validTypes);
        }
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
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
        if (transactionHistory.size() >= 1000) {
            transactionHistory.remove(0);
        }
        String timestamp = LocalDateTime.now().toString(); 
        this.transactionHistory.add(timestamp + " - " + transaction);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", userCpf='" + userCpf + '\'' +
                '}';
    }
}
