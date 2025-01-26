package br.com.compass.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private String transactionType; 
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private Long sourceAccountId; 
    private Long destinationAccountId;


}
