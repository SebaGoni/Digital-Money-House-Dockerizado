package com.DMH.accountsservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ActivityDTO {
    private Long id;
    private Long accountId;
    private String type; // Tipo de actividad (ejemplo: "pago", "carga", etc.)
    private BigDecimal amount;
    private String date; // Fecha de la actividad
    private String cvu;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCvu() {
        return cvu;
    }

    public void setCvu(String cvu) {
        this.cvu = cvu;
    }
}
