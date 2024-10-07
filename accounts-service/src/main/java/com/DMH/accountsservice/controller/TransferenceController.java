package com.DMH.accountsservice.controller;

import com.DMH.accountsservice.dto.TransferRequestDTO;
import com.DMH.accountsservice.dto.TransferenceDTO;
import com.DMH.accountsservice.entities.Transference;
import com.DMH.accountsservice.exceptions.InsufficientFundsException;
import com.DMH.accountsservice.exceptions.ResourceNotFoundException;
import com.DMH.accountsservice.exceptions.UnauthorizedException;
import com.DMH.accountsservice.service.TransferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/accounts/{accountId}/transferences")
public class TransferenceController {

    @Autowired
    private TransferenceService transferenceService;

    @PostMapping("/cards")
    public ResponseEntity<String> registerTransference(
            @PathVariable Long accountId,
            @RequestBody TransferenceDTO transferenceDto) {

        try {
            transferenceService.registerTransferenceFromCards(accountId, transferenceDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Ingreso registrado con éxito");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta o tarjeta no encontrada");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sin permisos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar el ingreso");
        }
    }

    @PostMapping("/money")
    public ResponseEntity<?> makeTransfer(
            @PathVariable Long accountId,
            @RequestBody TransferRequestDTO transferRequest) {
        try {
            // Realizar la transferencia
            transferenceService.makeTransferFromCash(accountId, transferRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Transferencia realizada con éxito");
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta inexistente");
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.GONE).body("Fondos insuficientes");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permisos insuficientes");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el procesamiento de la transferencia");
        }
    }

    @GetMapping("/last-transferred-accounts")
    public ResponseEntity<List<Transference>> getLastTransferredAccounts(@PathVariable Long accountId) {
        List<Transference> lastTransfers = transferenceService.getLastTransferredAccounts(accountId);
        return ResponseEntity.ok(lastTransfers);
    }
}

