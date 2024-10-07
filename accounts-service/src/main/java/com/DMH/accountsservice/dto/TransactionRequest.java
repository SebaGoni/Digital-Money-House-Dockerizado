package com.DMH.accountsservice.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String destinyAccount;
    private Double amount;
}
