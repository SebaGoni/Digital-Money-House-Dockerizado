package com.DMH.accountsservice.dto;

import java.math.BigDecimal;

public class AccountResponse {
    private Long Id;
    private BigDecimal balance;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        this.Id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
