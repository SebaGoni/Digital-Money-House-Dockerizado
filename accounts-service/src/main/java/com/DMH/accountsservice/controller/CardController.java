package com.DMH.accountsservice.controller;

import com.DMH.accountsservice.dto.CardDTO;
import com.DMH.accountsservice.dto.CreateCardDTO;
import com.DMH.accountsservice.exceptions.CardAlreadyExistsException;
import com.DMH.accountsservice.exceptions.CardNotFoundException;
import com.DMH.accountsservice.exceptions.ResourceNotFoundException;
import com.DMH.accountsservice.service.CardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/accounts/{accountId}/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards(@PathVariable Long accountId) {
        List<CardDTO> cards = cardService.getCardsByAccountId(accountId);
        if (cards.isEmpty()) {
            return ResponseEntity.ok().body(cards);
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long accountId, @PathVariable Long cardId) throws ResourceNotFoundException {
        CardDTO card = cardService.getCardById(accountId, cardId);
        return ResponseEntity.ok(card);
    }

    @PostMapping
    public ResponseEntity<?> createCard(@PathVariable Long accountId, @Valid @RequestBody CreateCardDTO createCardDto, HttpServletRequest request) {
        try {
            // Extraer el token del header
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwtToken = token.replace("Bearer ", "");
            CardDTO newCard = cardService.createCard(accountId, createCardDto, jwtToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCard);
        } catch (CardAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            e.printStackTrace(); // Para depuración
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        try {
            cardService.deleteCard(accountId, cardId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Tarjeta eliminada exitosamente"));
        } catch (CardNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}

