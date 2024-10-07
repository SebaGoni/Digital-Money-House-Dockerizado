package com.DMH.accountsservice.repository;

import com.DMH.accountsservice.feignCustomExceptions.FeignConfig;
import com.DMH.accountsservice.entities.Card;
import com.DMH.accountsservice.feignCustomExceptions.CustomErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "cards-service", url = "http://cards-service:8085/card", configuration = {FeignConfig.class, CustomErrorDecoder.class})
public interface FeignCardRepository {

    @PostMapping("/register-card")
    Card registerCard(@RequestBody Card card);

    @GetMapping("/{id}/all-cards")
    List<Card> getAllCardsByAccountId(@PathVariable Long id);

    @GetMapping("/{accountId}/card/{cardId}")
    Card getCardByIdAndAccountId (@PathVariable Long accountId, @PathVariable Long cardId);

    @DeleteMapping("/{accountId}/card/{cardNumber}")
    void deleteCard(@PathVariable Long accountId, @PathVariable String cardNumber);

    @GetMapping("/{accountId}/cardNumber/{cardNumber}")
    Card getCardByNumberAndAccountId (@PathVariable Long accountId, @PathVariable String cardNumber);

}
