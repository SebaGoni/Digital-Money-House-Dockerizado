package com.DMH.accountsservice.dto;

import lombok.Data;

@Data
public class DepositMoneyRequest {
    private String cardNumber;
    private Double amount;
}
