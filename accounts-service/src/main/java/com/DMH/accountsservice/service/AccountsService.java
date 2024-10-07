package com.DMH.accountsservice.service;

import com.DMH.accountsservice.dto.*;
import com.DMH.accountsservice.entities.*;
import com.DMH.accountsservice.exceptions.ResourceNotFoundException;
import com.DMH.accountsservice.repository.AccountsRepository;
import com.DMH.accountsservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountsService {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WebClient webClient;


    public AccountResponse getAccountSummary(Long accountId) throws ResourceNotFoundException {
        // Obtener la cuenta por ID
        Account account = accountsRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Crear respuesta con saldo disponible
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setBalance(account.getBalance());
        return response;
    }

    public List<Transaction> getLastTransactions(Long accountId) {
        // Obtener los últimos 5 movimientos
        return transactionRepository.findTop5ByAccountIdOrderByDateDesc(accountId);
    }

    public AccountDTO getAccountById(Long accountId) throws ResourceNotFoundException {
        // Obtener la cuenta de la base de datos
        Account account = accountsRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Convertir la entidad Account a AccountDTO
        return convertToDTO(account);
    }

    public AccountDTO updateAccount(Long id, AccountDTO accountDto) throws ResourceNotFoundException {
        // Verificar si la cuenta existe
        Account existingAccount = accountsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // Actualizar los campos de la cuenta existente
        existingAccount.setBalance(accountDto.getBalance());
        // Si tienes otros campos en AccountDTO, actualízalos aquí

        // Guardar la cuenta actualizada en la base de datos
        Account updatedAccount = accountsRepository.save(existingAccount);

        // Convertir la entidad actualizada a AccountDTO
        return convertToDTO(updatedAccount);
    }

    // Método para convertir Account a AccountDTO
    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setUserId(account.getUserId());
        dto.setAlias(account.getAlias());
        dto.setCvu(account.getCvu());
        dto.setBalance(account.getBalance());
        // Si tienes transacciones, puedes añadir lógica para incluirlas aquí
        return dto;
    }

    public Account findByEmail(String email) {
        return accountsRepository.findByEmail(email);
    }

    public List<AccountDTO> getAccounts() {
        List<Account> accounts = accountsRepository.findAll(); // Obtener todas las cuentas
        return accounts.stream()
                .map(this::convertToDTO) // Convertir cada Account a AccountDTO
                .collect(Collectors.toList());
    }

    public AccountResponse createAccount(AccountCreationRequest request) {
        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setEmail(request.getEmail());
        account.setAlias(request.getAlias());
        account.setCvu(request.getCvu());
        account.setBalance(request.getInitialBalance());

        // Guarda la cuenta en la base de datos
        account = accountsRepository.save(account);

        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setBalance(account.getBalance());

        return response;
    }

    public Mono<ResponseEntity<Map<String, String>>> updateAlias(Long id, AccountUpdateRequest request) {
        // Actualiza el alias en la tabla de cuentas
        accountsRepository.updateAlias(id, request.getAlias());

        // Realiza la llamada PATCH al servicio de usuarios
        return webClient.patch()
                .uri("/users/update/alias/{id}", id)
                .bodyValue(request) // Asegúrate de que request esté en formato JSON
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> ResponseEntity.ok(Collections.singletonMap("message", "Alias actualizado exitosamente")))
                .onErrorResume(e -> {
                    // Manejo de errores
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Collections.singletonMap("error", "Error al actualizar el alias en el servicio de usuarios: " + e.getMessage())));
                });
    }
}
