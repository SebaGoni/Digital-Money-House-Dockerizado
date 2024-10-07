package com.DMH.accountsservice.repository;

import com.DMH.accountsservice.feignCustomExceptions.FeignConfig;
import com.DMH.accountsservice.dto.CreateTransaction;
import com.DMH.accountsservice.entities.Transaction;
import com.DMH.accountsservice.feignCustomExceptions.CustomErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "transactions-service", url = "http://transactions-service:8083/transaction", configuration = {FeignConfig.class, CustomErrorDecoder.class})
public interface FeignTransactionRepository {

    @GetMapping("/lastTransactions/{userId}")
    List<Transaction> getLastFiveTransactions(@PathVariable Long userId);

    @GetMapping("/getAll/{userId}")
    List<Transaction> getAllTransactions(@PathVariable Long userId);

    @GetMapping("/{transactionId}/account/{accountId}")
    Transaction getTransaction(@PathVariable Long accountId, @PathVariable Long transactionId);

    @PostMapping("/create")
    Transaction createTransaction(@RequestBody CreateTransaction transaction);
}
