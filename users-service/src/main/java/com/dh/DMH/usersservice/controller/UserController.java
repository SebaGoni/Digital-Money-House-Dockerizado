package com.dh.DMH.usersservice.controller;

import com.dh.DMH.usersservice.dto.*;
import com.dh.DMH.usersservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dh.DMH.usersservice.service.UserService;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) throws IOException {
        Map<String, Object> response = userService.handleUserRegistration(user);
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully. Please check your email for the verification code.",
                "user", response));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUserEmail(@RequestBody VerificationRequest verificationRequest) {
        userService.verifyUserEmail(verificationRequest.getEmail(), verificationRequest.getVerificationCode());
        return ResponseEntity.ok("Email verified successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        String token = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String token) {
        userService.logoutUser(token);
        return ResponseEntity.ok("{\"message\": \"Logout exitoso\"}");
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        userService.processPasswordResetRequest(email);
        return ResponseEntity.ok("Correo enviado para la recuperación de contraseña");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String newPassword,
                                                @RequestParam String confirmPassword) {
        userService.resetPassword(token, newPassword, confirmPassword);
        return ResponseEntity.ok("Contraseña actualizada con éxito");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/update/alias/{id}")
    public ResponseEntity<?> updateAlias(@PathVariable Long id, @RequestBody UserAliasUpdateRequest request) {
        userService.updateAlias(id, request.getAlias());
        return ResponseEntity.ok("Alias actualizado exitosamente");
    }

    }


