package com.DMH.accountsservice.dto;

import java.math.BigDecimal;

public class TransferenceDTO {
    private Long cardId;
    private BigDecimal amount;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

