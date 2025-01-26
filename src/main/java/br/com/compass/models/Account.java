package br.com.compass.models;

import java.math.BigDecimal;

public class Account {
    private Long id;
    private String accountType;  
    private BigDecimal balance;
    private Long userId; 
    
    
	public Account(Long id, String accountType, BigDecimal balance, Long userId) {
		super();
		this.id = id;
		this.accountType = accountType;
		this.balance = balance;
		this.userId = userId;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
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


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	
}