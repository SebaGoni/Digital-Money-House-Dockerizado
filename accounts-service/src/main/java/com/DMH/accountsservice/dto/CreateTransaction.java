package com.DMH.accountsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateTransaction {
    private int senderId;
    private int receiverId;
    private Double amountOfMoney;
    private LocalDateTime date;
}
