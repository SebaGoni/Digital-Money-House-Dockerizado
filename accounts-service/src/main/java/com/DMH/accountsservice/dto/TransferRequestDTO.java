package com.DMH.accountsservice.dto;

import java.math.BigDecimal;

public class TransferRequestDTO {
    private String recipient; // CVU o alias
    private BigDecimal amount; // Monto a transferir

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

