package com.DMH.accountsservice.dto;

import java.math.BigDecimal;

public class RecipientDTO {
    private String recipient; // CBU/CVU/alias
    private BigDecimal lastAmount; // Último monto transferido

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getLastAmount() {
        return lastAmount;
    }

    public void setLastAmount(BigDecimal lastAmount) {
        this.lastAmount = lastAmount;
    }
}
