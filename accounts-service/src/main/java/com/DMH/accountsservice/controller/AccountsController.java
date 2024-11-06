package com.DMH.accountsservice.controller;

import com.DMH.accountsservice.dto.*;
import com.DMH.accountsservice.entities.*;
import com.DMH.accountsservice.exceptions.ResourceNotFoundException;
import com.DMH.accountsservice.repository.AccountsRepository;
import com.DMH.accountsservice.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountsController {
    @Autowired
    private AccountsService accountsService;
    private final AccountsRepository accountsRepository;
    private final WebClient webClient;

    @Autowired
    public AccountsController(AccountsRepository accountsRepository, WebClient.Builder webClientBuilder) {
        this.accountsRepository = accountsRepository;
        this.webClient = webClientBuilder.baseUrl("http://gateway-service:8084").build();
    }

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountCreationRequest request) {
        AccountResponse response = accountsService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountResponse> getAccountSummary(@PathVariable Long id) throws ResourceNotFoundException {
        AccountResponse accountResponse = accountsService.getAccountSummary(id);
        return ResponseEntity.ok(accountResponse);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<Transaction>> getLastTransactions(@PathVariable Long id) {
        List<Transaction> transactions = accountsService.getLastTransactions(id);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accounts = accountsService.getAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id) throws ResourceNotFoundException {
        AccountDTO account = accountsService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody AccountDTO accountDto) throws ResourceNotFoundException {
        AccountDTO updatedAccount = accountsService.updateAccount(id, accountDto);
        return ResponseEntity.ok(updatedAccount);
    }

    @PatchMapping("/update/alias/{id}")
    public Mono<ResponseEntity<Map<String, String>>> updateAlias(@PathVariable Long id, @RequestBody AccountUpdateRequest request) {
        accountsRepository.updateAlias(id, request.getAlias());
        return webClient.patch()
                .uri("/users/update/alias/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> ResponseEntity.ok(Collections.singletonMap("message", "Alias actualizado exitosamente")))
                .onErrorResume(e -> {
                    return Mono.just(ResponseEntity.status(500).body(Collections.singletonMap("error", "Error al actualizar el alias en el servicio de usuarios: " + e.getMessage())));
                });
    }

}
