package com.DMH.accountsservice.service;

import com.DMH.accountsservice.dto.CardDTO;
import com.DMH.accountsservice.dto.CreateCardDTO;
import com.DMH.accountsservice.entities.Account;
import com.DMH.accountsservice.entities.Activity;
import com.DMH.accountsservice.entities.Card;
import com.DMH.accountsservice.exceptions.CardAlreadyExistsException;
import com.DMH.accountsservice.exceptions.CardNotFoundException;
import com.DMH.accountsservice.exceptions.ResourceNotFoundException;
import com.DMH.accountsservice.repository.AccountsRepository;
import com.DMH.accountsservice.repository.ActivityRepository;
import com.DMH.accountsservice.repository.CardRepository;
import com.DMH.accountsservice.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    AccountsRepository accountsRepository;

    public List<CardDTO> getCardsByAccountId(Long accountId) {
        List<Card> cards = cardRepository.findByAccountId(accountId);
        return cards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CardDTO getCardById(Long accountId, Long cardId) throws ResourceNotFoundException {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        if (!card.getAccountId().equals(accountId)) {
            throw new AccessDeniedException("You do not have access to this card.");
        }

        return convertToDTO(card);
    }

    private CardDTO convertToDTO(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setNumber(card.getNumber());
        dto.setName(card.getName());
        dto.setExpiry(card.getExpiry());
        return dto;
    }

    // Método para agregar una tarjeta
    public CardDTO createCard(Long accountId, CreateCardDTO createCardDto, String jwtToken) throws CardAlreadyExistsException, ResourceNotFoundException {
        // Extraer el email del token
        String email = jwtAuthenticationFilter.extractEmailFromToken(jwtToken);
        if (email == null) {
            throw new ResourceNotFoundException("No se pudo obtener el email del token.");
        }

        // Buscar el accountId por email
        Account account = accountsRepository.findByEmail(email);
        if (account == null) {
            throw new ResourceNotFoundException("No se encontró ninguna cuenta asociada al email.");
        }
        Long accountIdFromToken = account.getId();

        // Comparar el accountId del token con el accountId del path variable
        if (!accountIdFromToken.equals(accountId)) {
            throw new ResourceNotFoundException("No tienes permiso para agregar una tarjeta a esta cuenta.");
        }

        // Verificar si la tarjeta ya está asociada a otra cuenta
        Optional<Card> existingCard = cardRepository.findByNumber(createCardDto.getNumber());
        if (existingCard.isPresent() && !existingCard.get().getAccountId().equals(accountId)) {
            throw new CardAlreadyExistsException("La tarjeta ya está asociada a otra cuenta.");
        }

        // Crear nueva tarjeta
        Card card = new Card();
        card.setAccountId(accountId);
        card.setNumber(createCardDto.getNumber());
        card.setName(createCardDto.getName());
        card.setExpiry(createCardDto.getExpiry());
        card.setCvc(createCardDto.getCvc());

        Card savedCard = cardRepository.save(card);

        return convertCardToDTO(savedCard);
    }


    private CardDTO convertCardToDTO(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setNumber(card.getNumber());
        dto.setName(card.getName());
        dto.setExpiry(card.getExpiry());
        return dto;
    }

    // Método para eliminar una tarjeta
    public void deleteCard(Long accountId, Long cardId) {
        // Verificar si la tarjeta existe y está asociada a la cuenta
        Optional<Card> cardOptional = cardRepository.findById(cardId);
        if (!cardOptional.isPresent() || !cardOptional.get().getAccountId().equals(accountId)) {
            throw new CardNotFoundException("La tarjeta no se encontró o no está asociada a esta cuenta.");
        }
        // Eliminar la tarjeta
        cardRepository.delete(cardOptional.get());

    }
}

