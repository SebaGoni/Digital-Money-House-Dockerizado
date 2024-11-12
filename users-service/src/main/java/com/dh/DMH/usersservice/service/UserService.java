package com.dh.DMH.usersservice.service;

import com.dh.DMH.usersservice.dto.AccountCreationRequest;
import com.dh.DMH.usersservice.dto.AccountResponse;
import com.dh.DMH.usersservice.dto.UserDTO;
import com.dh.DMH.usersservice.entity.User;
import com.dh.DMH.usersservice.exception.*;
import com.dh.DMH.usersservice.security.JwtProvider;
import com.dh.DMH.usersservice.service.client.AccountClient;
import jakarta.ws.rs.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dh.DMH.usersservice.repository.UserRepository;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    @Value("${path.to.alias.file}")
    private String aliasFilePath;

    @Value("${path.to.cvu.file}")
    private String cvuFilePath;

    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountClient accountClient;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private EmailService emailService;

    public UserService(UserRepository userRepository, ResourceLoader resourceLoader) {
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
    }

    public Map<String, Object> handleUserRegistration(User user) throws IOException {
        User registeredUser = registerUser(user);
        String verificationCode = generateVerificationCode(registeredUser);
        emailService.sendVerificationEmail(registeredUser.getEmail(), verificationCode);
        return buildUserResponse(registeredUser);
    }

    private Map<String, Object> buildUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("dni", user.getDni());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("cvu", user.getCvu());
        response.put("alias", user.getAlias());
        return response;
    }

    @Transactional
    public User registerUser(User user) throws IOException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("El correo ya está en uso.");
        }
        String cvu = generateCvu();
        String alias = generateAlias();

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setCvu(cvu);
        user.setAlias(alias);
        user.setEmailVerified(false);

        User registeredUser = userRepository.save(user);

        AccountCreationRequest accountRequest = new AccountCreationRequest();
        accountRequest.setUserId(registeredUser.getId());
        accountRequest.setEmail(registeredUser.getEmail());
        accountRequest.setAlias(registeredUser.getAlias());
        accountRequest.setCvu(registeredUser.getCvu());
        accountRequest.setInitialBalance(BigDecimal.ZERO);

        AccountResponse accountResponse = accountClient.createAccount(accountRequest);

        registeredUser.setAccountId(accountResponse.getId());

        userRepository.save(registeredUser);

        return registeredUser;
    }

    /*private String generateCvu() throws IOException {
        Resource resource = new ClassPathResource("cvu.txt");
        List<String> cvus = new ArrayList<>(Files.readAllLines(resource.getFile().toPath()));
        Collections.shuffle(cvus);
        return cvus.get(0);
    }

    private String generateAlias() throws IOException {
        Resource resource = new ClassPathResource("alias.txt");
        List<String> aliases = new ArrayList<>(Files.readAllLines(resource.getFile().toPath()));
        Collections.shuffle(aliases);
        return aliases.get(0);
    }*/

    private String generateCvu() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cvu.txt");
        if (inputStream == null) {
            throw new FileNotFoundException("Archivo cvu.txt no encontrado en el classpath");
        }

        List<String> cvus = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.toList());
        Collections.shuffle(cvus); // Mezcla las líneas
        return cvus.get(0); // Devuelve el primer valor aleatorio
    }

    private String generateAlias() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("alias.txt");
        if (inputStream == null) {
            throw new FileNotFoundException("Archivo alias.txt no encontrado en el classpath");
        }

        List<String> aliases = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.toList());
        Collections.shuffle(aliases); // Mezcla las líneas
        return aliases.get(0); // Devuelve el primer valor aleatorio
    }


    public String generateVerificationCode(User user) {
        String code = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationCode(code);
        userRepository.save(user);
        return code;
    }

    public void verifyUserEmail(String email, String verificationCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (user.getVerificationCode() != null && user.getVerificationCode().equals(verificationCode)) {
            user.setEmailVerified(true);
            user.setVerificationCode(null);
            userRepository.save(user);
        } else {
            throw new InvalidVerificationCodeException("Invalid verification code or email.");
        }
    }

    public String authenticate(String email, String password) throws UserNotFoundException, IncorrectPasswordException, EmailNotVerifiedException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario inexistente"));
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Por favor verifica tu correo electrónico utilizando el código que te fue enviado.");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException("Contraseña incorrecta");
        }
        return jwtProvider.generateToken(user.getId(), user.getEmail());
    }


    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
        return convertToUserDTO(user);
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setCvu(user.getCvu());
        userDto.setAlias(user.getAlias());
        return userDto;
    }

    public UserDTO updateUser(Long id, UserDTO userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));

        user.setAlias(userDto.getAlias());

        User updatedUser = userRepository.save(user);

        return convertToUserDTO(updatedUser);
    }

    public void logoutUser(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            jwtProvider.invalidateToken(jwt);
        } else {
            throw new InvalidTokenException("Token no válido");
        }
    }

    public void processPasswordResetRequest(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        String token = jwtProvider.generateToken(user.getId(), user.getEmail());

        String link = "http://localhost:3000/reset-password?token=" + token;

        emailService.sendEmail(user.getEmail(), "Recuperación de contraseña",
                "Haz clic en el siguiente enlace para restablecer tu contraseña: " + link);
    }

    public void resetPassword(String token, String newPassword, String confirmPassword) {
        String email = jwtProvider.validateToken(token);
        if (email == null) {
            throw new InvalidTokenException("Token inválido");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IncorrectPasswordException("Las contraseñas no coinciden");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void updateAlias(Long id, String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new BadRequestException("El alias no puede estar vacío");
        }

        int updatedRows = userRepository.updateAlias(id, alias);
        if (updatedRows == 0) {
            throw new UserNotFoundException("Usuario no encontrado");
        }
    }
}


