package com.DMH.accountsservice.service;

import com.DMH.accountsservice.dto.TransferRequestDTO;
import com.DMH.accountsservice.dto.TransferenceDTO;
import com.DMH.accountsservice.entities.Account;
import com.DMH.accountsservice.entities.Activity;
import com.DMH.accountsservice.entities.Card;
import com.DMH.accountsservice.entities.Transference;
import com.DMH.accountsservice.exceptions.InsufficientFundsException;
import com.DMH.accountsservice.exceptions.ResourceNotFoundException;
import com.DMH.accountsservice.exceptions.UnauthorizedException;
import com.DMH.accountsservice.repository.AccountsRepository;
import com.DMH.accountsservice.repository.ActivityRepository;
import com.DMH.accountsservice.repository.CardRepository;
import com.DMH.accountsservice.repository.TransferenceRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferenceService {

    @Autowired
    private TransferenceRepository transferenceRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ActivityRepository activityRepository;

    public void registerTransferenceFromCards(Long accountId, TransferenceDTO transferenceDto) throws ResourceNotFoundException, UnauthorizedException {

        // Validar que la cuenta existe
        Account account = accountsRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Validar que la tarjeta existe
        Card card = cardRepository.findById(transferenceDto.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        // Crear la transferencia
        Transference transference = new Transference();
        transference.setAccountId(account.getId());
        transference.setCardId(card.getId());
        transference.setAmount(transferenceDto.getAmount());
        transference.setDate(LocalDateTime.now());
        transference.setType("deposit"); // Para depósitos
        transference.setRecipient(account.getCvu());

        // Guardar la transferencia
        transferenceRepository.save(transference);

        // Actualizar el balance de la cuenta usando BigDecimal
        BigDecimal currentBalance = account.getBalance();
        BigDecimal amount = transferenceDto.getAmount();

        // Sumamos el monto al balance actual
        BigDecimal newBalance = currentBalance.add(amount);

        // Actualizamos el balance en la cuenta
        account.setBalance(newBalance);

        // Guardar la cuenta con el nuevo balance
        accountsRepository.save(account);

        // Registrar la actividad
        Activity activity = new Activity();
        activity.setAccountId(accountId);
        activity.setType("deposit"); // Tipo de actividad
        activity.setAmount(amount); // Monto de la transferencia
        activity.setDescription("Depósito de " + amount + " realizado con la tarjeta " + card.getNumber()); // Descripción
        activity.setDate(LocalDateTime.now()); // Fecha de la actividad

        activityRepository.save(activity);
    }

    @Transactional
    public void makeTransferFromCash(Long accountId, TransferRequestDTO transferRequest) throws AccountNotFoundException {
        // Validar la existencia de la cuenta que envía el dinero
        Account senderAccount = accountsRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta inexistente"));

        // Validar fondos
        if (senderAccount.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new InsufficientFundsException("Fondos insuficientes");
        }

        // Buscar la cuenta del destinatario por alias o CVU
        Account recipientAccount = findRecipientAccount(transferRequest.getRecipient());

        // Actualizar balances
        senderAccount.setBalance(senderAccount.getBalance().subtract(transferRequest.getAmount())); // Resta del saldo del remitente
        recipientAccount.setBalance(recipientAccount.getBalance().add(transferRequest.getAmount())); // Suma al saldo del destinatario

        // Guardar los cambios en ambas cuentas
        accountsRepository.save(senderAccount);
        accountsRepository.save(recipientAccount);

        // Aquí puedes guardar la transferencia en la base de datos
        Transference transfer = new Transference();
        transfer.setAccountId(accountId); // Cuenta que envía
        transfer.setAmount(transferRequest.getAmount());
        transfer.setType("transfer-out"); // Para transferencias enviadas
        transfer.setRecipient(transferRequest.getRecipient());
        transferenceRepository.save(transfer);

        // Registrar la actividad para la cuenta que envía
        Activity senderActivity = new Activity();
        senderActivity.setAccountId(accountId);
        senderActivity.setType("transfer-out"); // Tipo de actividad
        senderActivity.setAmount(transferRequest.getAmount().negate()); // Monto de la transferencia (negado)
        senderActivity.setDescription(transferRequest.getRecipient()); // Descripción
        senderActivity.setDate(LocalDateTime.now());

        activityRepository.save(senderActivity);

        // Registrar la actividad para la cuenta que recibe
        Activity recipientActivity = new Activity();
        recipientActivity.setAccountId(recipientAccount.getId());
        recipientActivity.setType("transfer-in"); // Tipo de actividad
        recipientActivity.setAmount(transferRequest.getAmount()); // Monto de la transferencia
        recipientActivity.setDescription(senderAccount.getCvu()); // Descripción
        recipientActivity.setDate(LocalDateTime.now()); // Fecha de la actividad

        activityRepository.save(recipientActivity);
    }

    // Método para encontrar la cuenta del destinatario
    private Account findRecipientAccount(String recipientIdentifier) throws AccountNotFoundException {
        // Intenta encontrar la cuenta por alias
        Account recipientAccount = accountsRepository.findByAlias(recipientIdentifier);
        if (recipientAccount != null) {
            return recipientAccount;
        }

        // Si no se encuentra por alias, intenta encontrar por CVU
        recipientAccount = accountsRepository.findByCvu(recipientIdentifier);
        if (recipientAccount != null) {
            return recipientAccount;
        }

        // Si no se encuentra por alias ni por CVU, lanzar excepción
        throw new AccountNotFoundException("Cuenta destinataria inexistente");
    }

    // Método para obtener las últimas 5 cuentas a las que se transfirió dinero
    public List<Transference> getLastTransferredAccounts(Long accountId) {
        return transferenceRepository.findTop5ByAccountIdOrderByDateDesc(accountId);
    }

    public List<Activity> getRecentActivities(Long accountId) {
        return activityRepository.findTop5ByAccountIdOrderByDateDesc(accountId);
    }

}



